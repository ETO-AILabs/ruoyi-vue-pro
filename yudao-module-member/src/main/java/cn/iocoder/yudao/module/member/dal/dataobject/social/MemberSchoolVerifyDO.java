package cn.iocoder.yudao.module.member.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 学校认证申请 DO
 *
 * @author 芋道源码
 */
@TableName("member_school_verify")
@KeySequence("member_school_verify_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSchoolVerifyDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 学校id
     */
    private Long groupId;
    /**
     * 图片地址
     */
    private String schoolUrl;
    /**
     * 审核状态 0-未审核 1-审核通过 2-拒绝
     */
    private Integer verifyStatus;

}
