package org.apereo.cas.services;

import org.apereo.cas.audit.AuditableContext;
import org.apereo.cas.audit.AuditableExecution;
import org.apereo.cas.config.CasCoreNotificationsAutoConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilAutoConfiguration;
import org.apereo.cas.config.CasCoreWebAutoConfiguration;
import org.apereo.cas.config.CasWebApplicationServiceFactoryConfiguration;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link GroovyRegisteredServiceAccessStrategyEnforcerTests}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
@Tag("Groovy")
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreNotificationsAutoConfiguration.class,
    CasCoreUtilAutoConfiguration.class,
    CasCoreWebAutoConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class
}, properties = "cas.access-strategy.groovy.location=classpath:ServiceAccessStrategy.groovy")
class GroovyRegisteredServiceAccessStrategyEnforcerTests {

    @Autowired
    @Qualifier("registeredServiceAccessStrategyEnforcer")
    private AuditableExecution registeredServiceAccessStrategyEnforcer;

    @Autowired
    @Qualifier("groovyRegisteredServiceAccessStrategyEnforcer")
    private RegisteredServiceAccessStrategyEnforcer groovyRegisteredServiceAccessStrategyEnforcer;
    
    @Test
    void verifyOperation() throws Throwable {
        assertNotNull(groovyRegisteredServiceAccessStrategyEnforcer);
        
        val context = AuditableContext.builder()
            .registeredService(RegisteredServiceTestUtils.getRegisteredService())
            .service(RegisteredServiceTestUtils.getService())
            .authentication(RegisteredServiceTestUtils.getAuthentication())
            .build();
        val results = registeredServiceAccessStrategyEnforcer.execute(context);
        assertTrue(results.isExecutionFailure());
    }
}
