package cn.iocoder.yudao.module.member.controller.app.social.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "用户 APP - 场景表单 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSocialSceneFormRespVO {

    @Schema(description = "区块编码(前端:key)", requiredMode = Schema.RequiredMode.REQUIRED, example = "buddy_activity")
    private String code;

    @Schema(description = "区块标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "想干嘛")
    private String sectionName;

    @Schema(description = "子分组列表")
    private List<SubGroup> subGroups;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "子分组")
    public static class SubGroup {

        @Schema(description = "分组名称", example = "生活类")
        private String groupName;

        @Schema(description = "分组编码(前端:key)", example = "living")
        private String groupCode;

        @Schema(description = "标签列表（二级场景直接使用）")
        private List<TagItem> tags;

        @Schema(description = "三级子分组（仅 personality 场景，含性格/恋爱观下的子标签组）")
        private List<SubSubGroup> subSubGroups;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "三级子分组")
    public static class SubSubGroup {

        @Schema(description = "子分组名称", example = "情绪特质")
        private String subGroupName;

        @Schema(description = "子分组编码", example = "emotional_trait")
        private String subGroupCode;

        @Schema(description = "标签列表")
        private List<TagItem> tags;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "标签项")
    public static class TagItem {

        @Schema(description = "标签ID(更新时用)", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
        private Long tagId;

        @Schema(description = "标签编码(前端:key)", requiredMode = Schema.RequiredMode.REQUIRED, example = "ktv")
        private String tagCode;

        @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "KTV")
        private String tagName;

        @Schema(description = "是否选中", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
        private Boolean isSelected;

        @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sort;
    }

}
