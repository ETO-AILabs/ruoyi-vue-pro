package cn.iocoder.yudao.module.match.controller.app.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * MatchAI APP - 推荐候选人卡片 VO
 *
 * 接口契约见 api-contract.md。
 */
@Schema(description = "MatchAI APP - 推荐候选人卡片 VO")
@Data
public class CandidateCardVO {

    @Schema(description = "候选人编号", example = "1")
    private Long id;

    @Schema(description = "头像", example = "")
    private String avatar;

    @Schema(description = "昵称", example = "林一")
    private String nickname;

    @Schema(description = "年龄", example = "21")
    private Integer age;

    @Schema(description = "学校名称", example = "浙江大学")
    private String schoolName;

    @Schema(description = "年级", example = "本科三年级")
    private String grade;

    @Schema(description = "专业大类", example = "理工科")
    private String majorCategory;

    @Schema(description = "标签", example = "[\"算法\", \"健身\"]")
    private List<String> tags;

    @Schema(description = "MBTI", example = "INTJ")
    private String mbti;

    @Schema(description = "一句话介绍", example = "一句话介绍")
    private String selfIntro;

    @Schema(description = "匹配度", example = "85")
    private Integer matchScore;

    @Schema(description = "匹配理由", example = "[\"兴趣契合\", \"性格互补\"]")
    private List<String> matchReasons;

}
