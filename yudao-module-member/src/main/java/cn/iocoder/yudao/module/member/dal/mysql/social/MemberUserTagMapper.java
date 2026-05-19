package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberUserTagDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberUserTagMapper extends BaseMapperX<MemberUserTagDO> {

    default List<MemberUserTagDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<MemberUserTagDO>()
                .eq(MemberUserTagDO::getUserId, userId));
    }

    default int deleteByUserIdAndTagIds(Long userId, List<Long> tagIds) {
        return delete(new LambdaQueryWrapperX<MemberUserTagDO>()
                .eq(MemberUserTagDO::getUserId, userId)
                .in(MemberUserTagDO::getTagId, tagIds));
    }

    default List<MemberUserTagDO> selectListByUserIdAndTagIds(Long userId, List<Long> tagIds) {
        return selectList(new LambdaQueryWrapperX<MemberUserTagDO>()
                .eq(MemberUserTagDO::getUserId, userId)
                .in(MemberUserTagDO::getTagId, tagIds));
    }

}
