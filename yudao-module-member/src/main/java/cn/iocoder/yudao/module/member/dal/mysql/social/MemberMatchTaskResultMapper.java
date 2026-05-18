package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskResultDO;
import org.apache.ibatis.annotations.Mapper;

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

}
