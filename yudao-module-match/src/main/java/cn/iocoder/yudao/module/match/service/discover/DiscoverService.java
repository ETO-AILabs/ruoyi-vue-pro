package cn.iocoder.yudao.module.match.service.discover;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.match.controller.app.discover.vo.SchoolVO;
import cn.iocoder.yudao.module.match.controller.app.discover.vo.TribeVO;

import java.util.List;

/**
 * MatchAI APP - 校园发现 Service 接口
 *
 * 当前为内存 Map 实现（见 {@link DiscoverServiceImpl}），后续可切换为
 * yudao-module-match/dal/mysql 下 Mapper 的数据库实现。
 */
public interface DiscoverService {

    /**
     * 获得部落列表（分页）
     *
     * @param pageParam 分页参数
     * @return 部落分页
     */
    PageResult<TribeVO> getTribes(PageParam pageParam);

    /**
     * 获得学校列表（onboarding 学校选择用）
     *
     * @return 学校列表
     */
    List<SchoolVO> getSchools();

}
