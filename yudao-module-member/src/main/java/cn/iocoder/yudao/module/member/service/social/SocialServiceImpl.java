package cn.iocoder.yudao.module.member.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialUserDetailRespVO;
import cn.iocoder.yudao.module.member.convert.social.SocialConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberTagDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.group.MemberGroupMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberSceneMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberTagMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberUserSceneMapper;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static java.util.stream.Collectors.toList;

@Service
@Validated
public class SocialServiceImpl implements SocialService {

    @Resource
    private MemberSceneMapper memberSceneMapper;
    @Resource
    private MemberUserSceneMapper memberUserSceneMapper;
    @Resource
    private MemberGroupMapper memberGroupMapper;
    @Resource
    private MemberTagMapper memberTagMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public List<MemberSceneDO> getSceneList(Long userId) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            return Collections.emptyList();
        }

        // 有学校：取学校关联的场景
        if (user.getGroupId() != null) {
            return memberUserSceneMapper.selectListByScopeTypeAndScopeId(1,
                    String.valueOf(user.getGroupId()))
                    .stream()
                    .map(us -> memberSceneMapper.selectById(us.getSceneId()))
                    .filter(scene -> scene != null && scene.getSceneStatus() == 1)
                    .collect(toList());
        }

        // 无学校：取兜底场景
        return memberUserSceneMapper.selectListByScopeTypeAndScopeId(0, "0")
                .stream()
                .map(us -> memberSceneMapper.selectById(us.getSceneId()))
                .filter(scene -> scene != null && scene.getSceneStatus() == 1)
                .collect(toList());
    }

    @Override
    public List<MemberGroupDO> searchSchool(String name) {
        if (StrUtil.isBlank(name)) {
            return Collections.emptyList();
        }
        return memberGroupMapper.selectSchoolListByNameLike(name);
    }

    @Override
    public List<MemberTagDO> getTagList() {
        return memberTagMapper.selectListByStatus(1);
    }

    @Override
    public AppSocialUserDetailRespVO getUserDetail(Long userId) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(ErrorCodeConstants.USER_NOT_EXISTS);
        }
        // 校验必须有分身
        if (user.getIsHasCloned() == null || !user.getIsHasCloned()) {
            throw exception(ErrorCodeConstants.USER_NOT_CLONED);
        }

        AppSocialUserDetailRespVO resp = SocialConvert.INSTANCE.convert(user);

        // 计算年龄
        if (user.getBirthday() != null) {
            int age = java.time.LocalDate.now().getYear() - user.getBirthday().getYear();
            resp.setAge(age);
        }

        // 处理标签
        if (CollUtil.isNotEmpty(user.getTagIds())) {
            List<MemberTagDO> tags = memberTagMapper.selectBatchIds(user.getTagIds());
            resp.setTagNames(tags.stream().map(MemberTagDO::getTagName).collect(toList()));
        }

        // 处理学校名称
        if (user.getGroupId() != null) {
            MemberGroupDO group = memberGroupMapper.selectById(user.getGroupId());
            if (group != null) {
                resp.setSchoolName(group.getName());
            }
        }

        return resp;
    }

}
