package cn.iocoder.yudao.module.match.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推荐候选人 DO
 *
 * 对应表 match_candidate（DDL 见 resources/sql/match.sql）。
 * 生产环境可由"其它用户的 AI 分身"动态生成，此处保留独立的候选人表便于运营维护种子候选人。
 */
@TableName("match_candidate")
@Data
@EqualsAndHashCode(callSuper = true)
public class MatchCandidateDO extends BaseDO {

    /**
     * 候选人编号
     */
    @TableId
    private Long id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 学校编码
     */
    private String schoolCode;

    /**
     * 学校名称
     */
    private String schoolName;

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
     * 是否已喜欢当前用户（用于触发双向匹配）
     */
    private Boolean likedMe;

}
