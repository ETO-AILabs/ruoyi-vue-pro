package cn.iocoder.yudao.module.match.controller.app.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MatchAI APP - 匹配结果项 VO
 *
 * = CandidateCard + matchedAt + liked，接口契约见 api-contract.md。
 */
@Schema(description = "MatchAI APP - 匹配结果项 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MatchResultItemVO extends CandidateCardVO {

    @Schema(description = "匹配时间戳（毫秒）", example = "1700000000000")
    private Long matchedAt;

    @Schema(description = "是否互相喜欢", example = "true")
    private Boolean liked;

}
