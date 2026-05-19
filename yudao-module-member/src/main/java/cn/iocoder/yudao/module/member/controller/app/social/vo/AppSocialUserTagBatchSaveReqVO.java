package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "用户 APP - 批量保存用户标签 Request VO")
@Data
public class AppSocialUserTagBatchSaveReqVO {

    @Schema(description = "标签ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 5, 12]")
    @NotEmpty(message = "标签ID列表不能为空")
    private List<Long> tagIds;

    @Schema(description = "来源(self=自选/auto=行为打标)", requiredMode = Schema.RequiredMode.REQUIRED, example = "self")
    @NotEmpty(message = "来源不能为空")
    private String source;

}
