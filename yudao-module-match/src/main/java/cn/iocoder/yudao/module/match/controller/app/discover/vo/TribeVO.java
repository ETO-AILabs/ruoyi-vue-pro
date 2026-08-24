package cn.iocoder.yudao.module.match.controller.app.discover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * MatchAI APP - 部落 VO
 *
 * 接口契约见 api-contract.md。
 */
@Schema(description = "MatchAI APP - 部落 VO")
@Data
public class TribeVO {

    @Schema(description = "部落编号", example = "1")
    private Long id;

    @Schema(description = "部落名称", example = "夜跑搭子营")
    private String name;

    @Schema(description = "封面图", example = "")
    private String cover;

    @Schema(description = "成员数量", example = "128")
    private Integer memberCount;

    @Schema(description = "描述", example = "描述")
    private String description;

    @Schema(description = "标签", example = "[\"运动\", \"跑步\"]")
    private List<String> tags;

}
