package cn.iocoder.yudao.module.member.dal.dataobject.social;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 场景区块子分组关联 DO
 *
 * @author 芋道源码
 */
@TableName("member_scene_section_group")
@KeySequence("member_scene_section_group_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSceneSectionGroupDO {

    @TableId
    private Long id;
    /**
     * 区块ID
     */
    private Long sectionId;
    /**
     * 子分组标签ID(level-2 tag)
     */
    private Long groupTagId;
    /**
     * 分组排序
     */
    private Integer sort;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
