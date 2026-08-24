package cn.iocoder.yudao.module.match.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部落 DO
 *
 * 对应表 discover_tribe（DDL 见 resources/sql/match.sql）。
 */
@TableName("discover_tribe")
@Data
@EqualsAndHashCode(callSuper = true)
public class DiscoverTribeDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 部落名称
     */
    private String name;

    /**
     * 封面图
     */
    private String cover;

    /**
     * 成员数量
     */
    private Integer memberCount;

    /**
     * 描述
     */
    private String description;

    /**
     * 标签（JSON 数组字符串）
     */
    private String tags;

}
