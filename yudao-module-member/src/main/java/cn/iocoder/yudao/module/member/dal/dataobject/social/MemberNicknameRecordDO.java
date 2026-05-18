package cn.iocoder.yudao.module.member.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 昵称生成记录 DO
 *
 * @author 芋道源码
 */
@TableName("member_nickname")
@KeySequence("member_nickname_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberNicknameRecordDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 生成次数
     */
    private Integer genCount;
    /**
     * 生成日期
     */
    private LocalDate genDate;

}
