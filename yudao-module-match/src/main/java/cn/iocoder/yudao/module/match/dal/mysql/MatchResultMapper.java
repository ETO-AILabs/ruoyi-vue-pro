package cn.iocoder.yudao.module.match.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.match.dal.dataobject.MatchResultDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 匹配结果 Mapper
 *
 * 骨架实现，当前服务使用内存 Map；接入数据库后在此补充查询方法。
 */
@Mapper
public interface MatchResultMapper extends BaseMapperX<MatchResultDO> {
}
