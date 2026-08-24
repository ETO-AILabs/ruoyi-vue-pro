package cn.iocoder.yudao.module.match.service.match;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.match.controller.app.match.vo.AvatarProfileSaveReqVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.AvatarProfileVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.CandidateCardVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.MatchResultItemVO;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.match.enums.ErrorCodeConstants.*;

/**
 * MatchAI APP - 匹配 Service 实现（内存版）
 *
 * 说明：当前使用 ConcurrentHashMap 内存存储，便于前端联调；表结构 DDL 见
 * resources/sql/match.sql，后续替换为 MyBatis-Plus 的 Mapper 实现即可（DO/Mapper 骨架已建好）。
 */
@Service
@Validated
public class MatchServiceImpl implements MatchService {

    /**
     * AI 分身存储：userId -> 分身 Profile
     */
    private final Map<Long, AvatarProfileVO> avatarStore = new ConcurrentHashMap<>();
    /**
     * 候选人池：candidateId -> 候选人
     */
    private final Map<Long, Candidate> candidateStore = new ConcurrentHashMap<>();
    /**
     * 喜欢记录：userId -> 喜欢的 candidateId 集合
     */
    private final Map<Long, Set<Long>> likedStore = new ConcurrentHashMap<>();
    /**
     * 跳过记录：userId -> 跳过的 candidateId 集合
     */
    private final Map<Long, Set<Long>> passedStore = new ConcurrentHashMap<>();
    /**
     * 匹配结果：userId -> 匹配结果列表
     */
    private final Map<Long, List<MatchResultItemVO>> matchStore = new ConcurrentHashMap<>();

    private final AtomicLong candidateIdGenerator = new AtomicLong(1000);

    @PostConstruct
    public void init() {
        initCandidates();
    }

    @Override
    public AvatarProfileVO getAvatar(Long userId) {
        return avatarStore.get(userId);
    }

    @Override
    public Long saveAvatar(Long userId, AvatarProfileSaveReqVO reqVO) {
        AvatarProfileVO profile = new AvatarProfileVO();
        profile.setSchoolCode(reqVO.getSchoolCode());
        profile.setSchoolName(reqVO.getSchoolName());
        profile.setCampus(reqVO.getCampus());
        profile.setGrade(reqVO.getGrade());
        profile.setMajorCategory(reqVO.getMajorCategory());
        profile.setMajor(reqVO.getMajor());
        profile.setTags(reqVO.getTags());
        profile.setInterests(reqVO.getInterests());
        profile.setMbti(reqVO.getMbti());
        profile.setValues(reqVO.getValues());
        profile.setSelfIntro(reqVO.getSelfIntro());
        profile.setRecentStatus(reqVO.getRecentStatus());
        avatarStore.put(userId, profile);
        return userId;
    }

    @Override
    public PageResult<CandidateCardVO> getCandidates(Long userId, PageParam pageParam) {
        AvatarProfileVO me = avatarStore.get(userId);
        if (me == null) {
            throw exception(MATCH_AVATAR_NOT_EXISTS);
        }
        Set<Long> liked = likedStore.getOrDefault(userId, Collections.emptySet());
        Set<Long> passed = passedStore.getOrDefault(userId, Collections.emptySet());
        // 过滤已喜欢 / 已跳过，然后计算匹配度并降序排序
        List<CandidateCardVO> cards = candidateStore.values().stream()
                .filter(c -> !liked.contains(c.getId()))
                .filter(c -> !passed.contains(c.getId()))
                .map(c -> buildCandidateCard(me, c))
                .sorted(Comparator.comparingInt(CandidateCardVO::getMatchScore).reversed())
                .collect(Collectors.toList());
        return pageResult(cards, pageParam);
    }

    @Override
    public Boolean like(Long userId, Long candidateId) {
        Candidate candidate = candidateStore.get(candidateId);
        if (candidate == null) {
            throw exception(MATCH_CANDIDATE_NOT_EXISTS);
        }
        Set<Long> liked = likedStore.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet());
        if (!liked.add(candidateId)) {
            throw exception(MATCH_ALREADY_LIKED);
        }
        // 模拟双向喜欢：候选人若"已喜欢你"，则产生匹配结果
        if (Boolean.TRUE.equals(candidate.getLikedMe())) {
            MatchResultItemVO item = new MatchResultItemVO();
            item.setId(candidate.getId());
            item.setAvatar(candidate.getAvatar());
            item.setNickname(candidate.getNickname());
            item.setAge(candidate.getAge());
            item.setSchoolName(candidate.getProfile().getSchoolName());
            item.setGrade(candidate.getProfile().getGrade());
            item.setMajorCategory(candidate.getProfile().getMajorCategory());
            item.setTags(candidate.getTags());
            item.setMbti(candidate.getProfile().getMbti());
            item.setSelfIntro(candidate.getSelfIntro());
            item.setMatchScore(100);
            item.setMatchReasons(Collections.singletonList("互相喜欢"));
            item.setMatchedAt(System.currentTimeMillis());
            item.setLiked(Boolean.TRUE);
            matchStore.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean pass(Long userId, Long candidateId) {
        if (!candidateStore.containsKey(candidateId)) {
            throw exception(MATCH_CANDIDATE_NOT_EXISTS);
        }
        Set<Long> passed = passedStore.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet());
        if (!passed.add(candidateId)) {
            throw exception(MATCH_ALREADY_PASSED);
        }
        return Boolean.TRUE;
    }

    @Override
    public PageResult<MatchResultItemVO> getResults(Long userId, PageParam pageParam) {
        List<MatchResultItemVO> list = matchStore.getOrDefault(userId, Collections.emptyList());
        // 按匹配时间倒序
        List<MatchResultItemVO> sorted = list.stream()
                .sorted(Comparator.comparingLong(MatchResultItemVO::getMatchedAt).reversed())
                .collect(Collectors.toList());
        return pageResult(sorted, pageParam);
    }

    // ========== 私有方法 ==========

    /**
     * 基于"兴趣 / 性格 / 专业 / 学校 / 三观"五维打分，生成候选人卡片。
     */
    private CandidateCardVO buildCandidateCard(AvatarProfileVO me, Candidate c) {
        AvatarProfileVO p = c.getProfile();
        int score = 30; // 基础分
        List<String> reasons = new ArrayList<>();

        // 1. 同校
        if (Objects.equals(me.getSchoolCode(), p.getSchoolCode())) {
            score += 10;
            reasons.add("同校之缘");
        }
        // 2. 专业方向
        if (me.getMajorCategory() != null && me.getMajorCategory().equals(p.getMajorCategory())) {
            score += 15;
            reasons.add("专业方向契合");
        }
        if (me.getMajor() != null && me.getMajor().equals(p.getMajor())) {
            score += 15;
            reasons.add("同专业");
        }
        // 3. 兴趣重叠
        Set<String> myInterests = toSet(me.getInterests());
        Set<String> cInterests = toSet(p.getInterests());
        myInterests.retainAll(cInterests);
        if (!myInterests.isEmpty()) {
            score += Math.min(myInterests.size(), 3) * 15;
            reasons.add("兴趣契合");
        }
        // 4. 三观一致
        Set<String> myValues = toSet(me.getValues());
        Set<String> cValues = toSet(p.getValues());
        myValues.retainAll(cValues);
        if (!myValues.isEmpty()) {
            score += Math.min(myValues.size(), 2) * 10;
            reasons.add("三观一致");
        }
        // 5. 性格（MBTI）：对轴互补加分，完全相同给少量相似分
        int oppositeAxes = countOppositeAxes(me.getMbti(), p.getMbti());
        if (oppositeAxes >= 2) {
            score += 15;
            reasons.add("性格互补");
        } else if (oppositeAxes == 0) {
            score += 5;
            reasons.add("性格相似");
        }

        CandidateCardVO card = new CandidateCardVO();
        card.setId(c.getId());
        card.setAvatar(c.getAvatar());
        card.setNickname(c.getNickname());
        card.setAge(c.getAge());
        card.setSchoolName(p.getSchoolName());
        card.setGrade(p.getGrade());
        card.setMajorCategory(p.getMajorCategory());
        card.setTags(c.getTags());
        card.setMbti(p.getMbti());
        card.setSelfIntro(c.getSelfIntro());
        card.setMatchScore(Math.min(100, score));
        card.setMatchReasons(reasons.size() > 3 ? reasons.subList(0, 3) : reasons);
        return card;
    }

    private Set<String> toSet(List<String> list) {
        if (list == null) {
            return new HashSet<>();
        }
        return new HashSet<>(list);
    }

    /**
     * MBTI 四轴：E-I / N-S / F-T / J-P。返回对轴相反的数量。
     */
    private int countOppositeAxes(String mbti1, String mbti2) {
        if (mbti1 == null || mbti2 == null || mbti1.length() < 4 || mbti2.length() < 4) {
            return -1;
        }
        char[][] axes = {{'E', 'I'}, {'N', 'S'}, {'F', 'T'}, {'J', 'P'}};
        int count = 0;
        for (int i = 0; i < 4; i++) {
            char a = mbti1.charAt(i);
            char b = mbti2.charAt(i);
            char x = axes[i][0];
            char y = axes[i][1];
            if ((a == x && b == y) || (a == y && b == x)) {
                count++;
            }
        }
        return count;
    }

    private <T> PageResult<T> pageResult(List<T> list, PageParam pageParam) {
        int pageNo = pageParam.getPageNo();
        int pageSize = pageParam.getPageSize();
        long total = list.size();
        if (list.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        int from = (pageNo - 1) * pageSize;
        if (from >= list.size()) {
            return new PageResult<>(Collections.emptyList(), total);
        }
        int to = Math.min(from + pageSize, list.size());
        return new PageResult<>(list.subList(from, to), total);
    }

    // ========== 种子数据（演示用） ==========

    private void initCandidates() {
        addCandidate("林一", 21, "浙江大学", "本科三年级", "理工科", "计算机科学与技术",
                Arrays.asList("算法", "健身"), Arrays.asList("桌游 / 剧本杀", "科技 / 极客"),
                Arrays.asList("真诚", "成长"), "INTJ", "ACMer，喜欢健身和桌游", true);
        addCandidate("陈默", 22, "浙江大学", "本科四年级", "理工科", "软件工程",
                Arrays.asList("摄影", "篮球"), Arrays.asList("科技 / 极客", "运动"),
                Arrays.asList("自由", "成长"), "ISTP", "相机不离手的程序员", true);
        addCandidate("苏晚", 20, "浙江大学", "本科二年级", "人文社科", "新闻学",
                Arrays.asList("写作", "旅行"), Arrays.asList("旅行", "电影 / 展览"),
                Arrays.asList("真诚", "热爱生活"), "ENFP", "想记录每一个有趣的人", false);
        addCandidate("周航", 23, "浙江大学", "研究生一年级", "理工科", "电子科学与技术",
                Arrays.asList("羽毛球", "音乐"), Arrays.asList("音乐", "运动"),
                Arrays.asList("进取", "自律"), "ESTJ", "实验室卷王，球场主力", false);
        addCandidate("许晴", 21, "中国美术学院", "本科三年级", "艺术", "视觉传达",
                Arrays.asList("绘画", "设计"), Arrays.asList("电影 / 展览", "桌游 / 剧本杀"),
                Arrays.asList("美感", "自由"), "INFJ", "正在做毕业设计的设计师", true);
        addCandidate("李想", 22, "复旦大学", "本科三年级", "理工科", "人工智能",
                Arrays.asList("AI", "骑行"), Arrays.asList("科技 / 极客", "运动"),
                Arrays.asList("创新", "成长"), "ENTP", "AI 炼丹师，业余骑行者", false);
        addCandidate("王悦", 20, "浙江大学", "本科二年级", "经管", "工商管理",
                Arrays.asList("舞蹈", "烘焙"), Arrays.asList("美食", "音乐"),
                Arrays.asList("乐观", "温暖"), "ESFJ", "社团活动组织者", false);
        addCandidate("郑南", 24, "浙江大学", "研究生二年级", "理工科", "控制科学与工程",
                Arrays.asList("游泳", "读书"), Arrays.asList("读书", "旅行"),
                Arrays.asList("内省", "踏实"), "ISFJ", "安静的读书人", true);
    }

    private void addCandidate(String nickname, Integer age, String schoolName, String grade,
                              String majorCategory, String major, List<String> tags,
                              List<String> interests, List<String> values, String mbti,
                              String selfIntro, boolean likedMe) {
        AvatarProfileVO profile = new AvatarProfileVO();
        profile.setSchoolCode("zju"); // 简化：种子数据均视为同校，便于演示打分
        profile.setSchoolName(schoolName);
        profile.setGrade(grade);
        profile.setMajorCategory(majorCategory);
        profile.setMajor(major);
        profile.setInterests(interests);
        profile.setValues(values);
        profile.setMbti(mbti);
        Candidate candidate = new Candidate();
        candidate.setId(candidateIdGenerator.incrementAndGet());
        candidate.setNickname(nickname);
        candidate.setAge(age);
        candidate.setTags(tags);
        candidate.setSelfIntro(selfIntro);
        candidate.setLikedMe(likedMe);
        candidate.setProfile(profile);
        candidateStore.put(candidate.getId(), candidate);
    }

    /**
     * 内存版候选人模型
     */
    @Data
    public static class Candidate {
        private Long id;
        private String avatar;
        private String nickname;
        private Integer age;
        private List<String> tags;
        private String selfIntro;
        /**
         * 模拟候选人是否已"喜欢你"，用于触发双向匹配
         */
        private Boolean likedMe;
        private AvatarProfileVO profile;
    }

}
