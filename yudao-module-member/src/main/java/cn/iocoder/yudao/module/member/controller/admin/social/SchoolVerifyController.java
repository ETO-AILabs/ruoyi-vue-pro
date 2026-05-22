package cn.iocoder.yudao.module.member.controller.admin.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyAuditReqVO;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.SchoolVerifyRespVO;
import cn.iocoder.yudao.module.member.service.social.SchoolVerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 学校认证")
@RestController
@RequestMapping("/member/social/school/verify")
@Validated
public class SchoolVerifyController {

    @Resource
    private SchoolVerifyService schoolVerifyService;

    @GetMapping("/page")
    @Operation(summary = "获得学校认证分页")
    @PreAuthorize("@ss.hasPermission('member:social:school:verify:query')")
    public CommonResult<PageResult<SchoolVerifyRespVO>> getVerifyPage(@Valid SchoolVerifyPageReqVO reqVO) {
        return success(schoolVerifyService.getVerifyPage(reqVO));
    }

    @PutMapping("/audit")
    @Operation(summary = "审核学校认证")
    @PreAuthorize("@ss.hasPermission('member:social:school:verify:audit')")
    public CommonResult<Boolean> auditVerify(@Valid @RequestBody SchoolVerifyAuditReqVO reqVO) {
        schoolVerifyService.auditVerify(reqVO);
        return success(true);
    }

}
