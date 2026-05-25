package cn.iocoder.yudao.module.member.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 APP - 创建分身 Request VO")
@Data
public class AppMemberUserCloneCreateReqVO {

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小艾")
    @NotEmpty(message = "昵称不能为空")
    private String nickname;

    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "性别不能为空")
    private Integer sex;

    @Schema(description = "出生日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "出生日期不能为空")
    private LocalDateTime birthday;

    @Schema(description = "经度", example = "116.397128")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "39.916527")
    private BigDecimal latitude;

    @Schema(description = "地理位置名称", example = "北京市海淀区")
    private String location;

    @Schema(description = "常驻地", requiredMode = Schema.RequiredMode.REQUIRED, example = "杭州 · 西湖区")
    @NotEmpty(message = "常驻地不能为空")
    private String residence;

    @Schema(description = "MBTI性格类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "INTJ")
    @NotEmpty(message = "MBTI不能为空")
    private String mbti;

    @Schema(description = "职业标签ID", example = "100")
    private Long professionTagId;

    @Schema(description = "标签ID列表", example = "[1,2,3]")
    private List<Long> tagIds;

    @Schema(description = "学校ID", example = "1")
    private Long groupId;

    @Schema(description = "个人描述", example = "热爱生活的程序员")
    private String userDesc;

    @Schema(description = "微信号", example = "wx123")
    private String wechat;

}
