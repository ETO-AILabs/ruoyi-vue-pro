package cn.iocoder.yudao.module.match.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 喜欢/跳过记录 DO
 *
 * 对应表 match_like_record（DDL 见 resources/sql/match.sql）。
 * type 区分：1=喜欢，2=跳过。
 */
@TableName("match_like_record")
@Data
@EqualsAndHashCode(callSuper = true)
public class MatchLikeRecordDO extends BaseDO {

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
     * 记录类型：1=喜欢，2=跳过
     */
    private Integer type;

}
