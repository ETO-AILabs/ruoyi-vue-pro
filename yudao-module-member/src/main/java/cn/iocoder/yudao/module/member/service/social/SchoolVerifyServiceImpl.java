package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialSchoolVerifyReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSchoolVerifyDO;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberSchoolVerifyMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.SCHOOL_VERIFY_EXISTS;

@Service
@Validated
public class SchoolVerifyServiceImpl implements SchoolVerifyService {

    @Resource
    private MemberSchoolVerifyMapper memberSchoolVerifyMapper;

    @Override
    public Long submitVerify(Long userId, AppSocialSchoolVerifyReqVO reqVO) {
        // 校验是否已有待审核记录
        MemberSchoolVerifyDO exist = memberSchoolVerifyMapper.selectByUserId(userId);
        if (exist != null && exist.getVerifyStatus() == 0) {
            throw exception(SCHOOL_VERIFY_EXISTS);
        }
        // 创建新的认证申请
        MemberSchoolVerifyDO verify = MemberSchoolVerifyDO.builder()
                .userId(userId)
                .groupId(reqVO.getGroupId())
                .schoolUrl(reqVO.getSchoolUrl())
                .verifyStatus(0)
                .build();
        memberSchoolVerifyMapper.insert(verify);
        return verify.getId();
    }

    @Override
    public MemberSchoolVerifyDO getVerifyByUserId(Long userId) {
        return memberSchoolVerifyMapper.selectByUserId(userId);
    }

}
