package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberAvatarsDO;

/**
 * 头像 Service 接口
 *
 * @author 芋道源码
 */
public interface AvatarService {

    /**
     * 随机获取一个预置头像
     */
    MemberAvatarsDO getRandomAvatar();

}
