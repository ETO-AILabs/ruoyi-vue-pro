package cn.iocoder.yudao.module.member.dal.dataobject.tag;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员标签 DO
 *
 * @author 芋道源码
 */
@TableName("member_tag")
@KeySequence("member_tag_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberTagDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 标签名称
     */
    private String name;
    /**
     * 标签名称（预置标签使用）
     */
    private String tagName;
    /**
     * 父ID，0=根节点
     */
    private Long parentId;
    /**
     * 分类编码
     */
    private String category;
    /**
     * 标签编码（前端用，同分组内唯一）
     */
    private String code;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态 1-可用 0-不可用
     */
    private Integer status;

}