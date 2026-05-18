package cn.iocoder.yudao.module.member.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 匹配任务 DO
 *
 * @author 芋道源码
 */
@TableName("member_match_task")
@KeySequence("member_match_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMatchTaskDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 场景ID
     */
    private Long sceneId;
    /**
     * 匹配状态 0-未开始 1-进行中 2-失败 3-成功
     */
    private Integer matchStatus;
    /**
     * 匹配备注
     */
    private String matchRemark;
    /**
     * 匹配诉求
     */
    private String matchGoal;
    /**
     * 匹配配置
     */
    private String matchConfig;
    /**
     * 匹配完成时间
     */
    private LocalDateTime finishTime;

}
