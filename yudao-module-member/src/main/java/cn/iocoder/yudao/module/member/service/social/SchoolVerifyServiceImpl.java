package cn.iocoder.yudao.module.member.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyAuditReqVO;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyRespVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialSchoolVerifyReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSchoolVerifyDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.group.MemberGroupMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberSchoolVerifyMapper;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.SCHOOL_VERIFY_EXISTS;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.SCHOOL_VERIFY_NOT_EXISTS;

@Service
@Validated
public class SchoolVerifyServiceImpl implements SchoolVerifyService {

    @Resource
    private MemberSchoolVerifyMapper memberSchoolVerifyMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberGroupMapper memberGroupMapper;

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

    @Override
    public PageResult<SchoolVerifyRespVO> getVerifyPage(SchoolVerifyPageReqVO reqVO) {
        // 1. 分页查询认证记录
        PageResult<MemberSchoolVerifyDO> page = memberSchoolVerifyMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<MemberSchoolVerifyDO>()
                        .eqIfPresent(MemberSchoolVerifyDO::getVerifyStatus, reqVO.getVerifyStatus())
                        .orderByDesc(MemberSchoolVerifyDO::getCreateTime));

        if (CollUtil.isEmpty(page.getList())) {
            return PageResult.empty();
        }

        // 2. 批量查询 user nickname + group schoolName
        Set<Long> userIds = page.getList().stream().map(MemberSchoolVerifyDO::getUserId).collect(Collectors.toSet());
        Set<Long> groupIds = page.getList().stream().map(MemberSchoolVerifyDO::getGroupId).collect(Collectors.toSet());

        Map<Long, MemberUserDO> userMap = memberUserMapper.selectBatchIds(userIds)
                .stream().collect(Collectors.toMap(MemberUserDO::getId, u -> u));
        Map<Long, MemberGroupDO> groupMap = memberGroupMapper.selectBatchIds(groupIds)
                .stream().collect(Collectors.toMap(MemberGroupDO::getId, g -> g));

        // 3. 组装 VO
        List<SchoolVerifyRespVO> list = page.getList().stream().map(verify -> {
            MemberUserDO user = userMap.get(verify.getUserId());
            MemberGroupDO group = groupMap.get(verify.getGroupId());
            return new SchoolVerifyRespVO(
                    verify.getId(),
                    verify.getUserId(),
                    user != null ? user.getNickname() : null,
                    verify.getGroupId(),
                    group != null ? group.getName() : null,
                    verify.getSchoolUrl(),
                    verify.getVerifyStatus(),
                    verify.getCreateTime()
            );
        }).collect(Collectors.toList());

        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public void auditVerify(SchoolVerifyAuditReqVO reqVO) {
        MemberSchoolVerifyDO verify = memberSchoolVerifyMapper.selectById(reqVO.getId());
        if (verify == null) {
            throw exception(SCHOOL_VERIFY_NOT_EXISTS);
        }

        // 更新审核状态
        verify.setVerifyStatus(reqVO.getVerifyStatus());
        memberSchoolVerifyMapper.updateById(verify);

        // 审核通过 → 标记用户为学生认证
        if (reqVO.getVerifyStatus() == 1) {
            MemberUserDO user = memberUserMapper.selectById(verify.getUserId());
            if (user != null) {
                user.setIsVerified(true);
                memberUserMapper.updateById(user);
            }
        }
    }

}
