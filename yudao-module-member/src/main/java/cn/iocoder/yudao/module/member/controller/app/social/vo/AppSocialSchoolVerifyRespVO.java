package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - 学校认证状态 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialSchoolVerifyRespVO {

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer verifyStatus;

    @Schema(description = "学校名称", example = "北京大学")
    private String schoolName;

}
