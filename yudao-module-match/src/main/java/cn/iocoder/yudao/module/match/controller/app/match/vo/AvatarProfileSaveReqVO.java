package cn.iocoder.yudao.module.match.controller.app.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MatchAI APP - 保存 AI 分身 Request VO
 *
 * 字段与 AvatarProfileVO 一致（接口契约见 api-contract.md）。
 */
@Schema(description = "MatchAI APP - 保存 AI 分身 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AvatarProfileSaveReqVO extends AvatarProfileVO {
}
