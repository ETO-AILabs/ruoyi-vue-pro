package cn.iocoder.yudao.module.member.service.social;

import cn.iocoder.yudao.module.member.framework.ai.DeepSeekProperties;
import cn.iocoder.yudao.module.member.dal.dataobject.social.MemberNicknameRecordDO;
import cn.iocoder.yudao.module.member.dal.mysql.social.MemberNicknameRecordMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.NICKNAME_GEN_LIMIT;

@Service
@Validated
@Slf4j
public class NicknameServiceImpl implements NicknameService {

    private static final int DAILY_LIMIT = 3;
    private static final List<String> FALLBACK_NAMES = List.of(
            "风中的羽翼", "星空追梦人", "阳光少年", "星辰大海", "清风徐来",
            "月光诗人", "远行的风", "山间清泉", "云端的梦", "彩虹彼岸");

    @Resource
    private MemberNicknameRecordMapper memberNicknameRecordMapper;
    @Resource
    private DeepSeekProperties deepSeekProperties;

    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public String generateNickname(Long userId) {
        LocalDate today = LocalDate.now();
        MemberNicknameRecordDO record = memberNicknameRecordMapper.selectByUserIdAndGenDate(userId, today);

        int currentCount = (record != null) ? record.getGenCount() : 0;
        if (currentCount >= DAILY_LIMIT) {
            throw exception(NICKNAME_GEN_LIMIT, DAILY_LIMIT);
        }

        String nickname = callDeepSeek();

        if (record != null) {
            record.setGenCount(currentCount + 1);
            record.setNickName(nickname);
            memberNicknameRecordMapper.updateById(record);
        } else {
            record = MemberNicknameRecordDO.builder()
                    .userId(userId)
                    .nickName(nickname)
                    .genCount(1)
                    .genDate(today)
                    .build();
            memberNicknameRecordMapper.insert(record);
        }
        return nickname;
    }

    private String callDeepSeek() {
        if (!deepSeekProperties.isEnable()) {
            return randomFallback();
        }
        try {
            // OpenAI 兼容格式请求
            Map<String, Object> requestBody = Map.of(
                    "model", deepSeekProperties.getModel(),
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", "生成一个中文昵称，2-4个字，风格文艺清新，只返回昵称本身不要多余文字")),
                    "temperature", deepSeekProperties.getTemperature(),
                    "max_tokens", 20
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(deepSeekProperties.getApiKey());

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String url = deepSeekProperties.getBaseUrl() + "/v1/chat/completions";

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            return content != null ? content.trim() : randomFallback();
        } catch (Exception e) {
            log.warn("[callDeepSeek] 调用 DeepSeek 生成昵称失败", e);
            return randomFallback();
        }
    }

    private String randomFallback() {
        return FALLBACK_NAMES.get((int) (System.currentTimeMillis() % FALLBACK_NAMES.size()));
    }

}
