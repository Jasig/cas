package org.apereo.cas.services;

import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreNotificationsConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.DynamoDbServiceRegistryConfiguration;
import org.apereo.cas.util.junit.EnabledIfPortOpen;

import lombok.Getter;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

/**
 * This is {@link DynamoDbServiceRegistryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@SpringBootTest(classes = {
    DynamoDbServiceRegistryConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasCoreUtilConfiguration.class,
    CasCoreNotificationsConfiguration.class,
    CasCoreAuthenticationMetadataConfiguration.class,
    RefreshAutoConfiguration.class
},
    properties = {
        "cas.service-registry.dynamo-db.endpoint=http://localhost:8000",
        "cas.service-registry.dynamo-db.drop-tables-on-startup=true",
        "cas.service-registry.dynamo-db.local-instance=true",
        "cas.service-registry.dynamo-db.region=us-east-1"
    })
@Tag("DynamoDb")
@EnabledIfPortOpen(port = 8000)
@Getter
public class DynamoDbServiceRegistryTests extends AbstractServiceRegistryTests {

    static {
        System.setProperty("aws.accessKeyId", "AKIAIPPIGGUNIO74C63Z");
        System.setProperty("aws.secretKey", "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");
    }

    @Autowired
    @Qualifier("serviceRegistry")
    private ServiceRegistry newServiceRegistry;
}
