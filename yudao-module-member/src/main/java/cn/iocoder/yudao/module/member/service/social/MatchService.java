package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchSceneRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskDO;

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

}
