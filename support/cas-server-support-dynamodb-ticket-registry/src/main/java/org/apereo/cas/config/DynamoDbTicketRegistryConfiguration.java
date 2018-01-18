package org.apereo.cas.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.aws.ChainingAWSCredentialsProvider;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.core.util.EncryptionRandomizedSigningJwtCryptographyProperties;
import org.apereo.cas.configuration.model.support.dynamodb.DynamoDbTicketRegistryProperties;
import org.apereo.cas.ticket.TicketCatalog;
import org.apereo.cas.ticket.registry.DynamoDbTicketRegistry;
import org.apereo.cas.ticket.registry.DynamoDbTicketRegistryFacilitator;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.CoreTicketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

/**
 * This is {@link DynamoDbTicketRegistryConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration("dynamoDbTicketRegistryConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class DynamoDbTicketRegistryConfiguration {
    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @RefreshScope
    @Bean
    public TicketRegistry ticketRegistry(@Qualifier("ticketCatalog") final TicketCatalog ticketCatalog) {
        final DynamoDbTicketRegistryProperties db = casProperties.getTicket().getRegistry().getDynamoDb();
        final EncryptionRandomizedSigningJwtCryptographyProperties crypto = db.getCrypto();
        return new DynamoDbTicketRegistry(CoreTicketUtils.newTicketRegistryCipherExecutor(crypto, "dynamoDb"),
            dynamoDbTicketRegistryFacilitator(ticketCatalog));
    }

    @Autowired
    @RefreshScope
    @Bean
    public DynamoDbTicketRegistryFacilitator dynamoDbTicketRegistryFacilitator(@Qualifier("ticketCatalog") final TicketCatalog ticketCatalog) {
        final DynamoDbTicketRegistryProperties db = casProperties.getTicket().getRegistry().getDynamoDb();
        final DynamoDbTicketRegistryFacilitator f = new DynamoDbTicketRegistryFacilitator(ticketCatalog, db, amazonDynamoDbClient());
        f.createTicketTables(db.isDropTablesOnStartup());
        return f;
    }

    @RefreshScope
    @Bean
    @SneakyThrows
    public AmazonDynamoDBClient amazonDynamoDbClient() {

        final DynamoDbTicketRegistryProperties dynamoDbProperties = casProperties.getTicket().getRegistry().getDynamoDb();
        final ClientConfiguration cfg = new ClientConfiguration();
        cfg.setConnectionTimeout(dynamoDbProperties.getConnectionTimeout());
        cfg.setMaxConnections(dynamoDbProperties.getMaxConnections());
        cfg.setRequestTimeout(dynamoDbProperties.getRequestTimeout());
        cfg.setSocketTimeout(dynamoDbProperties.getSocketTimeout());
        cfg.setUseGzip(dynamoDbProperties.isUseGzip());
        cfg.setUseReaper(dynamoDbProperties.isUseReaper());
        cfg.setUseThrottleRetries(dynamoDbProperties.isUseThrottleRetries());
        cfg.setUseTcpKeepAlive(dynamoDbProperties.isUseTcpKeepAlive());
        cfg.setProtocol(Protocol.valueOf(dynamoDbProperties.getProtocol().toUpperCase()));
        cfg.setClientExecutionTimeout(dynamoDbProperties.getClientExecutionTimeout());
        cfg.setCacheResponseMetadata(dynamoDbProperties.isCacheResponseMetadata());

        if (StringUtils.isNotBlank(dynamoDbProperties.getLocalAddress())) {
            cfg.setLocalAddress(InetAddress.getByName(dynamoDbProperties.getLocalAddress()));
        }

        final AWSCredentialsProvider provider =
            ChainingAWSCredentialsProvider.getInstance(dynamoDbProperties.getCredentialAccessKey(),
                dynamoDbProperties.getCredentialSecretKey(), dynamoDbProperties.getCredentialsPropertiesFile());
        final AmazonDynamoDBClient client = new AmazonDynamoDBClient(provider, cfg);

        if (StringUtils.isNotBlank(dynamoDbProperties.getEndpoint())) {
            client.setEndpoint(dynamoDbProperties.getEndpoint());
        }

        if (StringUtils.isNotBlank(dynamoDbProperties.getRegion())) {
            client.setRegion(Region.getRegion(Regions.valueOf(dynamoDbProperties.getRegion())));
        }

        if (StringUtils.isNotBlank(dynamoDbProperties.getRegionOverride())) {
            client.setSignerRegionOverride(dynamoDbProperties.getRegionOverride());
        }

        if (StringUtils.isNotBlank(dynamoDbProperties.getServiceNameIntern())) {
            client.setServiceNameIntern(dynamoDbProperties.getServiceNameIntern());
        }

        if (dynamoDbProperties.getTimeOffset() != 0) {
            client.setTimeOffset(dynamoDbProperties.getTimeOffset());
        }

        return client;

    }
}
