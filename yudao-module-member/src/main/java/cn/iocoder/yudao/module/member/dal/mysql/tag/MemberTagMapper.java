package cn.iocoder.yudao.module.member.dal.mysql.tag;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.tag.vo.MemberTagPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.tag.MemberTagDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员标签 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberTagMapper extends BaseMapperX<MemberTagDO> {

    default PageResult<MemberTagDO> selectPage(MemberTagPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberTagDO>()
                .likeIfPresent(MemberTagDO::getName, reqVO.getName())
                .betweenIfPresent(MemberTagDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberTagDO::getId));
    }

    default MemberTagDO selelctByName(String name) {
        return selectOne(MemberTagDO::getName, name);
    }

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