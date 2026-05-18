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

}
