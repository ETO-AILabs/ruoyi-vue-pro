package cn.iocoder.yudao.module.match.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * MatchAI match 模块错误码枚举类
 *
 * 使用 1-020-000-000 段（未被其它模块占用）。
 */
public interface ErrorCodeConstants {

    // ========== AI 分身 1-020-001-000 ==========
    ErrorCode MATCH_AVATAR_NOT_EXISTS = new ErrorCode(1_020_001_000, "AI 分身不存在，请先完成个人资料");

    // ========== 匹配推荐 1-020-002-000 ==========
    ErrorCode MATCH_CANDIDATE_NOT_EXISTS = new ErrorCode(1_020_002_000, "推荐候选人不存在");
    ErrorCode MATCH_ALREADY_LIKED = new ErrorCode(1_020_002_001, "已喜欢过该候选人，请勿重复操作");
    ErrorCode MATCH_ALREADY_PASSED = new ErrorCode(1_020_002_002, "已跳过该候选人，请勿重复操作");

}
