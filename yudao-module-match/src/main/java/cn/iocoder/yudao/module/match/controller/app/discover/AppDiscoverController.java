package cn.iocoder.yudao.module.match.controller.app.discover;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.match.controller.app.discover.vo.SchoolVO;
import cn.iocoder.yudao.module.match.controller.app.discover.vo.TribeVO;
import cn.iocoder.yudao.module.match.service.discover.DiscoverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * MatchAI APP - 校园发现（部落 / 学校）
 *
 * 统一前缀 /app-api 自动拼接，实际路径为 /app-api/discover/*。
 * 部落、学校为公开接口，使用 @PermitAll 免登录（YudaoWebSecurityConfigurerAdapter 自动收集）。
 */
@Tag(name = "MatchAI APP - 校园发现")
@RestController
@RequestMapping("/discover")
@Validated
@Slf4j
public class AppDiscoverController {

    @Resource
    private DiscoverService discoverService;

    @GetMapping("/tribes")
    @PermitAll
    @Operation(summary = "获得部落列表（分页）")
    public CommonResult<PageResult<TribeVO>> getTribes(PageParam pageParam) {
        return success(discoverService.getTribes(pageParam));
    }

    @GetMapping("/schools")
    @PermitAll
    @Operation(summary = "获得学校列表（onboarding 学校选择用）")
    public CommonResult<List<SchoolVO>> getSchools() {
        return success(discoverService.getSchools());
    }

}
