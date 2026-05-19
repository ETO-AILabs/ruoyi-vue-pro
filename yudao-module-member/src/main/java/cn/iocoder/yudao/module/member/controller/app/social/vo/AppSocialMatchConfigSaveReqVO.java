package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Schema(description = "用户 APP - 保存场景匹配配置 Request VO")
@Data
public class AppSocialMatchConfigSaveReqVO {

    @Schema(description = "场景编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buddy")
    @NotEmpty(message = "场景编码不能为空")
    private String sceneCode;

    @Schema(description = "选中的标签ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[501, 101, 102]")
    @NotEmpty(message = "标签ID列表不能为空")
    private List<Long> tagIds;

    @Schema(description = "额外字段(微信号、自我介绍等非标签数据)", example = "{\"wx\": \"mywechat123\"}")
    private Map<String, Object> extraFields;

}
