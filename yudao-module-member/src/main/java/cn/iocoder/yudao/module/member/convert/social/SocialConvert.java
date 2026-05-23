package cn.iocoder.yudao.module.member.convert.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.social.vo.*;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.*;
import cn.iocoder.yudao.module.member.dal.dataobject.tag.MemberTagDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SocialConvert {

    SocialConvert INSTANCE = Mappers.getMapper(SocialConvert.class);

    AppSocialSceneRespVO convert(MemberSceneDO bean);

    List<AppSocialSceneRespVO> convertSceneList(List<MemberSceneDO> list);

    AppSocialTagRespVO convert(MemberTagDO bean);

    List<AppSocialTagRespVO> convertTagList(List<MemberTagDO> list);

    AppSocialMatchTaskRespVO convert(MemberMatchTaskDO bean);

    List<AppSocialMatchTaskRespVO> convertMatchTaskList(List<MemberMatchTaskDO> list);

    PageResult<AppSocialMatchTaskRespVO> convertMatchTaskPage(PageResult<MemberMatchTaskDO> page);

    AppSocialMatchResultRespVO convert(MemberMatchTaskResultDO bean);

    List<AppSocialMatchResultRespVO> convertMatchResultList(List<MemberMatchTaskResultDO> list);

    @Mapping(source = "nickname", target = "nickname")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(source = "sex", target = "sex")
    @Mapping(source = "userDesc", target = "userDesc")
    @Mapping(source = "wechat", target = "wechat")
    @Mapping(source = "residence", target = "residence")
    @Mapping(source = "mbti", target = "mbti")
    @Mapping(source = "profession", target = "profession")
    AppSocialUserDetailRespVO convert(MemberUserDO bean);

    AppSocialSchoolSearchRespVO convertSchool(MemberGroupDO bean);

    List<AppSocialSchoolSearchRespVO> convertSchoolList(List<MemberGroupDO> list);

}
