package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberUserSceneDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberUserSceneMapper extends BaseMapperX<MemberUserSceneDO> {

    default List<MemberUserSceneDO> selectListByScopeTypeAndScopeId(Integer scopeType, String scopeId) {
        return selectList(new LambdaQueryWrapperX<MemberUserSceneDO>()
                .eq(MemberUserSceneDO::getScopeType, scopeType)
                .eq(MemberUserSceneDO::getScopeId, scopeId)
                .orderByAsc(MemberUserSceneDO::getSort));
    }

}
