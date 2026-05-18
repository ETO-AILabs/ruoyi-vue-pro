package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.social.vo.AppSocialMatchPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskDO;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberMatchTaskResultDO;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberMatchTaskMapper;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberMatchTaskResultMapper;
import cn.iocoder.yudao.module.member.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    @Override
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

        // 3. 创建匹配任务
        MemberMatchTaskDO task = MemberMatchTaskDO.builder()
                .userId(userId)
                .sceneId(reqVO.getSceneId())
                .matchGoal(reqVO.getMatchGoal())
                .matchRemark(reqVO.getMatchRemark())
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
    public List<MemberMatchTaskResultDO> getMatchResultList(Long userId) {
        // 1. 查询用户的匹配任务（匹配成功的）
        List<MemberMatchTaskDO> tasks = memberMatchTaskMapper.selectListByUserId(userId);
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 筛选12h有效期内的任务
        LocalDateTime now = LocalDateTime.now();
        List<Long> validTaskIds = tasks.stream()
                .filter(t -> t.getMatchStatus() == 3) // 匹配成功
                .filter(t -> t.getCreateTime().plusHours(MATCH_RESULT_VISIBLE_HOURS).isAfter(now))
                .map(MemberMatchTaskDO::getId)
                .toList();

        if (validTaskIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 查询匹配结果
        return memberMatchTaskResultMapper.selectListByTaskIdIn(validTaskIds);
    }

}
