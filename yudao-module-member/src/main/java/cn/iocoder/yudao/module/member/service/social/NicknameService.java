package cn.iocoder.yudao.module.member.service.social;

import javax.validation.constraints.NotNull;

public interface NicknameService {

    /**
     * 生成昵称
     *
     * @param userId 用户ID
     * @return 生成的昵称
     */
    @NotNull
    String generateNickname(Long userId);

}
