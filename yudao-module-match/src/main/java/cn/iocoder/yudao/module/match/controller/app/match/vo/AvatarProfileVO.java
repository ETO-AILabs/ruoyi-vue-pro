package cn.iocoder.yudao.module.match.controller.app.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * MatchAI APP - AI 分身 Profile VO
 *
 * 字段与前端 onboarding 问卷 MatchQuestion.id 对齐，见 api-contract.md。
 */
@Schema(description = "MatchAI APP - AI 分身 Profile VO")
@Data
public class AvatarProfileVO {

    @Schema(description = "学校编码", example = "zju")
    private String schoolCode;

    @Schema(description = "学校名称", example = "浙江大学")
    private String schoolName;

    @Schema(description = "校区", example = "玉泉")
    private String campus;

    @Schema(description = "年级", example = "本科三年级")
    private String grade;

    @Schema(description = "专业大类", example = "理工科")
    private String majorCategory;

    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;

    @Schema(description = "标签", example = "[\"算法\"]")
    private List<String> tags;

    @Schema(description = "兴趣", example = "[\"桌游 / 剧本杀\", \"科技 / 极客\"]")
    private List<String> interests;

    @Schema(description = "MBTI", example = "INTJ")
    private String mbti;

    @Schema(description = "价值观", example = "[\"真诚\", \"成长\"]")
    private List<String> values;

    @Schema(description = "一句话介绍", example = "一句话介绍")
    private String selfIntro;

    @Schema(description = "最近在忙的事", example = "最近在忙的事")
    private String recentStatus;

}
