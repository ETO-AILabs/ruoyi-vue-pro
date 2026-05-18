package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - 社交场景 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialSceneRespVO {

    @Schema(description = "场景主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "场景编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Long sceneCode;

    @Schema(description = "场景名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "校园交友")
    private String sceneName;

    @Schema(description = "场景配置", example = "{}")
    private String sceneConfig;

    @Schema(description = "页面路径", example = "/pages/match/index")
    private String pagePath;

}
