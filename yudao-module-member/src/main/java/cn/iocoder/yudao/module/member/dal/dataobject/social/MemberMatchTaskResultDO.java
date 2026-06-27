package cn.iocoder.yudao.module.member.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 匹配结果 DO
 *
 * @author 芋道源码
 */
@TableName("member_match_task_result")
@KeySequence("member_match_task_result_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMatchTaskResultDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 匹配任务ID
     */
    private Long taskId;
    /**
     * 被匹配的用户ID
     */
    private Long matchedUserId;
    /**
     * 匹配得分
     */
    private Float matchScore;
    /**
     * 匹配结果
     */
    private String matchResult;
    /**
     * AI 推荐理由
     */
    private String aiReason;
    /**
     * 开始聊的建议
     */
    private String chatTip;
    /**
     * 场景扩展数据(JSON: currentStatus/teachSkill/wantSkill/offer/want/photos/conditionValue 等)
     */
    private String extension;
    /**
     * 是否已添加 0-否 1-是
     */
    private Integer isAdded;
    /**
     * 添加时间
     */
    private LocalDateTime addedTime;

}
