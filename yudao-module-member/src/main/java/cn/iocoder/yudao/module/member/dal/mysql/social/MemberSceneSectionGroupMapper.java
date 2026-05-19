package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneSectionGroupDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberSceneSectionGroupMapper extends BaseMapperX<MemberSceneSectionGroupDO> {

    default List<MemberSceneSectionGroupDO> selectListBySectionId(Long sectionId) {
        return selectList(new LambdaQueryWrapperX<MemberSceneSectionGroupDO>()
                .eq(MemberSceneSectionGroupDO::getSectionId, sectionId)
                .orderByAsc(MemberSceneSectionGroupDO::getSort));
    }

    default List<MemberSceneSectionGroupDO> selectListBySectionIds(List<Long> sectionIds) {
        return selectList(new LambdaQueryWrapperX<MemberSceneSectionGroupDO>()
                .in(MemberSceneSectionGroupDO::getSectionId, sectionIds)
                .orderByAsc(MemberSceneSectionGroupDO::getSort));
    }

    default List<MemberSceneSectionGroupDO> selectListByGroupTagId(Long groupTagId) {
        return selectList(new LambdaQueryWrapperX<MemberSceneSectionGroupDO>()
                .eq(MemberSceneSectionGroupDO::getGroupTagId, groupTagId));
    }

}
