package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMatchTaskMapper extends BaseMapperX<MemberMatchTaskDO> {

    default MemberMatchTaskDO selectByUserIdAndSceneIdAndStatusIn(Long userId, Long sceneId, List<Integer> statuses) {
        return selectOne(new LambdaQueryWrapperX<MemberMatchTaskDO>()
                .eq(MemberMatchTaskDO::getUserId, userId)
                .eq(MemberMatchTaskDO::getSceneId, sceneId)
                .in(MemberMatchTaskDO::getMatchStatus, statuses)
                .orderByDesc(MemberMatchTaskDO::getCreateTime)
                .last("LIMIT 1"));
    }

    default MemberMatchTaskDO selectLatestByUserIdAndSceneId(Long userId, Long sceneId) {
        return selectOne(new LambdaQueryWrapperX<MemberMatchTaskDO>()
                .eq(MemberMatchTaskDO::getUserId, userId)
                .eq(MemberMatchTaskDO::getSceneId, sceneId)
                .orderByDesc(MemberMatchTaskDO::getCreateTime)
                .last("LIMIT 1"));
    }

    default List<MemberMatchTaskDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<MemberMatchTaskDO>()
                .eq(MemberMatchTaskDO::getUserId, userId)
                .orderByDesc(MemberMatchTaskDO::getCreateTime));
    }

}
