package cn.iocoder.yudao.module.member.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialSceneFormRespVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialUserDetailRespVO;
import cn.iocoder.yudao.module.member.convert.social.SocialConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.*;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.group.MemberGroupMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.*;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;
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
    @Resource
    private MemberSceneSectionMapper memberSceneSectionMapper;
    @Resource
    private MemberSceneSectionGroupMapper memberSceneSectionGroupMapper;
    @Resource
    private MemberUserTagMapper memberUserTagMapper;
    @Resource
    private MemberMatchTaskMapper memberMatchTaskMapper;

    @Override
    public List<MemberSceneDO> getSceneList(Long userId) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            return Collections.emptyList();
        }

        // 有学校：优先取学校关联的场景；没配置则兜底
        if (user.getGroupId() != null) {
            List<MemberSceneDO> scenes = memberUserSceneMapper
                    .selectListByScopeTypeAndScopeId(1, String.valueOf(user.getGroupId()))
                    .stream()
                    .map(us -> memberSceneMapper.selectById(us.getSceneId()))
                    .filter(scene -> scene != null && scene.getSceneStatus() == 1)
                    .collect(toList());
            if (CollUtil.isNotEmpty(scenes)) {
                return scenes;
            }
        }

        // 兜底场景
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

        // 处理标签（优先从 member_user_tag 读取，兼容旧 tag_ids 字段）
        List<MemberUserTagDO> userTags = memberUserTagMapper.selectListByUserId(userId);
        List<Long> tagIdList;
        if (CollUtil.isNotEmpty(userTags)) {
            tagIdList = userTags.stream().map(MemberUserTagDO::getTagId).collect(toList());
        } else {
            tagIdList = user.getTagIds();
        }
        if (CollUtil.isNotEmpty(tagIdList)) {
            List<MemberTagDO> tags = memberTagMapper.selectBatchIds(tagIdList);
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

    @Override
    public List<AppSocialSceneFormRespVO> getSceneForm(String sceneCode, Long userId) {
        // 1. 查场景下所有区块
        List<MemberSceneSectionDO> sections = memberSceneSectionMapper.selectListBySceneCode(sceneCode);
        if (CollUtil.isEmpty(sections)) {
            return Collections.emptyList();
        }
        List<Long> sectionIds = sections.stream().map(MemberSceneSectionDO::getId).collect(toList());

        // 2. 查所有区块关联的子分组
        List<MemberSceneSectionGroupDO> allGroups = memberSceneSectionGroupMapper.selectListBySectionIds(sectionIds);
        Map<Long, List<MemberSceneSectionGroupDO>> groupMap = allGroups.stream()
                .collect(Collectors.groupingBy(MemberSceneSectionGroupDO::getSectionId));

        // 3. 收集所有 group_tag_id → 查 group标签 + 子标签
        Set<Long> groupTagIds = allGroups.stream()
                .map(MemberSceneSectionGroupDO::getGroupTagId)
                .collect(Collectors.toSet());

        List<MemberTagDO> groupTags = memberTagMapper.selectBatchIds(groupTagIds);
        Map<Long, MemberTagDO> groupTagMap = groupTags.stream()
                .collect(Collectors.toMap(MemberTagDO::getId, t -> t));

        List<MemberTagDO> leafTags = memberTagMapper.selectListByParentIds(new ArrayList<>(groupTagIds));
        Map<Long, List<MemberTagDO>> leafTagMap = leafTags.stream()
                .collect(Collectors.groupingBy(MemberTagDO::getParentId));

        // 4. 查用户已选标签（只取本表单涉及的标签）
        Set<Long> allLeafIds = leafTags.stream().map(MemberTagDO::getId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(allLeafIds)) {
            // 无叶子标签时直接返回区块结构（空子分组）
            return sections.stream()
                    .map(s -> new AppSocialSceneFormRespVO(s.getCode(), s.getSectionName(), Collections.emptyList()))
                    .collect(toList());
        }

        List<MemberUserTagDO> userTags = memberUserTagMapper.selectListByUserIdAndTagIds(userId, new ArrayList<>(allLeafIds));
        Set<Long> selectedTagIds = userTags.stream()
                .map(MemberUserTagDO::getTagId)
                .collect(Collectors.toSet());

        // 5. 组装 3 层结构
        List<AppSocialSceneFormRespVO> result = new ArrayList<>();
        for (MemberSceneSectionDO section : sections) {
            List<MemberSceneSectionGroupDO> sectionGroups = groupMap.getOrDefault(section.getId(), Collections.emptyList());
            List<AppSocialSceneFormRespVO.SubGroup> subGroups = new ArrayList<>();

            for (MemberSceneSectionGroupDO sg : sectionGroups) {
                MemberTagDO groupTag = groupTagMap.get(sg.getGroupTagId());
                if (groupTag == null) continue;

                List<MemberTagDO> children = leafTagMap.getOrDefault(sg.getGroupTagId(), Collections.emptyList());
                List<AppSocialSceneFormRespVO.TagItem> tags = children.stream()
                        .map(t -> new AppSocialSceneFormRespVO.TagItem(
                                t.getId(), t.getCode(), t.getTagName(),
                                selectedTagIds.contains(t.getId()), t.getSort()))
                        .collect(toList());

                subGroups.add(new AppSocialSceneFormRespVO.SubGroup(
                        groupTag.getTagName(), groupTag.getCode(), tags));
            }

            result.add(new AppSocialSceneFormRespVO(section.getCode(), section.getSectionName(), subGroups));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveMatchConfig(String sceneCode, Long userId, List<Long> tagIds, Map<String, Object> extraFields) {
        // 保存标签（覆盖式）
        batchSaveUserTag(userId, tagIds, "self");
        // 额外字段在创建匹配任务时写入 matchConfig
        // 此处仅持久化标签，不创建任务
        return true;
    }

    @Override
    public Map<String, Object> getMatchConfig(String sceneCode, Long userId) {
        // 从最新匹配任务读取 extraFields
        // 需要 member_scene 表支撑 sceneCode→sceneId 映射
        // 目前 sceneId 为数字，暂返回空
        return Collections.emptyMap();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveUserTag(Long userId, List<Long> tagIds, String source) {
        // 先删该来源的所有标签
        memberUserTagMapper.delete(new LambdaQueryWrapperX<MemberUserTagDO>()
                .eq(MemberUserTagDO::getUserId, userId)
                .eq(MemberUserTagDO::getSource, source));

        // 再批量插入
        if (CollUtil.isNotEmpty(tagIds)) {
            List<MemberUserTagDO> list = tagIds.stream()
                    .map(tagId -> MemberUserTagDO.builder()
                            .userId(userId)
                            .tagId(tagId)
                            .source(source)
                            .build())
                    .collect(toList());
            list.forEach(memberUserTagMapper::insert);
        }
    }

    @Override
    public List<Long> getUserTagIds(Long userId) {
        List<MemberUserTagDO> list = memberUserTagMapper.selectListByUserId(userId);
        return list.stream().map(MemberUserTagDO::getTagId).collect(toList());
    }

}
