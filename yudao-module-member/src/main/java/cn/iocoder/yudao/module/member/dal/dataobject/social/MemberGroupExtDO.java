package cn.iocoder.yudao.module.member.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 学校信息扩展 DO
 *
 * @author 芋道源码
 */
@TableName("member_group_ext")
@KeySequence("member_group_ext_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGroupExtDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 学校id
     */
    private Long groupId;
    /**
     * 学校类型 1-本科 2-专科 3-高中
     */
    private Integer schoolType;
    /**
     * 标签（985,211等）
     */
    private String schoolTag;
    /**
     * 所属省份
     */
    private String province;
    /**
     * 所属市
     */
    private String city;
    /**
     * 所属区
     */
    private String district;
    /**
     * 状态 1-正常 0-停用
     */
    private Integer schoolStatus;

}
