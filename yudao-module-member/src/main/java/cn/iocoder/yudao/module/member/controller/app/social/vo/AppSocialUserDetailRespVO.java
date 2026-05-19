package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "用户 APP - 用户社交主页 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialUserDetailRespVO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小艾")
    private String nickname;

    @Schema(description = "用户头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xxx.png")
    private String avatar;

    @Schema(description = "性别", example = "1")
    private Integer sex;

    @Schema(description = "年龄", example = "20")
    private Integer age;

    @Schema(description = "个人描述", example = "热爱生活")
    private String userDesc;

    @Schema(description = "微信号", example = "wx123")
    private String wechat;

    @Schema(description = "标签名称列表")
    private List<String> tagNames;

    @Schema(description = "学校名称", example = "北京大学")
    private String schoolName;

    @Schema(description = "常驻地", example = "杭州·西湖区")
    private String residence;

    @Schema(description = "MBTI性格类型", example = "INTJ")
    private String mbti;

    @Schema(description = "职业", example = "产品经理")
    private String profession;

}
