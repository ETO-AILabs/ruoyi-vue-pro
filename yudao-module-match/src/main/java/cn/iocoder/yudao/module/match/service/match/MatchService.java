package cn.iocoder.yudao.module.match.service.match;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.match.controller.app.match.vo.AvatarProfileSaveReqVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.AvatarProfileVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.CandidateCardVO;
import cn.iocoder.yudao.module.match.controller.app.match.vo.MatchResultItemVO;

/**
 * MatchAI APP - 匹配 Service 接口
 *
 * 当前为内存 Map 实现（见 {@link MatchServiceImpl}），后续可切换为基于
 * yudao-module-match/dal/mysql 下 Mapper 的数据库实现。
 */
public interface MatchService {

    /**
     * 获得当前用户的 AI 分身
     *
     * @param userId 用户编号
     * @return 分身 Profile；若未创建返回 null
     */
    AvatarProfileVO getAvatar(Long userId);

    /**
     * 保存当前用户的 AI 分身（不存在则创建）
     *
     * @param userId 用户编号
     * @param reqVO  分身 Profile
     * @return 分身编号（当前为 userId）
     */
    Long saveAvatar(Long userId, AvatarProfileSaveReqVO reqVO);

    /**
     * 获得推荐候选人列表（分页，按匹配度降序）
     *
     * @param userId 用户编号
     * @param pageParam 分页参数
     * @return 候选人卡片分页
     */
    PageResult<CandidateCardVO> getCandidates(Long userId, PageParam pageParam);

    /**
     * 喜欢候选人。若候选人已喜欢你，则产生一条匹配结果。
     *
     * @param userId      用户编号
     * @param candidateId 候选人编号
     * @return true
     */
    Boolean like(Long userId, Long candidateId);

    /**
     * 跳过候选人
     *
     * @param userId      用户编号
     * @param candidateId 候选人编号
     * @return true
     */
    Boolean pass(Long userId, Long candidateId);

    /**
     * 获得匹配结果列表（分页）
     *
     * @param userId 用户编号
     * @param pageParam 分页参数
     * @return 匹配结果分页
     */
    PageResult<MatchResultItemVO> getResults(Long userId, PageParam pageParam);

}
