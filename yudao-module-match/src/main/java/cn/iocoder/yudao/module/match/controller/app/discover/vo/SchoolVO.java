package cn.iocoder.yudao.module.match.controller.app.discover.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * MatchAI APP - 学校 VO（onboarding 学校选择用）
 *
 * 接口契约见 api-contract.md。
 */
@Schema(description = "MatchAI APP - 学校 VO")
@Data
public class SchoolVO {

    @Schema(description = "学校编码", example = "zju")
    private String code;

    @Schema(description = "学校名称", example = "浙江大学")
    private String name;

    @Schema(description = "所在城市", example = "杭州")
    private String city;

}
