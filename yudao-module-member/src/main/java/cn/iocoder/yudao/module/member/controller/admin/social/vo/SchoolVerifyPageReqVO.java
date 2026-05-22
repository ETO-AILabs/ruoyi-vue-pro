package cn.iocoder.yudao.module.member.controller.admin.social.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 学校认证分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SchoolVerifyPageReqVO extends PageParam {

    @Schema(description = "审核状态 0-未审核 1-审核通过 2-拒绝", example = "0")
    private Integer verifyStatus;

}
