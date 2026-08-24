package cn.iocoder.yudao.module.match.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 匹配结果 DO
 *
 * 对应表 match_result（DDL 见 resources/sql/match.sql）。
 */
@TableName("match_result")
@Data
@EqualsAndHashCode(callSuper = true)
public class MatchResultDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户编号（关联 member_user.id）
     */
    private Long userId;

    /**
     * 候选人编号（关联 match_candidate.id）
     */
    private Long candidateId;

    /**
     * 匹配时间
     */
    private LocalDateTime matchedTime;

}
