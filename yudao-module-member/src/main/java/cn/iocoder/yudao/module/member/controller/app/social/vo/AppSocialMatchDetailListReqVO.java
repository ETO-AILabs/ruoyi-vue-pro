package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - 匹配结果列表 Request VO")
@Data
public class AppSocialMatchDetailListReqVO {

    @Schema(description = "场景编码，可选过滤：buddy/love/skill/swap", example = "buddy")
    private String sceneCode;

    @Schema(description = "是否仅查询未添加", example = "false")
    private Boolean onlyUnadded;

}
