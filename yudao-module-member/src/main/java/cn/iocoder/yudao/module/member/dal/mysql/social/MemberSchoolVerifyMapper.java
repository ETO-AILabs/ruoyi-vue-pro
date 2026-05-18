package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSchoolVerifyDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberSchoolVerifyMapper extends BaseMapperX<MemberSchoolVerifyDO> {

    default MemberSchoolVerifyDO selectByUserId(Long userId) {
        return selectOne(MemberSchoolVerifyDO::getUserId, userId);
    }

}
