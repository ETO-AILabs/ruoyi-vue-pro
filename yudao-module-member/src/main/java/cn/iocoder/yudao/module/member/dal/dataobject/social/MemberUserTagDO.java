package cn.iocoder.yudao.module.member.dal.dataobject.social;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户标签 DO
 *
 * @author 芋道源码
 */
@TableName("member_user_tag")
@KeySequence("member_user_tag_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUserTagDO {

    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 标签ID
     */
    private Long tagId;
    /**
     * 来源(self=自选/auto=行为打标)
     */
    private String source;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
