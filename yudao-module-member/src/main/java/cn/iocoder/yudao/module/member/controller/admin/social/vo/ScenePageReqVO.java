package cn.iocoder.yudao.module.member.controller.admin.social.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 场景分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ScenePageReqVO extends PageParam {

    @Schema(description = "场景名称", example = "校园")
    private String sceneName;

    @Schema(description = "场景类型", example = "1")
    private Integer sceneType;

    @Schema(description = "状态", example = "1")
    private Integer sceneStatus;

}
