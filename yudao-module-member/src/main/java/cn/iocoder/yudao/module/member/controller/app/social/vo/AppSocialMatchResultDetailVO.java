package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 APP - 匹配结果详情 Response VO
 * 涵盖四场景（合拍搭子/合拍伴侣/技能交换/易物交友）的所有展示字段
 *
 * @author 芋道源码
 */
@Schema(description = "用户 APP - 匹配结果详情 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialMatchResultDetailVO {

    // ========== 基础标识 ==========
    @Schema(description = "结果ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long resultId;

    @Schema(description = "任务ID", example = "1")
    private Long taskId;

    @Schema(description = "场景ID", example = "1")
    private Long sceneId;

    @Schema(description = "场景编码: buddy/love/skill/swap", example = "buddy")
    private String sceneCode;

    @Schema(description = "场景名称", example = "合拍搭子")
    private String sceneName;

    // ========== 被匹配者信息 ==========
    @Schema(description = "被匹配者用户ID", example = "10")
    private Long matchedUserId;

    @Schema(description = "被匹配者昵称", example = "柠檬味的猫")
    private String nickname;

    @Schema(description = "头像URL", example = "https://...")
    private String avatar;

    @Schema(description = "性别 1-男 2-女", example = "2")
    private Integer sex;

    @Schema(description = "年龄", example = "23")
    private Integer age;

    @Schema(description = "城市/常驻地", example = "杭州·西湖区")
    private String city;

    @Schema(description = "学校", example = "浙江大学")
    private String school;

    @Schema(description = "职业", example = "产品经理")
    private String profession;

    @Schema(description = "MBTI", example = "INFP")
    private String mbti;

    @Schema(description = "身高(cm)", example = "168")
    private Integer height;

    @Schema(description = "个人描述", example = "周末喜欢逛展探店")
    private String userDesc;

    @Schema(description = "微信号(详情用)", example = "wx_lemon_cat")
    private String wechatId;

    // ========== 匹配信息 ==========
    @Schema(description = "匹配得分 0-100", example = "92.5")
    private BigDecimal matchScore;

    @Schema(description = "AI 推荐理由", example = "你们都热爱摄影与旅行，作息接近")
    private String aiReason;

    @Schema(description = "开始聊的建议", example = "可以问她最近去过哪条线路")
    private String chatTip;

    @Schema(description = "标签列表", example = "[\"摄影\",\"旅行\",\"阅读\"]")
    private List<String> tags;

    @Schema(description = "是否已添加", example = "false")
    private Boolean added;

    @Schema(description = "添加时间")
    private LocalDateTime addedTime;

    // ========== 场景扩展字段（根据 sceneCode 决定语义） ==========
    @Schema(description = "当前状态(buddy/love)", example = "上班族")
    private String currentStatus;

    @Schema(description = "教什么技能(skill)", example = "吉他入门")
    private String teachSkill;

    @Schema(description = "想学什么技能(skill)", example = "尤克里里")
    private String wantSkill;

    @Schema(description = "拿什么换/物品(swap)", example = "Kindle Paperwhite 4")
    private String offer;

    @Schema(description = "想换什么(swap)", example = "蓝牙耳机")
    private String want;

    @Schema(description = "估值/原价(swap)", example = "499")
    private BigDecimal price;

    @Schema(description = "成色(swap)", example = "9 成新")
    private String conditionValue;

    @Schema(description = "物品图片URL列表(swap)", example = "[\"https://...\"]")
    private List<String> photos;

    @Schema(description = "匹配完成时间")
    private LocalDateTime finishTime;
}
