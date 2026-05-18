package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialSchoolVerifyReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSchoolVerifyDO;

public interface SchoolVerifyService {

    /**
     * 提交学校认证申请
     */
    Long submitVerify(Long userId, AppSocialSchoolVerifyReqVO reqVO);

    /**
     * 查询认证状态
     */
    MemberSchoolVerifyDO getVerifyByUserId(Long userId);

}
