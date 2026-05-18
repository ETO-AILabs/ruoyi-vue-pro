package cn.iocoder.yudao.module.member.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 用户场景配置 DO
 *
 * @author 芋道源码
 */
@TableName("member_user_scene")
@KeySequence("member_user_scene_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUserSceneDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 场景主键 ID
     */
    private Long sceneId;
    /**
     * 场景类型 1-校外 2-校园
     */
    private Integer sceneType;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 用户信息id
     */
    private String scopeId;
    /**
     * 用户信息id类型 0-默认 1-学校id
     */
    private Integer scopeType;
    /**
     * 场景配置
     */
    private String sceneConfig;
    /**
     * 页面路径
     */
    private String pagePath;

}
