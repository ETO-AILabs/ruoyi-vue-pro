package cn.iocoder.yudao.module.member.controller.admin.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.social.vo.*;
import cn.iocoder.yudao.module.member.convert.social.SocialConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberSceneDO;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberSceneMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 社交场景")
@RestController
@RequestMapping("/member/social/scene")
@Validated
public class SceneController {

    @Resource
    private MemberSceneMapper memberSceneMapper;

    @PostMapping("/create")
    @Operation(summary = "创建场景")
    @PreAuthorize("@ss.hasPermission('member:social:scene:create')")
    public CommonResult<Long> createScene(@Valid @RequestBody SceneCreateReqVO reqVO) {
        MemberSceneDO scene = MemberSceneDO.builder()
                .sceneCode(reqVO.getSceneCode())
                .sceneType(reqVO.getSceneType())
                .sceneName(reqVO.getSceneName())
                .sceneStatus(reqVO.getSceneStatus())
                .build();
        memberSceneMapper.insert(scene);
        return success(scene.getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新场景")
    @PreAuthorize("@ss.hasPermission('member:social:scene:update')")
    public CommonResult<Boolean> updateScene(@Valid @RequestBody SceneUpdateReqVO reqVO) {
        MemberSceneDO scene = MemberSceneDO.builder()
                .id(reqVO.getId())
                .sceneCode(reqVO.getSceneCode())
                .sceneType(reqVO.getSceneType())
                .sceneName(reqVO.getSceneName())
                .sceneStatus(reqVO.getSceneStatus())
                .build();
        memberSceneMapper.updateById(scene);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除场景")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:social:scene:delete')")
    public CommonResult<Boolean> deleteScene(@RequestParam("id") Long id) {
        memberSceneMapper.deleteById(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得场景")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:social:scene:query')")
    public CommonResult<SceneRespVO> getScene(@RequestParam("id") Long id) {
        MemberSceneDO scene = memberSceneMapper.selectById(id);
        // 手动转换
        SceneRespVO resp = new SceneRespVO();
        if (scene != null) {
            resp.setId(scene.getId());
            resp.setSceneCode(scene.getSceneCode());
            resp.setSceneType(scene.getSceneType());
            resp.setSceneName(scene.getSceneName());
            resp.setSceneStatus(scene.getSceneStatus());
            resp.setCreateTime(scene.getCreateTime());
        }
        return success(resp);
    }

    @GetMapping("/page")
    @Operation(summary = "获得场景分页")
    @PreAuthorize("@ss.hasPermission('member:social:scene:query')")
    public CommonResult<PageResult<SceneRespVO>> getScenePage(@Valid ScenePageReqVO reqVO) {
        PageResult<MemberSceneDO> page = memberSceneMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<MemberSceneDO>()
                        .likeIfPresent(MemberSceneDO::getSceneName, reqVO.getSceneName())
                        .eqIfPresent(MemberSceneDO::getSceneType, reqVO.getSceneType())
                        .eqIfPresent(MemberSceneDO::getSceneStatus, reqVO.getSceneStatus())
                        .orderByDesc(MemberSceneDO::getId));
        // 手动转换
        PageResult<SceneRespVO> result = new PageResult<>();
        result.setList(page.getList().stream().map(scene -> {
            SceneRespVO resp = new SceneRespVO();
            resp.setId(scene.getId());
            resp.setSceneCode(scene.getSceneCode());
            resp.setSceneType(scene.getSceneType());
            resp.setSceneName(scene.getSceneName());
            resp.setSceneStatus(scene.getSceneStatus());
            resp.setCreateTime(scene.getCreateTime());
            return resp;
        }).collect(Collectors.toList()));
        result.setTotal(page.getTotal());
        return success(result);
    }

}
