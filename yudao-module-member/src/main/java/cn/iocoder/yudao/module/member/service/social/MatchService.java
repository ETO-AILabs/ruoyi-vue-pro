package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchDetailListReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchResultDetailVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchSceneRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskDO;

import java.util.List;

public interface MatchService {

    /**
     * 创建匹配任务
     */
    Long createMatchTask(Long userId, AppSocialMatchCreateReqVO reqVO);

    /**
     * 分页查询用户的匹配任务
     */
    PageResult<MemberMatchTaskDO> getMatchTaskPage(Long userId, AppSocialMatchPageReqVO reqVO);

    /**
     * 查询指定场景的最近匹配状态 + 有效匹配结果
     */
    AppSocialMatchSceneRespVO getMatchSceneData(Long userId, Long sceneId);

    /**
     * 查询当前用户所有未读/已读的有效匹配结果（首页 Aurora 列表）
     */
    List<AppSocialMatchResultDetailVO> getMatchResultList(Long userId, AppSocialMatchDetailListReqVO reqVO);

    /**
     * 查询单个匹配结果详情
     */
    AppSocialMatchResultDetailVO getMatchResultDetail(Long userId, Long resultId);

    /**
     * 标记匹配结果为「已添加」
     */
    void markMatchResultAdded(Long userId, Long resultId);

}
