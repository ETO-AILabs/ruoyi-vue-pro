package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyAuditReqVO;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyRespVO;
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

    /**
     * 分页查询学校认证（Admin）
     */
    PageResult<SchoolVerifyRespVO> getVerifyPage(SchoolVerifyPageReqVO reqVO);

    /**
     * 审核学校认证
     */
    void auditVerify(SchoolVerifyAuditReqVO reqVO);

}
