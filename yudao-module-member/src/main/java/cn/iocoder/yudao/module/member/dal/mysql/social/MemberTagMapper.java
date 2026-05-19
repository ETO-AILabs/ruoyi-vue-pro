package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberTagDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberTagMapper extends BaseMapperX<MemberTagDO> {

    default List<MemberTagDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<MemberTagDO>()
                .eq(MemberTagDO::getStatus, status)
                .orderByAsc(MemberTagDO::getSort));
    }

    default List<MemberTagDO> selectListByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<MemberTagDO>()
                .eq(MemberTagDO::getParentId, parentId)
                .orderByAsc(MemberTagDO::getSort));
    }

    default List<MemberTagDO> selectListByCategory(String category) {
        return selectList(new LambdaQueryWrapperX<MemberTagDO>()
                .eq(MemberTagDO::getCategory, category)
                .eq(MemberTagDO::getParentId, 0L)
                .orderByAsc(MemberTagDO::getSort));
    }

    default MemberTagDO selectByParentIdAndCode(Long parentId, String code) {
        return selectOne(new LambdaQueryWrapperX<MemberTagDO>()
                .eq(MemberTagDO::getParentId, parentId)
                .eq(MemberTagDO::getCode, code));
    }

    default List<MemberTagDO> selectListByParentIds(List<Long> parentIds) {
        return selectList(new LambdaQueryWrapperX<MemberTagDO>()
                .in(MemberTagDO::getParentId, parentIds)
                .orderByAsc(MemberTagDO::getSort));
    }

}
