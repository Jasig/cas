package org.apereo.cas.services;

import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.util.RandomUtils;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.Ordered;

import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link ChainingServicesManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Tag("RegisteredService")
public class ChainingServicesManagerTests extends AbstractServicesManagerTests<ChainingServicesManager> {
    @Test
    public void verifyOperation() {
        val input = mock(ServicesManager.class);
        when(input.findServiceBy(anyLong(), any())).thenCallRealMethod();
        when(input.findServiceByName(anyString(), any())).thenCallRealMethod();
        when(input.count()).thenCallRealMethod();
        when(input.getName()).thenCallRealMethod();
        when(input.getOrder()).thenCallRealMethod();
        assertEquals(Ordered.LOWEST_PRECEDENCE, input.getOrder());
        assertEquals(0, input.count());
        assertNotNull(input.getName());

        assertNull(input.findServiceBy(0, RegexRegisteredService.class));
        assertNull(input.findServiceBy(new WebApplicationServiceFactory().createService("name"), RegexRegisteredService.class));
    }

    @Test
    public void verifySupports() {
        val r = new RegexRegisteredService();
        r.setId(10);
        r.setName("domainService1");
        r.setServiceId("https://www.example.com/one");
        servicesManager.save(r);
        assertTrue(servicesManager.supports(RegexRegisteredService.class));
        assertTrue(servicesManager.supports(r));
        assertTrue(servicesManager.supports(RegisteredServiceTestUtils.getService()));
    }

    @Test
    public void verifySaveWithDomains() {
        val svc = new RegexRegisteredService();
        svc.setId(RandomUtils.nextLong());
        svc.setName("domainService2");
        svc.setServiceId("https://www.example.com/" + svc.getId());
        assertNotNull(servicesManager.save(svc, false));
        assertEquals(servicesManager.getDomains().count(), 1);
        assertFalse(servicesManager.getServicesForDomain("example.org").isEmpty());
    }

    @Test
    public void verifySaveInBulk() {
        servicesManager.deleteAll();
        servicesManager.save(() -> {
            val svc = new RegexRegisteredService();
            svc.setId(RandomUtils.nextLong());
            svc.setName("domainService2");
            svc.setServiceId("https://www.example.com/" + svc.getId());
            return svc;
        }, Assertions::assertNotNull, 10);
        val results = servicesManager.load();
        assertEquals(10, results.size());
    }

    @Test
    public void verifySaveInStreams() {
        servicesManager.deleteAll();
        val s1 = RegisteredServiceTestUtils.getRegisteredService(UUID.randomUUID().toString(), true);
        val s2 = RegisteredServiceTestUtils.getRegisteredService(UUID.randomUUID().toString(), true);
        servicesManager.save(Stream.of(s1, s2));
        val results = servicesManager.load();
        assertEquals(2, results.size());
    }

    @Override
    protected ServicesManager getServicesManagerInstance() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        val chain = new ChainingServicesManager();
        val context = ServicesManagerConfigurationContext.builder()
            .serviceRegistry(serviceRegistry)
            .applicationContext(applicationContext)
            .environments(new HashSet<>())
            .servicesCache(Caffeine.newBuilder().initialCapacity(100).maximumSize(100).build())
            .build();
        val manager = new DefaultServicesManager(context);
        chain.registerServiceManager(manager);
        return chain;
    }
}
