package cn.iocoder.yudao.module.member.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 预置头像库 DO
 *
 * @author 芋道源码
 */
@TableName("member_avatars")
@KeySequence("member_avatars_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAvatarsDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 头像地址
     */
    private String imageUrl;

}
