package cn.iocoder.yudao.module.system.util.tenant;

import cn.hutool.core.util.StrUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 租户网站工具类。
 *
 * 兼容纯域名、host:port 以及完整 URL 的租户网站匹配。
 */
public class TenantWebsiteUtils {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    private static final Pattern SCHEME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$");

    private TenantWebsiteUtils() {}

    public static List<String> buildQueryCandidates(String website) {
        if (StrUtil.isBlank(website)) {
            return Collections.emptyList();
        }

        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        addCandidate(candidates, website);

        URI uri = parseWebsite(website);
        if (uri == null || StrUtil.isBlank(uri.getHost())) {
            return new ArrayList<>(candidates);
        }

        String host = uri.getHost();
        String hostAndPort = buildHostAndPort(host, uri.getPort());
        addCandidate(candidates, host);
        addCandidate(candidates, hostAndPort);
        addCandidate(candidates, HTTP_PREFIX + hostAndPort);
        addCandidate(candidates, HTTPS_PREFIX + hostAndPort);
        return new ArrayList<>(candidates);
    }

    private static URI parseWebsite(String website) {
        String value = normalizeCandidate(website);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        try {
            if (!SCHEME_PATTERN.matcher(value).matches()) {
                value = HTTP_PREFIX + value;
            }
            return URI.create(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static void addCandidate(LinkedHashSet<String> candidates, String website) {
        String value = normalizeCandidate(website);
        if (StrUtil.isNotBlank(value)) {
            candidates.add(value);
        }
    }

    private static String normalizeCandidate(String website) {
        String value = StrUtil.trim(website);
        if (StrUtil.isBlank(value)) {
            return null;
        }
        return StrUtil.removeSuffix(value, "/");
    }

    private static String buildHostAndPort(String host, int port) {
        return port < 0 ? host : host + ":" + port;
    }

}
