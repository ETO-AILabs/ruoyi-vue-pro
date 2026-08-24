package cn.iocoder.yudao.module.match.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 分身 DO
 *
 * 对应表 match_avatar（DDL 见 resources/sql/match.sql）。
 * tags / interests / values 为数组，DB 中以 JSON 数组字符串存储（如 '["算法"]'），
 * 后续接入 MyBatis-Plus 时可配置 JacksonTypeHandler 做自动映射。
 */
@TableName("match_avatar")
@Data
@EqualsAndHashCode(callSuper = true)
public class MatchAvatarDO extends BaseDO {

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
     * 学校编码
     */
    private String schoolCode;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 校区
     */
    private String campus;

    /**
     * 年级
     */
    private String grade;

    /**
     * 专业大类
     */
    private String majorCategory;

    /**
     * 专业
     */
    private String major;

    /**
     * 标签（JSON 数组字符串）
     */
    private String tags;

    /**
     * 兴趣（JSON 数组字符串）
     */
    private String interests;

    /**
     * MBTI
     */
    private String mbti;

    /**
     * 价值观（JSON 数组字符串）
     */
    private String values;

    /**
     * 一句话介绍
     */
    private String selfIntro;

    /**
     * 最近在忙的事
     */
    private String recentStatus;

}
