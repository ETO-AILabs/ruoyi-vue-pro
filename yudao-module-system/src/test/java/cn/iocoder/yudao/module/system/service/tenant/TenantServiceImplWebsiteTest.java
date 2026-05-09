package cn.iocoder.yudao.module.system.service.tenant;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.system.dal.dataobject.tenant.TenantDO;
import cn.iocoder.yudao.module.system.dal.mysql.tenant.TenantMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TenantServiceImplWebsiteTest extends BaseMockitoUnitTest {

    @InjectMocks
    private TenantServiceImpl tenantService;

    @Mock
    private TenantMapper tenantMapper;

    @Test
    public void testGetTenantByWebsite_localhostWithPort() {
        TenantDO tenant = TenantDO.builder().id(1L).name("test").build();
        when(tenantMapper.selectListByWebsite(eq("localhost:3000"))).thenReturn(Collections.emptyList());
        when(tenantMapper.selectListByWebsite(eq("localhost"))).thenReturn(Collections.emptyList());
        when(tenantMapper.selectListByWebsite(eq("http://localhost:3000")))
                .thenReturn(Collections.singletonList(tenant));
        when(tenantMapper.selectListByWebsite(eq("https://localhost:3000"))).thenReturn(Collections.emptyList());

        TenantDO result = tenantService.getTenantByWebsite("localhost:3000");

        assertEquals(tenant, result);
        verify(tenantMapper).selectListByWebsite("localhost:3000");
        verify(tenantMapper).selectListByWebsite("localhost");
        verify(tenantMapper).selectListByWebsite("http://localhost:3000");
    }

}
