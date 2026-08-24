package cn.iocoder.yudao.module.match.controller.app.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * MatchAI APP - 喜欢候选人 Request VO
 */
@Schema(description = "MatchAI APP - 喜欢候选人 Request VO")
@Data
public class LikeReqVO {

    @Schema(description = "候选人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "候选人编号不能为空")
    private Long candidateId;

}
