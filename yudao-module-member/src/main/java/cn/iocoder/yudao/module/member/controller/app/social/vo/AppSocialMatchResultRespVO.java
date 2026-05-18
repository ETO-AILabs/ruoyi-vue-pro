package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - 匹配结果 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialMatchResultRespVO {

    @Schema(description = "结果ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "匹配对象用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long matchedUserId;

    @Schema(description = "匹配得分", requiredMode = Schema.RequiredMode.REQUIRED)
    private Float matchScore;

}
