package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberSceneMapper extends BaseMapperX<MemberSceneDO> {

    default List<MemberSceneDO> selectListByStatus(Integer status) {
        return selectList(MemberSceneDO::getSceneStatus, status);
    }

}
