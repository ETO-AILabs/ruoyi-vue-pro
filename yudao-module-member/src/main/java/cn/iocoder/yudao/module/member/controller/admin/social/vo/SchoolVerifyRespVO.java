package cn.iocoder.yudao.module.member.controller.admin.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学校认证 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolVerifyRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "学校ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long groupId;

    @Schema(description = "学校名称", example = "北京大学")
    private String schoolName;

    @Schema(description = "图片地址", example = "https://example.com/verify.jpg")
    private String schoolUrl;

    @Schema(description = "审核状态 0-未审核 1-审核通过 2-拒绝",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer verifyStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
