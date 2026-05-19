package cn.iocoder.yudao.module.member.dal.dataobject.social;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 场景区块标题 DO
 *
 * @author 芋道源码
 */
@TableName("member_scene_section")
@KeySequence("member_scene_section_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSceneSectionDO {

    @TableId
    private Long id;
    /**
     * 场景编码(buddy/love/skill/swap)
     */
    private String sceneCode;
    /**
     * 区块编码(前端用，如buddy_activity/interest_tags/preferences)
     */
    private String code;
    /**
     * 区块标题
     */
    private String sectionName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
