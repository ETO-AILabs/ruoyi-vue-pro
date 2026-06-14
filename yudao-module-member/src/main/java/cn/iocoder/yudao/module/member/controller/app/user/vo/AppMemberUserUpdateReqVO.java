package cn.iocoder.yudao.module.member.controller.app.user.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 会员用户更新 Request VO")
@Data
public class AppMemberUserUpdateReqVO {

    @Schema(description = "用户昵称", example = "李四")
    private String nickname;

    @Schema(description = "头像", example = "https://www.iocoder.cn/x.png")
    @URL(message = "头像必须是 URL 格式")
    private String avatar;

    @Schema(description = "性别", example = "1")
    private Integer sex;

    // ===== 合拍伴侣基础信息 =====

    @Schema(description = "出生日期")
    private LocalDateTime birthday;

    @Schema(description = "身高(cm)", example = "175")
    private Integer height;

    @Schema(description = "家乡（市 · 区）", example = "杭州 · 西湖区")
    private String hometown;

    @Schema(description = "常驻地（市 · 区）", example = "上海 · 浦东")
    private String residence;

    @Schema(description = "MBTI", example = "INFP")
    private String mbti;

    @Schema(description = "收入档位 1=学生 2=5k↓ 3=5-10k 4=10-20k 5=20-50k 6=50k↑", example = "3")
    private Integer income;

    @Schema(description = "微信号", example = "mywechat123")
    private String wechat;

}
