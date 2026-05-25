package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 APP - 地区树 Response VO")
@Data
public class AppSocialRegionTreeRespVO {

    @Schema(description = "区域编号", example = "110000")
    private Integer id;

    @Schema(description = "区域名称", example = "北京")
    private String name;

    @Schema(description = "子区域")
    private List<AppSocialRegionTreeRespVO> children;

}