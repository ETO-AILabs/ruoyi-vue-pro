package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialRegionTreeRespVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialSceneFormDataVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialSceneFormRespVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialUserDetailRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneDO;
import cn.iocoder.yudao.module.member.dal.dataobject.tag.MemberTagDO;

import java.util.List;
import java.util.Map;

public interface SocialService {

    /**
     * 获取用户可见的场景列表
     */
    List<MemberSceneDO> getSceneList(Long userId);

    /**
     * 搜索学校
     */
    List<MemberGroupDO> searchSchool(String name);

    /**
     * 获取可用标签列表
     *
     * @param category 分类编码，null 表示全部
     */
    List<MemberTagDO> getTagList(String category);

    /**
     * 根据父级分类编码获取子标签列表
     *
     * @param category 分类编码，如 profession
     * @return 子标签列表
     */
    List<MemberTagDO> getTagChildrenByCategory(String category);

    /**
     * 获取用户社交主页信息
     */
    AppSocialUserDetailRespVO getUserDetail(Long userId);

    /**
     * 获取场景表单（区块标题 → 子分组 → 标签列表）
     */
    AppSocialSceneFormDataVO getSceneForm(String sceneCode, Long userId);

    /**
     * 保存场景匹配配置（标签选中 + 额外字段）
     */
    Boolean saveMatchConfig(String sceneCode, Long userId, List<Long> tagIds, Map<String, Object> extraFields);

    /**
     * 获取上次额外字段
     */
    Map<String, Object> getMatchConfig(String sceneCode, Long userId);

    /**
     * 批量保存用户标签（覆盖式）
     */
    void batchSaveUserTag(Long userId, List<Long> tagIds, String source);

    /**
     * 获取用户标签ID列表
     */
    List<Long> getUserTagIds(Long userId);

    /**
     * 获取省市区树
     */
    List<AppSocialRegionTreeRespVO> getRegionTree();

}
