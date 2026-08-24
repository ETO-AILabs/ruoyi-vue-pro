package cn.iocoder.yudao.module.match.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学校 DO
 *
 * 对应表 discover_school（DDL 见 resources/sql/match.sql）。
 */
@TableName("discover_school")
@Data
@EqualsAndHashCode(callSuper = true)
public class DiscoverSchoolDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 学校编码
     */
    private String code;

    /**
     * 学校名称
     */
    private String name;

    /**
     * 所在城市
     */
    private String city;

}
