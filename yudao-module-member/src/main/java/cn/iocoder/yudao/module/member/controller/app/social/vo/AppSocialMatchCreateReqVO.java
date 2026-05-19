package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Schema(description = "用户 APP - 创建匹配任务 Request VO")
@Data
public class AppSocialMatchCreateReqVO {

    @Schema(description = "场景ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    @Schema(description = "选中的标签ID列表", example = "[101, 102, 301]")
    private List<Long> tagIds;

    @Schema(description = "匹配诉求", example = "想找个一起打篮球的朋友")
    private String matchGoal;

    @Schema(description = "匹配备注", example = "周末有空")
    private String matchRemark;

    @Schema(description = "额外字段(微信号、图片等非标签数据)", example = "{\"wx\": \"mywechat123\"}")
    private Map<String, Object> extraFields;

}
