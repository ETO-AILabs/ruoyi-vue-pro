package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneSectionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberSceneSectionMapper extends BaseMapperX<MemberSceneSectionDO> {

    default List<MemberSceneSectionDO> selectListBySceneCode(String sceneCode) {
        return selectList(new LambdaQueryWrapperX<MemberSceneSectionDO>()
                .eq(MemberSceneSectionDO::getSceneCode, sceneCode)
                .orderByAsc(MemberSceneSectionDO::getSort));
    }

}
