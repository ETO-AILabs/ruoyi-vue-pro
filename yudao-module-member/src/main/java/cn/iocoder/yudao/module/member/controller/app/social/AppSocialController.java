package cn.iocoder.yudao.module.member.controller.app.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.social.vo.*;
import cn.iocoder.yudao.module.member.convert.social.SocialConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneDO;
import cn.iocoder.yudao.module.member.dal.dataobject.tag.MemberTagDO;
import cn.iocoder.yudao.module.member.service.social.MatchService;
import cn.iocoder.yudao.module.member.service.social.SchoolVerifyService;
import cn.iocoder.yudao.module.member.service.social.SocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 社交")
@RestController
@RequestMapping("/member/social")
@Validated
public class AppSocialController {

    @Resource
    private SocialService socialService;
    @Resource
    private MatchService matchService;
    @Resource
    private SchoolVerifyService schoolVerifyService;

    // ========== 场景 ==========

    @GetMapping("/scene/list")
    @Operation(summary = "获取用户可见场景列表")
    public CommonResult<List<AppSocialSceneRespVO>> getSceneList() {
        List<MemberSceneDO> list = socialService.getSceneList(getLoginUserId());
        return success(SocialConvert.INSTANCE.convertSceneList(list));
    }

    // ========== 学校 ==========

    @GetMapping("/school/search")
    @Operation(summary = "搜索学校")
    @Parameter(name = "name", description = "学校名称", required = true)
    public CommonResult<List<AppSocialSchoolSearchRespVO>> searchSchool(@RequestParam("name") String name) {
        List<MemberGroupDO> list = socialService.searchSchool(name);
        return success(SocialConvert.INSTANCE.convertSchoolList(list));
    }

    @PostMapping("/school/verify")
    @Operation(summary = "提交学校认证申请")
    public CommonResult<Long> submitSchoolVerify(@RequestBody @Valid AppSocialSchoolVerifyReqVO reqVO) {
        return success(schoolVerifyService.submitVerify(getLoginUserId(), reqVO));
    }

    @GetMapping("/school/verify/status")
    @Operation(summary = "查询学校认证状态")
    public CommonResult<AppSocialSchoolVerifyRespVO> getSchoolVerifyStatus() {
        cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSchoolVerifyDO verify =
                schoolVerifyService.getVerifyByUserId(getLoginUserId());
        if (verify == null) {
            return success(new AppSocialSchoolVerifyRespVO(null, null));
        }
        // 获取学校名称
        String schoolName = null;
        if (verify.getGroupId() != null) {
            cn.iocoder.yudao.module.member.dal.dataobject.group.MemberGroupDO group =
                    socialService.searchSchool("").stream()
                            .filter(g -> g.getId().equals(verify.getGroupId()))
                            .findFirst().orElse(null);
            if (group != null) {
                schoolName = group.getName();
            }
        }
        return success(new AppSocialSchoolVerifyRespVO(verify.getVerifyStatus(), schoolName));
    }

    // ========== 标签 ==========

    @GetMapping("/tag/list")
    @Operation(summary = "获取预置标签列表（可选分类过滤）")
    @Parameter(name = "category", description = "分类编码，如 profession")
    public CommonResult<List<AppSocialTagRespVO>> getTagList(@RequestParam(value = "category", required = false) String category) {
        List<MemberTagDO> list = socialService.getTagList(category);
        return success(SocialConvert.INSTANCE.convertTagList(list));
    }

    @GetMapping("/tag/children")
    @Operation(summary = "获取指定分类下的子标签列表")
    @Parameter(name = "category", description = "分类编码", required = true, example = "profession")
    public CommonResult<List<AppSocialTagRespVO>> getTagChildren(@RequestParam("category") String category) {
        List<MemberTagDO> list = socialService.getTagChildrenByCategory(category);
        return success(SocialConvert.INSTANCE.convertTagList(list));
    }

    // ========== 地区 ==========

    @GetMapping("/region/tree")
    @Operation(summary = "获取省市区树（常住地选择器）")
    public CommonResult<List<AppSocialRegionTreeRespVO>> getRegionTree() {
        return success(socialService.getRegionTree());
    }

    // ========== 匹配 ==========

    @PostMapping("/match/create")
    @Operation(summary = "创建匹配任务")
    public CommonResult<Long> createMatch(@RequestBody @Valid AppSocialMatchCreateReqVO reqVO) {
        return success(matchService.createMatchTask(getLoginUserId(), reqVO));
    }

    @GetMapping("/match/task/page")
    @Operation(summary = "分页查询匹配记录")
    public CommonResult<PageResult<AppSocialMatchTaskRespVO>> getMatchTaskPage(AppSocialMatchPageReqVO reqVO) {
        PageResult<MemberMatchTaskDO> page = matchService.getMatchTaskPage(getLoginUserId(), reqVO);
        return success(SocialConvert.INSTANCE.convertMatchTaskPage(page));
    }

    @GetMapping("/match/scene/status")
    @Operation(summary = "获取指定场景的匹配状态和有效匹配结果")
    @Parameter(name = "sceneId", description = "场景ID", required = true)
    public CommonResult<AppSocialMatchSceneRespVO> getMatchSceneStatus(@RequestParam("sceneId") Long sceneId) {
        return success(matchService.getMatchSceneData(getLoginUserId(), sceneId));
    }

    // ========== 场景表单 ==========

    @GetMapping("/scene/form")
    @Operation(summary = "获取场景表单（含用户微信+常驻地）")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<AppSocialSceneFormDataVO> getSceneForm(@RequestParam("sceneCode") String sceneCode) {
        return success(socialService.getSceneForm(sceneCode, getLoginUserId()));
    }

    // ========== 匹配配置 ==========

    @PostMapping("/match/config/save")
    @Operation(summary = "保存场景匹配配置")
    public CommonResult<Boolean> saveMatchConfig(@RequestBody @Valid AppSocialMatchConfigSaveReqVO reqVO) {
        return success(socialService.saveMatchConfig(reqVO.getSceneCode(), getLoginUserId(),
                reqVO.getTagIds(), reqVO.getExtraFields()));
    }

    @GetMapping("/match/config/get")
    @Operation(summary = "获取上次额外字段")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<Map<String, Object>> getMatchConfig(@RequestParam("sceneCode") String sceneCode) {
        return success(socialService.getMatchConfig(sceneCode, getLoginUserId()));
    }

    // ========== 用户标签 ==========

    @PostMapping("/user-tag/batch-save")
    @Operation(summary = "批量保存用户标签(覆盖式)")
    public CommonResult<Boolean> batchSaveUserTag(@RequestBody @Valid AppSocialUserTagBatchSaveReqVO reqVO) {
        socialService.batchSaveUserTag(getLoginUserId(), reqVO.getTagIds(), reqVO.getSource());
        return success(true);
    }

    @GetMapping("/user-tag/list")
    @Operation(summary = "获取用户标签ID列表")
    public CommonResult<List<Long>> getUserTagList() {
        return success(socialService.getUserTagIds(getLoginUserId()));
    }

    // ========== 用户社交主页 ==========

    @GetMapping("/user/detail")
    @Operation(summary = "获取用户社交主页")
    @Parameter(name = "userId", description = "用户ID", required = true)
    public CommonResult<AppSocialUserDetailRespVO> getUserDetail(@RequestParam("userId") Long userId) {
        return success(socialService.getUserDetail(userId));
    }

}
