package cn.iocoder.yudao.module.system.util.tenant;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenantWebsiteUtilsTest {

    @Test
    public void testBuildQueryCandidates_localhostWithPort() {
        List<String> candidates = TenantWebsiteUtils.buildQueryCandidates("localhost:3000");
        assertEquals(Arrays.asList("localhost:3000", "localhost", "http://localhost:3000", "https://localhost:3000"),
                candidates);
    }

    @Test
    public void testBuildQueryCandidates_fullUrl() {
        List<String> candidates = TenantWebsiteUtils.buildQueryCandidates("https://www.iocoder.cn/");
        assertEquals(Arrays.asList("https://www.iocoder.cn", "www.iocoder.cn", "http://www.iocoder.cn"), candidates);
    }

}
