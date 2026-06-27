package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskResultDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mapper
public interface MemberMatchTaskResultMapper extends BaseMapperX<MemberMatchTaskResultDO> {

    default List<MemberMatchTaskResultDO> selectListByTaskId(Long taskId) {
        return selectList(MemberMatchTaskResultDO::getTaskId, taskId);
    }

    default List<MemberMatchTaskResultDO> selectListByTaskIdIn(List<Long> taskIds) {
        return selectList(new LambdaQueryWrapperX<MemberMatchTaskResultDO>()
                .in(MemberMatchTaskResultDO::getTaskId, taskIds));
    }

    /**
     * 按结果ID列表批量查询
     */
    default List<MemberMatchTaskResultDO> selectListByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<MemberMatchTaskResultDO>()
                .in(MemberMatchTaskResultDO::getId, ids));
    }

    /**
     * 标记结果为已添加
     */
    default int updateAdded(Long id) {
        return update(null, new LambdaUpdateWrapper<MemberMatchTaskResultDO>()
                .eq(MemberMatchTaskResultDO::getId, id)
                .set(MemberMatchTaskResultDO::getIsAdded, 1)
                .set(MemberMatchTaskResultDO::getAddedTime, java.time.LocalDateTime.now()));
    }
}
