package cn.iocoder.yudao.module.member.controller.admin.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SceneUpdateReqVO extends SceneBaseVO {

    @Schema(description = "场景ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "场景ID不能为空")
    private Long id;

}
