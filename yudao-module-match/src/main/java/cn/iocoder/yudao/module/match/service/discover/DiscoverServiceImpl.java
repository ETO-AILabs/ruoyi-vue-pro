package cn.iocoder.yudao.module.match.service.discover;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.match.controller.app.discover.vo.SchoolVO;
import cn.iocoder.yudao.module.match.controller.app.discover.vo.TribeVO;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MatchAI APP - 校园发现 Service 实现（内存版）
 */
@Service
@Validated
public class DiscoverServiceImpl implements DiscoverService {

    private final List<TribeVO> tribeStore = new CopyOnWriteArrayList<>();
    private final List<SchoolVO> schoolStore = new ArrayList<>();

    public DiscoverServiceImpl() {
        initTribes();
        initSchools();
    }

    @Override
    public PageResult<TribeVO> getTribes(PageParam pageParam) {
        int pageNo = pageParam.getPageNo();
        int pageSize = pageParam.getPageSize();
        long total = tribeStore.size();
        if (tribeStore.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        int from = (pageNo - 1) * pageSize;
        if (from >= tribeStore.size()) {
            return new PageResult<>(Collections.emptyList(), total);
        }
        int to = Math.min(from + pageSize, tribeStore.size());
        return new PageResult<>(new ArrayList<>(tribeStore.subList(from, to)), total);
    }

    @Override
    public List<SchoolVO> getSchools() {
        return new ArrayList<>(schoolStore);
    }

    // ========== 种子数据 ==========

    private void initTribes() {
        tribeStore.add(buildTribe(1L, "夜跑搭子营", "", 128, "每周末晚绕西湖夜跑，配速随意，安全第一",
                Arrays.asList("运动", "跑步")));
        tribeStore.add(buildTribe(2L, "桌游推理社", "", 86, "剧本杀、狼人杀、德式桌游，周末组局",
                Arrays.asList("桌游 / 剧本杀", "社交")));
        tribeStore.add(buildTribe(3L, "极客实验室", "", 210, "AI、开源、硬件黑客，一起造点东西",
                Arrays.asList("科技 / 极客", "编程")));
        tribeStore.add(buildTribe(4L, "电影与展览", "", 54, "每周一部片或一个展，看完一起聊",
                Arrays.asList("电影 / 展览", "艺术")));
        tribeStore.add(buildTribe(5L, "羽毛球俱乐部", "", 302, "校队退役选手带练，菜鸟友好",
                Arrays.asList("运动", "羽毛球")));
        tribeStore.add(buildTribe(6L, "摄影同好会", "", 77, "扫街、人像、风光，器材不是重点",
                Arrays.asList("摄影", "旅行")));
    }

    private TribeVO buildTribe(Long id, String name, String cover, Integer memberCount,
                               String description, List<String> tags) {
        TribeVO tribe = new TribeVO();
        tribe.setId(id);
        tribe.setName(name);
        tribe.setCover(cover);
        tribe.setMemberCount(memberCount);
        tribe.setDescription(description);
        tribe.setTags(tags);
        return tribe;
    }

    private void initSchools() {
        schoolStore.add(buildSchool("zju", "浙江大学", "杭州"));
        schoolStore.add(buildSchool("zjgsu", "浙江工商大学", "杭州"));
        schoolStore.add(buildSchool("zjut", "浙江工业大学", "杭州"));
        schoolStore.add(buildSchool("hzsu", "杭州师范大学", "杭州"));
        schoolStore.add(buildSchool("zjcm", "中国美术学院", "杭州"));
        schoolStore.add(buildSchool("fudan", "复旦大学", "上海"));
        schoolStore.add(buildSchool("sjtu", "上海交通大学", "上海"));
        schoolStore.add(buildSchool("tju", "同济大学", "上海"));
        schoolStore.add(buildSchool("pku", "北京大学", "北京"));
        schoolStore.add(buildSchool("thu", "清华大学", "北京"));
    }

    private SchoolVO buildSchool(String code, String name, String city) {
        SchoolVO school = new SchoolVO();
        school.setCode(code);
        school.setName(name);
        school.setCity(city);
        return school;
    }

}
