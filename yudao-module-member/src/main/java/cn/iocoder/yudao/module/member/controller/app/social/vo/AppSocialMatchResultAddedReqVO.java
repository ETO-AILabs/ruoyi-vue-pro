package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 APP - 标记匹配结果为已添加 Request VO")
@Data
public class AppSocialMatchResultAddedReqVO {

    @Schema(description = "结果ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "resultId 不能为空")
    private Long resultId;

}
