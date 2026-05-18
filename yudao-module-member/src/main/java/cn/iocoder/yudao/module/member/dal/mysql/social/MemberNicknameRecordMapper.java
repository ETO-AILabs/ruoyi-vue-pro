package cn.iocoder.yudao.module.member.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberNicknameRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;

@Mapper
public interface MemberNicknameRecordMapper extends BaseMapperX<MemberNicknameRecordDO> {

    default MemberNicknameRecordDO selectByUserIdAndGenDate(Long userId, LocalDate genDate) {
        return selectOne(new LambdaQueryWrapperX<MemberNicknameRecordDO>()
                .eq(MemberNicknameRecordDO::getUserId, userId)
                .eq(MemberNicknameRecordDO::getGenDate, genDate));
    }

}
