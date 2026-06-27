package cn.iocoder.yudao.module.member.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchDetailListReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchResultDetailVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchSceneRespVO;
import cn.iocoder.yudao.module.member.convert.social.SocialConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskResultDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberUserTagDO;
import cn.iocoder.yudao.module.member.dal.dataobject.tag.MemberTagDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberMatchTaskMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberMatchTaskResultMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberSceneMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberUserTagMapper;
import cn.iocoder.yudao.module.member.dal.mysql.tag.MemberTagMapper;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.enums.ErrorCodeConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
@Validated
public class MatchServiceImpl implements MatchService {

    /**
     * 匹配冷却时间（小时）
     */
    private static final int MATCH_COOLDOWN_HOURS = 24;

    /**
     * 匹配结果可见时间（小时）
     */
    private static final int MATCH_RESULT_VISIBLE_HOURS = 12;

    /**
     * 每个匹配结果展示的标签上限
     */
    private static final int MAX_TAGS_PER_RESULT = 5;

    @Resource
    private MemberMatchTaskMapper memberMatchTaskMapper;
    @Resource
    private MemberMatchTaskResultMapper memberMatchTaskResultMapper;
    @Resource
    private MemberUserTagMapper memberUserTagMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberSceneMapper memberSceneMapper;
    @Resource
    private MemberTagMapper memberTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMatchTask(Long userId, AppSocialMatchCreateReqVO reqVO) {
        // 1. 校验同场景是否有进行中的任务
        MemberMatchTaskDO pendingTask = memberMatchTaskMapper
                .selectByUserIdAndSceneIdAndStatusIn(userId, reqVO.getSceneId(),
                        Arrays.asList(0, 1));
        if (pendingTask != null) {
            throw exception(ErrorCodeConstants.MATCH_TASK_EXISTS);
        }

        // 2. 校验冷却时间
        MemberMatchTaskDO latestTask = memberMatchTaskMapper
                .selectLatestByUserIdAndSceneId(userId, reqVO.getSceneId());
        if (latestTask != null) {
            LocalDateTime cooldownTime = latestTask.getCreateTime()
                    .plusHours(MATCH_COOLDOWN_HOURS);
            if (LocalDateTime.now().isBefore(cooldownTime)) {
                throw exception(ErrorCodeConstants.MATCH_COOLDOWN, MATCH_COOLDOWN_HOURS);
            }
        }

        // 3. 保存标签（覆盖式，按 section 分组）
        if (reqVO.getSectionTags() != null && !reqVO.getSectionTags().isEmpty()) {
            for (Map.Entry<String, List<Long>> entry : reqVO.getSectionTags().entrySet()) {
                String source = entry.getKey();
                List<Long> tagIds = entry.getValue();
                memberUserTagMapper.delete(new LambdaQueryWrapperX<MemberUserTagDO>()
                        .eq(MemberUserTagDO::getUserId, userId)
                        .eq(MemberUserTagDO::getSource, source));
                if (CollUtil.isNotEmpty(tagIds)) {
                    List<MemberUserTagDO> inserts = tagIds.stream()
                            .filter(java.util.Objects::nonNull)
                            .distinct()
                            .map(tagId -> MemberUserTagDO.builder()
                                    .userId(userId)
                                    .tagId(tagId)
                                    .source(source)
                                    .build())
                            .collect(java.util.stream.Collectors.toList());
                    if (CollUtil.isNotEmpty(inserts)) {
                        for (MemberUserTagDO item : inserts) {
                            try {
                                memberUserTagMapper.insert(item);
                            } catch (org.springframework.dao.DuplicateKeyException e) {
                                // 已存在则跳过
                            }
                        }
                    }
                }
            }
        }

        // 4. 如有微信号或常驻地，同步到用户表
        if (reqVO.getExtraFields() != null) {
            String wechat = (String) reqVO.getExtraFields().get("wechat");
            String residence = (String) reqVO.getExtraFields().get("residence");
            if (StrUtil.isNotBlank(wechat) || StrUtil.isNotBlank(residence)) {
                MemberUserDO.MemberUserDOBuilder builder = MemberUserDO.builder().id(userId);
                if (StrUtil.isNotBlank(wechat)) builder.wechat(wechat);
                if (StrUtil.isNotBlank(residence)) builder.residence(residence);
                memberUserMapper.updateById(builder.build());
            }
        }

        // 5. 构造 matchConfig
        String matchConfig = null;
        if (reqVO.getExtraFields() != null) {
            Map<String, Object> config = new HashMap<>();
            config.put("extraFields", reqVO.getExtraFields());
            matchConfig = JSONUtil.toJsonStr(config);
        }

        // 6. 创建匹配任务
        MemberMatchTaskDO task = MemberMatchTaskDO.builder()
                .userId(userId)
                .sceneId(reqVO.getSceneId())
                .matchGoal(reqVO.getMatchGoal())
                .matchRemark(reqVO.getMatchRemark())
                .matchConfig(matchConfig)
                .matchStatus(0)
                .build();
        memberMatchTaskMapper.insert(task);
        return task.getId();
    }

    @Override
    public PageResult<MemberMatchTaskDO> getMatchTaskPage(Long userId, AppSocialMatchPageReqVO reqVO) {
        return memberMatchTaskMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<MemberMatchTaskDO>()
                        .eq(MemberMatchTaskDO::getUserId, userId)
                        .orderByDesc(MemberMatchTaskDO::getCreateTime));
    }

    @Override
    public AppSocialMatchSceneRespVO getMatchSceneData(Long userId, Long sceneId) {
        MemberMatchTaskDO latest = memberMatchTaskMapper
                .selectLatestByUserIdAndSceneId(userId, sceneId);

        if (latest == null) {
            return new AppSocialMatchSceneRespVO(0, null);
        }

        LocalDateTime now = LocalDateTime.now();

        if (latest.getMatchStatus() == 0 || latest.getMatchStatus() == 1) {
            return new AppSocialMatchSceneRespVO(1, null);
        }

        if (latest.getMatchStatus() == 3
                && latest.getCreateTime().plusHours(MATCH_RESULT_VISIBLE_HOURS).isAfter(now)) {
            List<MemberMatchTaskResultDO> results = memberMatchTaskResultMapper
                    .selectListByTaskId(latest.getId());
            return new AppSocialMatchSceneRespVO(2,
                    SocialConvert.INSTANCE.convertMatchResultList(results));
        }

        if (latest.getCreateTime().plusHours(MATCH_COOLDOWN_HOURS).isAfter(now)) {
            return new AppSocialMatchSceneRespVO(3, null);
        }

        return new AppSocialMatchSceneRespVO(0, null);
    }

    @Override
    public List<AppSocialMatchResultDetailVO> getMatchResultList(Long userId, AppSocialMatchDetailListReqVO reqVO) {
        // 1. 拉取该用户所有成功(3)状态的任务（在12h可见窗口内），未失败也允许展示
        List<MemberMatchTaskDO> tasks = memberMatchTaskMapper.selectListByUserId(userId);
        if (CollUtil.isEmpty(tasks)) {
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        List<MemberMatchTaskDO> validTasks = tasks.stream()
                .filter(t -> t.getMatchStatus() == null || t.getMatchStatus() == 3
                        || t.getCreateTime().plusHours(MATCH_RESULT_VISIBLE_HOURS).isAfter(now))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(validTasks)) {
            return Collections.emptyList();
        }

        // 2. 拉取所有 result
        List<Long> taskIds = validTasks.stream().map(MemberMatchTaskDO::getId).collect(Collectors.toList());
        List<MemberMatchTaskResultDO> results = memberMatchTaskResultMapper.selectListByTaskIdIn(taskIds);
        if (CollUtil.isEmpty(results)) {
            return Collections.emptyList();
        }

        // 3. 过滤：仅未添加 / 场景编码匹配
        if (Boolean.TRUE.equals(reqVO.getOnlyUnadded())) {
            results = results.stream()
                    .filter(r -> r.getIsAdded() == null || r.getIsAdded() == 0)
                    .collect(Collectors.toList());
        }

        // 4. 加载场景元信息（sceneId -> MemberSceneDO）
        Map<Long, MemberSceneDO> sceneMap = memberSceneMapper.selectList().stream()
                .collect(Collectors.toMap(MemberSceneDO::getId, Function.identity(), (a, b) -> a));
        Map<Long, MemberMatchTaskDO> taskMap = validTasks.stream()
                .collect(Collectors.toMap(MemberMatchTaskDO::getId, Function.identity(), (a, b) -> a));

        // 5. 过滤场景
        if (StrUtil.isNotBlank(reqVO.getSceneCode())) {
            String code = reqVO.getSceneCode();
            results = results.stream().filter(r -> {
                MemberMatchTaskDO t = taskMap.get(r.getTaskId());
                if (t == null) return false;
                MemberSceneDO scene = sceneMap.get(t.getSceneId());
                return scene != null && code.equals(scene.getSceneCode());
            }).collect(Collectors.toList());
        }
        if (CollUtil.isEmpty(results)) {
            return Collections.emptyList();
        }

        // 6. 加载被匹配用户信息
        List<Long> matchedUserIds = results.stream()
                .map(MemberMatchTaskResultDO::getMatchedUserId).distinct().collect(Collectors.toList());
        Map<Long, MemberUserDO> userMap = memberUserMapper.selectBatchIds(matchedUserIds).stream()
                .collect(Collectors.toMap(MemberUserDO::getId, Function.identity(), (a, b) -> a));

        // 7. 批量加载标签：被匹配用户的所有标签（按 source = scene 拼前缀过滤）
        Map<Long, List<Long>> userTagIds = new HashMap<>();
        for (Long matchedUid : matchedUserIds) {
            List<MemberUserTagDO> tags = memberUserTagMapper.selectListByUserId(matchedUid);
            userTagIds.put(matchedUid, tags.stream().map(MemberUserTagDO::getTagId).collect(Collectors.toList()));
        }
        List<Long> allTagIds = userTagIds.values().stream()
                .flatMap(List::stream).distinct().collect(Collectors.toList());
        Map<Long, String> tagNameMap = CollUtil.isEmpty(allTagIds)
                ? Collections.emptyMap()
                : memberTagMapper.selectBatchIds(allTagIds).stream()
                        .collect(Collectors.toMap(MemberTagDO::getId, MemberTagDO::getTagName, (a, b) -> a));

        // 8. 装配 VO
        return results.stream().map(r -> {
            MemberMatchTaskDO task = taskMap.get(r.getTaskId());
            MemberSceneDO scene = task == null ? null : sceneMap.get(task.getSceneId());
            MemberUserDO user = userMap.get(r.getMatchedUserId());
            return buildDetailVO(r, task, scene, user, userTagIds, tagNameMap, false);
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public AppSocialMatchResultDetailVO getMatchResultDetail(Long userId, Long resultId) {
        MemberMatchTaskResultDO result = memberMatchTaskResultMapper.selectById(resultId);
        if (result == null) {
            throw exception(ErrorCodeConstants.MATCH_RESULT_NOT_FOUND);
        }
        // 鉴权：必须是当前用户任务的结果
        MemberMatchTaskDO task = memberMatchTaskMapper.selectById(result.getTaskId());
        if (task == null || !userId.equals(task.getUserId())) {
            throw exception(ErrorCodeConstants.MATCH_RESULT_NOT_FOUND);
        }
        MemberSceneDO scene = memberSceneMapper.selectById(task.getSceneId());
        MemberUserDO user = memberUserMapper.selectById(result.getMatchedUserId());
        Map<Long, List<Long>> userTagIds = new HashMap<>();
        List<Long> tagIds = memberUserTagMapper.selectListByUserId(result.getMatchedUserId())
                .stream().map(MemberUserTagDO::getTagId).collect(Collectors.toList());
        userTagIds.put(result.getMatchedUserId(), tagIds);
        Map<Long, String> tagNameMap = CollUtil.isEmpty(tagIds)
                ? Collections.emptyMap()
                : memberTagMapper.selectBatchIds(tagIds).stream()
                        .collect(Collectors.toMap(MemberTagDO::getId, MemberTagDO::getTagName, (a, b) -> a));
        AppSocialMatchResultDetailVO vo = buildDetailVO(result, task, scene, user, userTagIds, tagNameMap, true);
        if (vo == null) {
            throw exception(ErrorCodeConstants.MATCH_RESULT_NOT_FOUND);
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMatchResultAdded(Long userId, Long resultId) {
        MemberMatchTaskResultDO result = memberMatchTaskResultMapper.selectById(resultId);
        if (result == null) {
            throw exception(ErrorCodeConstants.MATCH_RESULT_NOT_FOUND);
        }
        MemberMatchTaskDO task = memberMatchTaskMapper.selectById(result.getTaskId());
        if (task == null || !userId.equals(task.getUserId())) {
            throw exception(ErrorCodeConstants.MATCH_RESULT_NOT_FOUND);
        }
        memberMatchTaskResultMapper.updateAdded(resultId);
    }

    // ========== 私有方法 ==========

    private AppSocialMatchResultDetailVO buildDetailVO(MemberMatchTaskResultDO result,
                                                       MemberMatchTaskDO task,
                                                       MemberSceneDO scene,
                                                       MemberUserDO user,
                                                       Map<Long, List<Long>> userTagIds,
                                                       Map<Long, String> tagNameMap,
                                                       boolean withWechat) {
        if (user == null) {
            return null;
        }
        AppSocialMatchResultDetailVO vo = new AppSocialMatchResultDetailVO();
        vo.setResultId(result.getId());
        vo.setTaskId(result.getTaskId());
        if (task != null) vo.setSceneId(task.getSceneId());
        if (scene != null) {
            vo.setSceneCode(scene.getSceneCode());
            vo.setSceneName(scene.getSceneName());
        }
        vo.setMatchedUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setSex(user.getSex());
        vo.setAge(calcAge(user.getBirthday()));
        vo.setCity(StrUtil.blankToDefault(user.getResidence(), user.getLocation()));
        vo.setSchool(extractSchoolName(user));
        vo.setProfession(user.getProfession());
        vo.setMbti(user.getMbti());
        vo.setHeight(user.getHeight());
        vo.setUserDesc(user.getUserDesc());
        if (withWechat) {
            vo.setWechatId(StrUtil.blankToDefault(user.getWechat(), "wx_user_" + user.getId()));
        }
        vo.setMatchScore(result.getMatchScore() == null ? null
                : BigDecimal.valueOf(result.getMatchScore()).setScale(1, java.math.RoundingMode.HALF_UP));
        vo.setAiReason(StrUtil.blankToDefault(result.getAiReason(), "你们在多个维度都很契合"));
        vo.setChatTip(StrUtil.blankToDefault(result.getChatTip(), "可以从共同兴趣切入聊聊"));
        vo.setAdded(result.getIsAdded() != null && result.getIsAdded() == 1);
        vo.setAddedTime(result.getAddedTime());
        vo.setFinishTime(task == null ? null : task.getFinishTime());

        // 标签
        List<Long> tagIdList = userTagIds.getOrDefault(user.getId(), Collections.emptyList());
        List<String> tagNames = tagIdList.stream()
                .map(tagNameMap::get)
                .filter(StrUtil::isNotBlank)
                .limit(MAX_TAGS_PER_RESULT)
                .collect(Collectors.toList());
        vo.setTags(tagNames);

        // 场景扩展字段
        parseExtension(result.getExtension(), vo);

        return vo;
    }

    private void parseExtension(String extensionJson, AppSocialMatchResultDetailVO vo) {
        if (StrUtil.isBlank(extensionJson)) {
            return;
        }
        try {
            Map<String, Object> map = new ObjectMapper().readValue(extensionJson, new TypeReference<Map<String, Object>>() {});
            if (map.get("currentStatus") != null) vo.setCurrentStatus(String.valueOf(map.get("currentStatus")));
            if (map.get("teachSkill") != null) vo.setTeachSkill(String.valueOf(map.get("teachSkill")));
            if (map.get("wantSkill") != null) vo.setWantSkill(String.valueOf(map.get("wantSkill")));
            if (map.get("offer") != null) vo.setOffer(String.valueOf(map.get("offer")));
            if (map.get("want") != null) vo.setWant(String.valueOf(map.get("want")));
            if (map.get("conditionValue") != null) vo.setConditionValue(String.valueOf(map.get("conditionValue")));
            if (map.get("price") != null) {
                try { vo.setPrice(new BigDecimal(String.valueOf(map.get("price")))); } catch (Exception ignore) {}
            }
            if (map.get("photos") instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> raw = (List<Object>) map.get("photos");
                vo.setPhotos(raw.stream().map(String::valueOf).collect(Collectors.toList()));
            }
        } catch (Exception ignore) {
            // 静默失败
        }
    }

    private Integer calcAge(LocalDateTime birthday) {
        if (birthday == null) return null;
        return Period.between(birthday.toLocalDate(), LocalDate.now()).getYears();
    }

    private String extractSchoolName(MemberUserDO user) {
        if (StrUtil.isBlank(user.getMark())) {
            return null;
        }
        // mark 字段约定可能存了学校名称，简短提取；保守取原值
        return user.getMark();
    }
}
