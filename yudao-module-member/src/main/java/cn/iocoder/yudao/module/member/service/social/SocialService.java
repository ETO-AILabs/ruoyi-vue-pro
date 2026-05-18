package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialUserDetailRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberTagDO;

import java.util.List;

public interface SocialService {

    /**
     * 获取用户可见的场景列表
     */
    List<MemberSceneDO> getSceneList(Long userId);

    /**
     * 搜索学校
     */
    List<MemberGroupDO> searchSchool(String name);

    /**
     * 获取可用标签列表
     */
    List<MemberTagDO> getTagList();

    /**
     * 获取用户社交主页信息
     */
    AppSocialUserDetailRespVO getUserDetail(Long userId);

}
