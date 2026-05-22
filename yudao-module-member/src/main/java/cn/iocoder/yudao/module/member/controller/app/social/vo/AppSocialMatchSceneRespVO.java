package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "用户 APP - 场景匹配状态 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialMatchSceneRespVO {

    @Schema(description = "匹配状态 0-待匹配 1-匹配中 2-匹配完成 3-冷却中",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer matchStatus;

    @Schema(description = "匹配结果列表（仅匹配完成时有数据）")
    private List<AppSocialMatchResultRespVO> results;

}
