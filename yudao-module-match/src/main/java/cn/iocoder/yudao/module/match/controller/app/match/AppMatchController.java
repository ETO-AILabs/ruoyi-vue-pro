package cn.iocoder.yudao.module.match.controller.app.match;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.match.controller.app.match.vo.AvatarProfileSaveReqVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.AvatarProfileVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.CandidateCardVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.LikeReqVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.MatchResultItemVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.PassReqVO;
import cn.iocoder.yudao.module.match.service.match.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * MatchAI APP - 匹配（AI 分身 / 推荐 / 喜欢 / 跳过 / 结果）
 *
 * 统一前缀 /app-api 由 yudao-spring-boot-starter-web 的 WebProperties 自动拼接
 * （controller 包匹配 **.controller.app.**），故此处实际路径为 /app-api/match/*。
 */
@Tag(name = "MatchAI APP - 匹配")
@RestController
@RequestMapping("/match")
@Validated
@Slf4j
public class AppMatchController {

    @Resource
    private MatchService matchService;

    @GetMapping("/avatar")
    @Operation(summary = "获得当前用户的 AI 分身")
    public CommonResult<AvatarProfileVO> getAvatar() {
        return success(matchService.getAvatar(getLoginUserId()));
    }

    @PostMapping("/avatar")
    @Operation(summary = "保存当前用户的 AI 分身")
    public CommonResult<Long> saveAvatar(@RequestBody @Valid AvatarProfileSaveReqVO reqVO) {
        return success(matchService.saveAvatar(getLoginUserId(), reqVO));
    }

    @GetMapping("/candidates")
    @Operation(summary = "获得推荐候选人（分页）")
    public CommonResult<PageResult<CandidateCardVO>> getCandidates(PageParam pageParam) {
        return success(matchService.getCandidates(getLoginUserId(), pageParam));
    }

    @PostMapping("/like")
    @Operation(summary = "喜欢候选人")
    public CommonResult<Boolean> like(@RequestBody @Valid LikeReqVO reqVO) {
        return success(matchService.like(getLoginUserId(), reqVO.getCandidateId()));
    }

    @PostMapping("/pass")
    @Operation(summary = "跳过候选人")
    public CommonResult<Boolean> pass(@RequestBody @Valid PassReqVO reqVO) {
        return success(matchService.pass(getLoginUserId(), reqVO.getCandidateId()));
    }

    @GetMapping("/results")
    @Operation(summary = "获得匹配结果（分页）")
    public CommonResult<PageResult<MatchResultItemVO>> getResults(PageParam pageParam) {
        return success(matchService.getResults(getLoginUserId(), pageParam));
    }

}
