package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 匹配任务 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialMatchTaskRespVO {

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "场景ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long sceneId;

    @Schema(description = "场景名称", example = "校园交友")
    private String sceneName;

    @Schema(description = "匹配状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer matchStatus;

    @Schema(description = "匹配诉求", example = "想找一起打球的朋友")
    private String matchGoal;

    @Schema(description = "匹配备注", example = "周末有空")
    private String matchRemark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "匹配完成时间")
    private LocalDateTime finishTime;

}
