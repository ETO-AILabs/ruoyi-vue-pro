package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 APP - 学校认证申请 Request VO")
@Data
public class AppSocialSchoolVerifyReqVO {

    @Schema(description = "学校ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "学校不能为空")
    private Long groupId;

    @Schema(description = "学生证图片地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xxx.jpg")
    @NotEmpty(message = "学生证图片不能为空")
    private String schoolUrl;

}
