package cn.iocoder.yudao.module.member.controller.admin.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 学校认证审核 Request VO")
@Data
public class SchoolVerifyAuditReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "认证记录ID不能为空")
    private Long id;

    @Schema(description = "审核状态 1-审核通过 2-拒绝",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "审核状态不能为空")
    private Integer verifyStatus;

}
