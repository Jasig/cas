package org.apereo.cas.audit;

import org.apereo.cas.audit.spi.BaseAuditConfigurationTests;
import org.apereo.cas.config.CasCoreAuditAutoConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreNotificationsAutoConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreTicketsSerializationConfiguration;
import org.apereo.cas.config.CasCoreUtilAutoConfiguration;
import org.apereo.cas.config.CasCoreWebAutoConfiguration;
import org.apereo.cas.config.CasSupportRedisAuditConfiguration;
import org.apereo.cas.config.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.util.junit.EnabledIfListeningOnPort;

import lombok.Getter;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

/**
 * This is {@link RedisAuditTrailManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@SpringBootTest(classes = {
    CasCoreAuditAutoConfiguration.class,
    CasCoreConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasCoreTicketsSerializationConfiguration.class,
    CasCoreAuthenticationPrincipalConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreUtilAutoConfiguration.class,
    CasCoreNotificationsAutoConfiguration.class,
    CasSupportRedisAuditConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    RefreshAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    CasCoreWebAutoConfiguration.class
},
    properties = {
        "cas.audit.redis.host=localhost",
        "cas.audit.redis.port=6379",
        "cas.audit.redis.asynchronous=false"
    })
@Tag("Redis")
@Getter
@EnabledIfListeningOnPort(port = 6379)
class RedisAuditTrailManagerTests extends BaseAuditConfigurationTests {
    @Autowired
    @Qualifier("redisAuditTrailManager")
    private AuditTrailManager auditTrailManager;
}
