package cn.iocoder.yudao.module.member.controller.admin.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SceneBaseVO {

    @Schema(description = "场景编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "buddy")
    private String sceneCode;

    @Schema(description = "场景类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "场景类型不能为空")
    private Integer sceneType;

    @Schema(description = "场景名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "校园交友")
    @NotEmpty(message = "场景名称不能为空")
    private String sceneName;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer sceneStatus;

}
