package cn.iocoder.yudao.module.member.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - 昵称生成 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppMemberUserNicknameGenerateRespVO {

    @Schema(description = "生成的昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "风中的羽翼")
    private String nickname;

    @Schema(description = "今日已生成次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer todayCount;

    @Schema(description = "每日上限", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer dailyLimit;

}
