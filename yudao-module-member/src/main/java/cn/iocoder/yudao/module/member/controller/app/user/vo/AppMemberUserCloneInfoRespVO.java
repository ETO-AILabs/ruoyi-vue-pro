package cn.iocoder.yudao.module.member.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 APP - 分身信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppMemberUserCloneInfoRespVO {

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小艾")
    private String nickname;

    @Schema(description = "用户头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xxx.png")
    private String avatar;

    @Schema(description = "性别", example = "1")
    private Integer sex;

    @Schema(description = "出生日期")
    private LocalDateTime birthday;

    @Schema(description = "经度", example = "116.397128")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "39.916527")
    private BigDecimal latitude;

    @Schema(description = "地理位置名称", example = "北京市海淀区")
    private String location;

    @Schema(description = "常驻地", example = "杭州 · 西湖区")
    private String residence;

    @Schema(description = "MBTI性格类型", example = "INTJ")
    private String mbti;

    @Schema(description = "职业标签ID", example = "100")
    private Long professionTagId;

    @Schema(description = "职业名称", example = "程序员")
    private String professionName;

    @Schema(description = "学校名称", example = "浙江大学")
    private String schoolName;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;

    @Schema(description = "学校ID", example = "1")
    private Long groupId;

    @Schema(description = "个人描述", example = "热爱生活的程序员")
    private String userDesc;

    @Schema(description = "微信号", example = "wx123")
    private String wechat;

    @Schema(description = "是否已创建分身", example = "true")
    private Boolean isHasCloned;

}
