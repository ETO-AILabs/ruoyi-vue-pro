package cn.iocoder.yudao.module.member.framework.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "yudao.ai.deepseek")
@Data
public class DeepSeekProperties {

    private boolean enable = false;
    private String apiKey;
    private String baseUrl = "https://api.deepseek.com";
    private String model = "deepseek-chat";
    private double temperature = 0.8;

}
