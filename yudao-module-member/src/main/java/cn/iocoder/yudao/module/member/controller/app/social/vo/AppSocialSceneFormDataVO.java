package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 APP - 场景表单数据 Response VO（含用户基础信息）")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialSceneFormDataVO {

    @Schema(description = "场景区块列表")
    private List<AppSocialSceneFormRespVO> sections;

    @Schema(description = "微信号", example = "mywechat123")
    private String wechat;

    @Schema(description = "常驻地", example = "杭州 · 西湖区")
    private String residence;

    @Schema(description = "出生日期", example = "2000-05-20T00:00:00")
    private LocalDateTime birthday;

    @Schema(description = "身高(cm)", example = "175")
    private Integer height;

    @Schema(description = "家乡（市 · 区）", example = "杭州 · 西湖区")
    private String hometown;

    @Schema(description = "MBTI", example = "INFP")
    private String mbti;

    @Schema(description = "收入档位 1=学生 2=5k↓ 3=5-10k 4=10-20k 5=20-50k 6=50k↑", example = "3")
    private Integer income;

    @Schema(description = "关于我（love 场景回显）", example = "喜欢旅行和摄影")
    private String aboutMe;

    @Schema(description = "理想中的 TA（love 场景回显）", example = "阳光开朗，有共同爱好")
    private String idealTa;

}
