package cn.iocoder.yudao.module.member.dal.dataobject.social;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 场景基础信息 DO
 *
 * @author 芋道源码
 */
@TableName("member_scene")
@KeySequence("member_scene_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSceneDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 场景编码
     */
    private Long sceneCode;
    /**
     * 场景类型 1-校外 2-校园
     */
    private Integer sceneType;
    /**
     * 场景名称
     */
    private String sceneName;
    /**
     * 状态 1-可用 0-不可用
     */
    private Integer sceneStatus;

}
