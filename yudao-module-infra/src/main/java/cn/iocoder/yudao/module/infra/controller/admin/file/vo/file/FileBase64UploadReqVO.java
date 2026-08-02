package cn.iocoder.yudao.module.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 管理后台 - Base64 上传文件 Request VO
 *
 * <p>用于前端在 multipart/form-data 上传失败（最常见于 H5 端 blob URL 临时路径）
 * 时的兜底方案：前端用 FileReader.readAsDataURL() 读取本地文件为 base64，
 * 通过此接口上传。优势：
 * <ul>
 *   <li>对 H5 blob:、wxfile://、file:// 等临时路径都兼容</li>
 *   <li>不依赖 uni.uploadFile，可走普通 JSON POST 避免跨域 preflight</li>
 *   <li>服务端与 /infra/file/upload 走完全相同的存储逻辑</li>
 * </ul>
 *
 * Author: Match-AI
 */
@Schema(description = "管理后台 - Base64 上传文件 Request VO")
@Data
public class FileBase64UploadReqVO {

    @Schema(description = "文件 base64 字符串（不含 data:image/xxx;base64, 前缀）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件内容不能为空")
    @Size(max = 10 * 1024 * 1024, message = "单文件 base64 不能超过 10MB")
    private String base64;

    @Schema(description = "文件名（含后缀，例如 photo.jpg）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名不能为空")
    private String name;

    @Schema(description = "MIME 类型，例如 image/jpeg；为空时自动推断", example = "image/jpeg")
    private String contentType;

    @Schema(description = "文件目录", example = "swap")
    private String directory;

    /**
     * 批量上传场景
     */
    @Schema(description = "批量上传的文件列表（与 base64/name 互斥）")
    private List<FileItem> files;

    @Data
    public static class FileItem {
        @NotBlank(message = "文件内容不能为空")
        @Size(max = 10 * 1024 * 1024, message = "单文件 base64 不能超过 10MB")
        private String base64;

        @NotBlank(message = "文件名不能为空")
        private String name;

        private String contentType;
    }
}
