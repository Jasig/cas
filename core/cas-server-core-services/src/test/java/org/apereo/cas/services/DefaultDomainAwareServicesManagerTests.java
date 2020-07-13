package org.apereo.cas.services;

import org.apereo.cas.services.domain.DefaultDomainAwareServicesManager;
import org.apereo.cas.services.domain.DefaultRegisteredServiceDomainExtractor;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;

import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Travis Schmidt
 * @since 5.2.0
 */
@NoArgsConstructor
@Tag("RegisteredService")
public class DefaultDomainAwareServicesManagerTests extends AbstractServicesManagerTests<DefaultDomainAwareServicesManager> {
    private DefaultDomainAwareServicesManager defaultDomainAwareServicesManager;

    @Test
    public void verifyDeleteEmptyDomains() {
        var r = new RegexRegisteredService();
        r.setId(10);
        r.setName("domainService1");
        r.setServiceId("https://www.example.com/one");
        this.servicesManager.save(r);

        r = new RegexRegisteredService();
        r.setId(20);
        r.setName("domainService2");
        r.setServiceId("https://www.example.com/two");
        servicesManager.save(r);
        servicesManager.deleteAll();
        assertTrue(this.defaultDomainAwareServicesManager.getDomains().collect(Collectors.toList()).isEmpty());
    }

    @Override
    protected ServicesManager getServicesManagerInstance() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        defaultDomainAwareServicesManager = new DefaultDomainAwareServicesManager(serviceRegistry,
            applicationContext,
            new DefaultRegisteredServiceDomainExtractor(),
            new HashSet<>(),
            Caffeine.newBuilder().build());
        return defaultDomainAwareServicesManager;
    }
}
