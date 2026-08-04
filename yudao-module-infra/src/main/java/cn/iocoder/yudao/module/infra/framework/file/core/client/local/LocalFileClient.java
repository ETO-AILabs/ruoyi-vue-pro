package cn.iocoder.yudao.module.infra.framework.file.core.client.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.iocoder.yudao.module.infra.framework.file.core.client.AbstractFileClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 本地文件客户端
 *
 * @author 芋道源码
 */
@Slf4j
public class LocalFileClient extends AbstractFileClient<LocalFileClientConfig> {

    public LocalFileClient(Long id, LocalFileClientConfig config) {
        super(id, config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    public String upload(byte[] content, String path, String type) {
        // 执行写入
        String filePath = getFilePath(path);
        // 【修复】自动创建父目录，避免 No such file or directory
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created && !parentDir.exists()) {
                // 父目录创建失败（可能是无权限/路径不存在），
                // fallback 到用户家目录下的 yudao-file，避免服务整体不可用
                String userHome = System.getProperty("user.home", "/tmp");
                File fallbackBase = new File(userHome, "yudao-file");
                if (!fallbackBase.exists()) {
                    fallbackBase.mkdirs();
                }
                log.warn("[upload][本地存储路径 {} 不可写，fallback 到 {}]", parentDir.getAbsolutePath(), fallbackBase.getAbsolutePath());
                // 重新计算 filePath（在 fallback 目录下）
                String fallbackPath = fallbackBase.getAbsolutePath() + File.separator + path;
                File fallbackFile = new File(fallbackPath);
                File fallbackParent = fallbackFile.getParentFile();
                if (fallbackParent != null && !fallbackParent.exists()) {
                    fallbackParent.mkdirs();
                }
                FileUtil.writeBytes(content, fallbackPath);
                return super.formatFileUrl(config.getDomain(), path);
            }
        }
        FileUtil.writeBytes(content, filePath);
        // 拼接返回路径
        return super.formatFileUrl(config.getDomain(), path);
    }

    @Override
    public void delete(String path) {
        String filePath = getFilePath(path);
        FileUtil.del(filePath);
    }

    @Override
    public byte[] getContent(String path) {
        String filePath = getFilePath(path);
        try {
            return FileUtil.readBytes(filePath);
        } catch (IORuntimeException ex) {
            if (ex.getMessage().startsWith("File not exist:")) {
                return null;
            }
            throw ex;
        }
    }

    private String getFilePath(String path) {
        return config.getBasePath() + File.separator + path;
    }

}
