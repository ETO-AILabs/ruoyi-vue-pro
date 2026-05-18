package cn.iocoder.yudao.module.member.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberAvatarsDO;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberAvatarsMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.hutool.core.util.RandomUtil.randomEle;

@Service
@Validated
public class AvatarServiceImpl implements AvatarService {

    @Resource
    private MemberAvatarsMapper memberAvatarsMapper;

    @Override
    public MemberAvatarsDO getRandomAvatar() {
        List<MemberAvatarsDO> list = memberAvatarsMapper.selectList();
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        return randomEle(list);
    }

}
