package cn.iocoder.yudao.module.member.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialRegionTreeRespVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialSceneFormDataVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialSceneFormRespVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialUserDetailRespVO;
import cn.iocoder.yudao.module.member.convert.social.SocialConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneSectionDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneSectionGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberUserTagDO;
import cn.iocoder.yudao.module.member.dal.dataobject.tag.MemberTagDO;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.group.MemberGroupMapper;
import cn.iocoder.yudao.module.member.dal.mysql.tag.MemberTagMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberSceneMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberSceneSectionMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberSceneSectionGroupMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberUserSceneMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberUserTagMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberMatchTaskMapper;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.ip.core.Area;
import cn.iocoder.yudao.framework.ip.core.enums.AreaTypeEnum;
import cn.iocoder.yudao.framework.ip.core.utils.AreaUtils;
import cn.iocoder.yudao.module.member.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
    public List<MemberTagDO> getTagList(String category) {
        if (StrUtil.isBlank(category)) {
            return memberTagMapper.selectListByStatus(1);
        }
        return memberTagMapper.selectListByCategory(category);
    }

    @Override
    public List<MemberTagDO> getTagChildrenByCategory(String category) {
        // 1. 先查根标签
        List<MemberTagDO> rootTags = memberTagMapper.selectListByCategory(category);
        if (CollUtil.isEmpty(rootTags)) {
            return Collections.emptyList();
        }
        // 2. 再查所有子标签
        List<Long> parentIds = rootTags.stream().map(MemberTagDO::getId).collect(Collectors.toList());
        return memberTagMapper.selectListByParentIds(parentIds);
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
            // 过滤掉职业标签（职业单独处理）
            tagIdList = userTags.stream()
                    .filter(t -> !"profession".equals(t.getSource()))
                    .map(MemberUserTagDO::getTagId)
                    .collect(toList());
        } else {
            tagIdList = user.getTagIds();
        }
        if (CollUtil.isNotEmpty(tagIdList)) {
            List<MemberTagDO> tags = memberTagMapper.selectBatchIds(tagIdList);
            resp.setTagNames(tags.stream().map(MemberTagDO::getTagName).collect(toList()));
        }

        // 处理职业标签（从 member_user_tag 中取 source=profession）
        List<MemberUserTagDO> professionTags = memberUserTagMapper.selectListByUserIdAndSource(userId, "profession");
        if (CollUtil.isNotEmpty(professionTags)) {
            Long professionTagId = professionTags.get(0).getTagId();
            MemberTagDO professionTag = memberTagMapper.selectById(professionTagId);
            if (professionTag != null) {
                resp.setProfession(professionTag.getTagName());
            }
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
    public AppSocialSceneFormDataVO getSceneForm(String sceneCode, Long userId) {
        // 1. 查用户基础信息
        MemberUserDO user = memberUserMapper.selectById(userId);
        String wechat = user != null ? user.getWechat() : null;
        String residence = user != null ? user.getResidence() : null;
        LocalDateTime birthday = user != null ? user.getBirthday() : null;
        Integer height = user != null ? user.getHeight() : null;
        String hometown = user != null ? user.getHometown() : null;
        String mbti = user != null ? user.getMbti() : null;
        Integer income = user != null ? user.getIncome() : null;

        // 1b. 查最新匹配任务，提取 aboutMe/idealTa（love 场景回显）
        String aboutMe = null;
        String idealTa = null;
        try {
            MemberSceneDO scene = memberSceneMapper.selectOne(
                    new LambdaQueryWrapperX<MemberSceneDO>()
                            .eq(MemberSceneDO::getSceneCode, sceneCode));
            if (scene != null) {
                MemberMatchTaskDO latestTask = memberMatchTaskMapper
                        .selectLatestByUserIdAndSceneId(userId, scene.getId());
                if (latestTask != null && StrUtil.isNotBlank(latestTask.getMatchConfig())) {
                    JSONObject configObj = JSONUtil.parseObj(latestTask.getMatchConfig());
                    JSONObject extraFields = configObj.getJSONObject("extraFields");
                    if (extraFields != null) {
                        aboutMe = extraFields.getStr("aboutMe");
                        idealTa = extraFields.getStr("idealTa");
                    }
                }
            }
        } catch (Exception e) {
            // ignore parse error
        }

        // 2. 查场景下所有区块
        List<MemberSceneSectionDO> sections = memberSceneSectionMapper.selectListBySceneCode(sceneCode);
        if (CollUtil.isEmpty(sections)) {
            AppSocialSceneFormDataVO vo = new AppSocialSceneFormDataVO(Collections.emptyList(), wechat, residence,
                    birthday, height, hometown, mbti, income, null, null);
            vo.setAboutMe(aboutMe);
            vo.setIdealTa(idealTa);
            return vo;
        }
        List<Long> sectionIds = sections.stream().map(MemberSceneSectionDO::getId).collect(toList());

        // 3. 查所有区块关联的子分组
        List<MemberSceneSectionGroupDO> allGroups = memberSceneSectionGroupMapper.selectListBySectionIds(sectionIds);
        Map<Long, List<MemberSceneSectionGroupDO>> groupMap = allGroups.stream()
                .collect(Collectors.groupingBy(MemberSceneSectionGroupDO::getSectionId));

        // 4. 收集所有 group_tag_id → 查 group标签 + 子标签
        Set<Long> groupTagIds = allGroups.stream()
                .map(MemberSceneSectionGroupDO::getGroupTagId)
                .collect(Collectors.toSet());

        List<MemberTagDO> groupTags = memberTagMapper.selectBatchIds(groupTagIds);
        Map<Long, MemberTagDO> groupTagMap = groupTags.stream()
                .collect(Collectors.toMap(MemberTagDO::getId, t -> t));

        List<MemberTagDO> leafTags = memberTagMapper.selectListByParentIds(new ArrayList<>(groupTagIds));
        Map<Long, List<MemberTagDO>> leafTagMap = leafTags.stream()
                .collect(Collectors.groupingBy(MemberTagDO::getParentId));

        // 4b. 技能场景（i_can_teach / i_want_learn）需要 3 级：level-2(subGroup) -> level-3(subSubGroup) -> level-4(tag)
        // 这里多查一层 level-4（数字插画、传统绘画...）
        Map<Long, List<MemberTagDO>> skillLevel4Map = new HashMap<>();
        Set<Long> skillLevel4Ids = new HashSet<>();
        boolean isSkillScene = sections.stream().anyMatch(s ->
                "i_can_teach".equals(s.getCode()) || "i_want_learn".equals(s.getCode()));
        if (isSkillScene && !leafTags.isEmpty()) {
            Set<Long> level3Ids = leafTags.stream().map(MemberTagDO::getId).collect(Collectors.toSet());
            List<MemberTagDO> level4Tags = memberTagMapper.selectListByParentIds(new ArrayList<>(level3Ids));
            skillLevel4Map = level4Tags.stream()
                    .collect(Collectors.groupingBy(MemberTagDO::getParentId));
            skillLevel4Ids = level4Tags.stream().map(MemberTagDO::getId).collect(Collectors.toSet());
        }

        // 5. 查用户已选标签（只取本表单涉及的标签）
        Set<Long> allLeafIds = new HashSet<>(leafTags.stream().map(MemberTagDO::getId).collect(Collectors.toSet()));
        allLeafIds.addAll(skillLevel4Ids);
        if (CollUtil.isEmpty(allLeafIds)) {
            // 无叶子标签时直接返回区块结构（空子分组）
            List<AppSocialSceneFormRespVO> emptySections = sections.stream()
                    .map(s -> new AppSocialSceneFormRespVO(s.getCode(), s.getSectionName(), Collections.emptyList()))
                    .collect(toList());
            AppSocialSceneFormDataVO vo = new AppSocialSceneFormDataVO(emptySections, wechat, residence,
                    birthday, height, hometown, mbti, income, null, null);
            vo.setAboutMe(aboutMe);
            vo.setIdealTa(idealTa);
            return vo;
        }

        List<MemberUserTagDO> userTags = memberUserTagMapper.selectListByUserIdAndTagIds(userId, new ArrayList<>(allLeafIds));
        Set<Long> selectedTagIds = userTags.stream()
                .map(MemberUserTagDO::getTagId)
                .collect(Collectors.toSet());

        // 6. 组装结构（personality 3级，其余2级）
        List<AppSocialSceneFormRespVO> result = new ArrayList<>();
        for (MemberSceneSectionDO section : sections) {
            List<MemberSceneSectionGroupDO> sectionGroups = groupMap.getOrDefault(section.getId(), Collections.emptyList());
            boolean isPersonality = "personality".equals(section.getCode());

            if (isPersonality) {
                // === 3级组装：子分组按 parentId 归属父标签下 ===
                Set<Long> childIds = sectionGroups.stream()
                        .map(MemberSceneSectionGroupDO::getGroupTagId).collect(Collectors.toSet());
                Map<Long, MemberTagDO> childTagMap = memberTagMapper.selectBatchIds(childIds).stream()
                        .collect(Collectors.toMap(MemberTagDO::getId, t -> t));

                Set<Long> parentIds = childTagMap.values().stream()
                        .map(MemberTagDO::getParentId).filter(pid -> pid != null && pid != 0)
                        .collect(Collectors.toSet());
                Map<Long, MemberTagDO> parentTagMap = !parentIds.isEmpty()
                        ? memberTagMapper.selectBatchIds(parentIds).stream()
                            .collect(Collectors.toMap(MemberTagDO::getId, t -> t))
                        : Collections.emptyMap();

                // 分组：有 parentId 的归到父下，无父标签的独立
                Map<Long, List<MemberSceneSectionGroupDO>> childrenByParent = new HashMap<>();
                List<MemberSceneSectionGroupDO> orphanGroups = new ArrayList<>();
                for (MemberSceneSectionGroupDO sg : sectionGroups) {
                    MemberTagDO t = childTagMap.get(sg.getGroupTagId());
                    if (t != null && t.getParentId() != null && t.getParentId() != 0) {
                        childrenByParent.computeIfAbsent(t.getParentId(), k -> new ArrayList<>()).add(sg);
                    } else {
                        orphanGroups.add(sg);
                    }
                }

                List<AppSocialSceneFormRespVO.SubGroup> sgResult = new ArrayList<>();

                // 父标签 -> SubGroup(subSubGroups)
                for (Map.Entry<Long, List<MemberSceneSectionGroupDO>> e : childrenByParent.entrySet()) {
                    MemberTagDO parentTag = parentTagMap.get(e.getKey());
                    if (parentTag == null) continue;
                    List<AppSocialSceneFormRespVO.SubSubGroup> ssg = new ArrayList<>();
                    for (MemberSceneSectionGroupDO child : e.getValue()) {
                        MemberTagDO ct = childTagMap.get(child.getGroupTagId());
                        if (ct == null) continue;
                        List<MemberTagDO> leaves = leafTagMap.getOrDefault(child.getGroupTagId(), Collections.emptyList());
                        List<AppSocialSceneFormRespVO.TagItem> tags = leaves.stream()
                                .map(tag -> new AppSocialSceneFormRespVO.TagItem(tag.getId(), tag.getCode(), tag.getTagName(),
                                        selectedTagIds.contains(tag.getId()), tag.getSort()))
                                .collect(toList());
                        ssg.add(new AppSocialSceneFormRespVO.SubSubGroup(ct.getTagName(), ct.getCode(), tags));
                    }
                    sgResult.add(new AppSocialSceneFormRespVO.SubGroup(parentTag.getTagName(), parentTag.getCode(), null, ssg));
                }

                // 孤儿子分组（无父标签，直接挂 tags）
                for (MemberSceneSectionGroupDO sg : orphanGroups) {
                    MemberTagDO gt = childTagMap.get(sg.getGroupTagId());
                    if (gt == null) continue;
                    List<MemberTagDO> leaves = leafTagMap.getOrDefault(sg.getGroupTagId(), Collections.emptyList());
                    List<AppSocialSceneFormRespVO.TagItem> tags = leaves.stream()
                            .map(tag -> new AppSocialSceneFormRespVO.TagItem(tag.getId(), tag.getCode(), tag.getTagName(),
                                    selectedTagIds.contains(tag.getId()), tag.getSort()))
                            .collect(toList());
                    sgResult.add(new AppSocialSceneFormRespVO.SubGroup(gt.getTagName(), gt.getCode(), tags, null));
                }

                result.add(new AppSocialSceneFormRespVO(section.getCode(), section.getSectionName(), sgResult));

            } else if ("i_can_teach".equals(section.getCode()) || "i_want_learn".equals(section.getCode())) {
                // === 技能场景：3 级结构（tab → accordion → chips）===
                // section = 我会的技能 / 我想学的技能（根，不展示为 tab）
                // subGroup = level-2（创意/商业/技术/生活/教学）→ 前端 tab
                // subSubGroup = level-3（插画与绘画、视觉设计...）→ 前端 accordion
                // tags    = level-4（数字插画、传统绘画...）→ 前端 chips
                List<AppSocialSceneFormRespVO.SubGroup> subGroups = new ArrayList<>();
                for (MemberSceneSectionGroupDO sg : sectionGroups) {
                    MemberTagDO groupTag = groupTagMap.get(sg.getGroupTagId());
                    if (groupTag == null) continue;
                    // groupTag 是 level-2（创意技能），它的子标签是 level-3
                    List<MemberTagDO> level3List = leafTagMap.getOrDefault(sg.getGroupTagId(), Collections.emptyList());
                    List<AppSocialSceneFormRespVO.SubSubGroup> ssgList = new ArrayList<>();
                    for (MemberTagDO l3 : level3List) {
                        // l3 是 level-3（插画与绘画），它的子标签是 level-4（数字插画...）
                        List<MemberTagDO> leaves = skillLevel4Map.getOrDefault(l3.getId(), Collections.emptyList());
                        List<AppSocialSceneFormRespVO.TagItem> tags = leaves.stream()
                                .map(t -> new AppSocialSceneFormRespVO.TagItem(t.getId(), t.getCode(), t.getTagName(),
                                        selectedTagIds.contains(t.getId()), t.getSort()))
                                .collect(toList());
                        ssgList.add(new AppSocialSceneFormRespVO.SubSubGroup(l3.getTagName(), l3.getCode(), tags));
                    }
                    // subGroup 用 level-2，subSubGroups 挂 level-3，tags 挂 level-4
                    subGroups.add(new AppSocialSceneFormRespVO.SubGroup(groupTag.getTagName(), groupTag.getCode(), null, ssgList));
                }
                result.add(new AppSocialSceneFormRespVO(section.getCode(), section.getSectionName(), subGroups));

            } else {
                // 原有二级逻辑
                List<AppSocialSceneFormRespVO.SubGroup> subGroups = new ArrayList<>();
                for (MemberSceneSectionGroupDO sg : sectionGroups) {
                    MemberTagDO groupTag = groupTagMap.get(sg.getGroupTagId());
                    if (groupTag == null) continue;
                    List<MemberTagDO> children = leafTagMap.getOrDefault(sg.getGroupTagId(), Collections.emptyList());
                    List<AppSocialSceneFormRespVO.TagItem> tags = children.stream()
                            .map(t -> new AppSocialSceneFormRespVO.TagItem(t.getId(), t.getCode(), t.getTagName(),
                                    selectedTagIds.contains(t.getId()), t.getSort()))
                            .collect(toList());
                    subGroups.add(new AppSocialSceneFormRespVO.SubGroup(groupTag.getTagName(), groupTag.getCode(), tags, null));
                }
                result.add(new AppSocialSceneFormRespVO(section.getCode(), section.getSectionName(), subGroups));
            }
        }

        AppSocialSceneFormDataVO vo = new AppSocialSceneFormDataVO(result, wechat, residence,
                birthday, height, hometown, mbti, income, null, null);
        vo.setAboutMe(aboutMe);
        vo.setIdealTa(idealTa);
        return vo;
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

    @Override
    public List<AppSocialRegionTreeRespVO> getRegionTree() {
        List<Area> provinces = AreaUtils.getByType(AreaTypeEnum.PROVINCE, area -> area);
        // 过滤港澳台（id >= 810000）
        return provinces.stream()
                .filter(p -> p.getId() < 810000)
                .map(this::toRegionVO)
                .collect(toList());
    }

    private AppSocialRegionTreeRespVO toRegionVO(Area area) {
        AppSocialRegionTreeRespVO vo = new AppSocialRegionTreeRespVO();
        vo.setId(area.getId());
        vo.setName(area.getName());
        if (area.getChildren() != null && !area.getChildren().isEmpty()) {
            vo.setChildren(area.getChildren().stream()
                    .map(this::toRegionVO)
                    .collect(toList()));
        }
        return vo;
    }

}
