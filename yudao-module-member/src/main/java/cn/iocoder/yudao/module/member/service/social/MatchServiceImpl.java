package cn.iocoder.yudao.module.member.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchSceneRespVO;
import cn.iocoder.yudao.module.member.convert.social.SocialConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskResultDO;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberMatchTaskMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberMatchTaskResultMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberUserTagMapper;
import cn.iocoder.yudao.module.member.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
@Validated
public class MatchServiceImpl implements MatchService {

    /**
     * 匹配冷却时间（小时）
     */
    private static final int MATCH_COOLDOWN_HOURS = 24;

    /**
     * 匹配结果可见时间（小时）
     */
    private static final int MATCH_RESULT_VISIBLE_HOURS = 12;

    @Resource
    private MemberMatchTaskMapper memberMatchTaskMapper;
    @Resource
    private MemberMatchTaskResultMapper memberMatchTaskResultMapper;
    @Resource
    private MemberUserTagMapper memberUserTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMatchTask(Long userId, AppSocialMatchCreateReqVO reqVO) {
        // 1. 校验同场景是否有进行中的任务
        MemberMatchTaskDO pendingTask = memberMatchTaskMapper
                .selectByUserIdAndSceneIdAndStatusIn(userId, reqVO.getSceneId(),
                        Arrays.asList(0, 1));
        if (pendingTask != null) {
            throw exception(ErrorCodeConstants.MATCH_TASK_EXISTS);
        }

        // 2. 校验冷却时间
        MemberMatchTaskDO latestTask = memberMatchTaskMapper
                .selectLatestByUserIdAndSceneId(userId, reqVO.getSceneId());
        if (latestTask != null) {
            LocalDateTime cooldownTime = latestTask.getCreateTime()
                    .plusHours(MATCH_COOLDOWN_HOURS);
            if (LocalDateTime.now().isBefore(cooldownTime)) {
                throw exception(ErrorCodeConstants.MATCH_COOLDOWN, MATCH_COOLDOWN_HOURS);
            }
        }

        // 3. 保存标签（覆盖式）
        if (CollUtil.isNotEmpty(reqVO.getTagIds())) {
            // 先删 self 来源标签
            memberUserTagMapper.delete(new LambdaQueryWrapperX<cn.iocoder.yudao.module.member.dal.dataobject.social.MemberUserTagDO>()
                    .eq(cn.iocoder.yudao.module.member.dal.dataobject.social.MemberUserTagDO::getUserId, userId)
                    .eq(cn.iocoder.yudao.module.member.dal.dataobject.social.MemberUserTagDO::getSource, "self"));
            // 再插入
            for (Long tagId : reqVO.getTagIds()) {
                memberUserTagMapper.insert(cn.iocoder.yudao.module.member.dal.dataobject.social.MemberUserTagDO.builder()
                        .userId(userId)
                        .tagId(tagId)
                        .source("self")
                        .build());
            }
        }

        // 4. 构造 matchConfig
        String matchConfig = null;
        if (reqVO.getExtraFields() != null) {
            Map<String, Object> config = new HashMap<>();
            config.put("extraFields", reqVO.getExtraFields());
            matchConfig = JSONUtil.toJsonStr(config);
        }

        // 5. 创建匹配任务
        MemberMatchTaskDO task = MemberMatchTaskDO.builder()
                .userId(userId)
                .sceneId(reqVO.getSceneId())
                .matchGoal(reqVO.getMatchGoal())
                .matchRemark(reqVO.getMatchRemark())
                .matchConfig(matchConfig)
                .matchStatus(0) // 未开始
                .build();
        memberMatchTaskMapper.insert(task);
        return task.getId();
    }

    @Override
    public PageResult<MemberMatchTaskDO> getMatchTaskPage(Long userId, AppSocialMatchPageReqVO reqVO) {
        return memberMatchTaskMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<MemberMatchTaskDO>()
                        .eq(MemberMatchTaskDO::getUserId, userId)
                        .orderByDesc(MemberMatchTaskDO::getCreateTime));
    }

    @Override
    public AppSocialMatchSceneRespVO getMatchSceneData(Long userId, Long sceneId) {
        MemberMatchTaskDO latest = memberMatchTaskMapper
                .selectLatestByUserIdAndSceneId(userId, sceneId);

        // 无任务 → 待匹配
        if (latest == null) {
            return new AppSocialMatchSceneRespVO(0, null);
        }

        LocalDateTime now = LocalDateTime.now();

        // 未开始/进行中 → 匹配中
        if (latest.getMatchStatus() == 0 || latest.getMatchStatus() == 1) {
            return new AppSocialMatchSceneRespVO(1, null);
        }

        // 成功且在12h可见窗口内 → 匹配完成（带结果）
        if (latest.getMatchStatus() == 3
                && latest.getCreateTime().plusHours(MATCH_RESULT_VISIBLE_HOURS).isAfter(now)) {
            List<MemberMatchTaskResultDO> results = memberMatchTaskResultMapper
                    .selectListByTaskId(latest.getId());
            return new AppSocialMatchSceneRespVO(2,
                    SocialConvert.INSTANCE.convertMatchResultList(results));
        }

        // 24h冷却期内 → 冷却中
        if (latest.getCreateTime().plusHours(MATCH_COOLDOWN_HOURS).isAfter(now)) {
            return new AppSocialMatchSceneRespVO(3, null);
        }

        // 冷却已过 → 待匹配
        return new AppSocialMatchSceneRespVO(0, null);
    }

}
