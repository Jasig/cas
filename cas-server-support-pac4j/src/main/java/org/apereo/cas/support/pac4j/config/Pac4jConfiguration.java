package org.apereo.cas.support.pac4j.config;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationMetaDataPopulator;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.pac4j.Pac4jProperties;
import org.apereo.cas.support.pac4j.Pac4jApplicationContextWrapper;
import org.apereo.cas.support.pac4j.authentication.ClientAuthenticationMetaDataPopulator;
import org.apereo.cas.support.pac4j.authentication.handler.support.ClientAuthenticationHandler;
import org.apereo.cas.support.pac4j.web.flow.ClientAction;
import org.apereo.cas.web.BaseApplicationContextWrapper;
import org.pac4j.config.client.PropertiesConfigFactory;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.webflow.execution.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is {@link Pac4jConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("pac4jConfiguration")
public class Pac4jConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired(required = false)
    private IndirectClient[] clients;
    
    @Bean
    public Pac4jProperties pac4jProperties() {
        return new Pac4jProperties();
    }

    @Bean
    public BaseApplicationContextWrapper pac4jApplicationContextWrapper() {
        return new Pac4jApplicationContextWrapper();
    }

    @Bean
    public AuthenticationMetaDataPopulator clientAuthenticationMetaDataPopulator() {
        return new ClientAuthenticationMetaDataPopulator();
    }

    @Bean
    public AuthenticationHandler clientAuthenticationHandler() {
        return new ClientAuthenticationHandler();
    }

    @Bean
    public Action clientAction() {
        return new ClientAction();
    }

    /**
     * Returning the built clients.
     *
     * @return the built clients.
     */
    @RefreshScope
    @Bean
    public Clients builtClients() {
        final List<Client> allClients = new ArrayList<>();

        // turn the properties file into a map of properties
        final Map<String, String> properties = new HashMap<>();

        properties.put(PropertiesConfigFactory.FACEBOOK_ID, casProperties.getPac4j().getFacebook().getId());
        properties.put(PropertiesConfigFactory.FACEBOOK_SECRET, casProperties.getPac4j().getFacebook().getSecret());
        properties.put(PropertiesConfigFactory.FACEBOOK_SCOPE, casProperties.getPac4j().getFacebook().getScope());
        properties.put(PropertiesConfigFactory.FACEBOOK_FIELDS, casProperties.getPac4j().getFacebook().getFields());

        properties.put(PropertiesConfigFactory.TWITTER_ID, casProperties.getPac4j().getTwitter().getId());
        properties.put(PropertiesConfigFactory.TWITTER_SECRET, casProperties.getPac4j().getTwitter().getSecret());

        properties.put(PropertiesConfigFactory.CAS_LOGIN_URL, casProperties.getPac4j().getCas().getLoginUrl());
        properties.put(PropertiesConfigFactory.CAS_PROTOCOL, casProperties.getPac4j().getCas().getProtocol());

        properties.put(PropertiesConfigFactory.SAML_IDENTITY_PROVIDER_METADATA_PATH,
                casProperties.getPac4j().getSaml().getIdentityProviderMetadataPath());
        properties.put(PropertiesConfigFactory.SAML_KEYSTORE_PASSWORD,
                casProperties.getPac4j().getSaml().getKeystorePassword());
        properties.put(PropertiesConfigFactory.SAML_KEYSTORE_PATH,
                casProperties.getPac4j().getSaml().getKeystorePath());
        properties.put(PropertiesConfigFactory.SAML_MAXIMUM_AUTHENTICATION_LIFETIME,
                casProperties.getPac4j().getSaml().getMaximumAuthenticationLifetime());
        properties.put(PropertiesConfigFactory.SAML_PRIVATE_KEY_PASSWORD,
                casProperties.getPac4j().getSaml().getPrivateKeyPassword());
        properties.put(PropertiesConfigFactory.SAML_SERVICE_PROVIDER_ENTITY_ID,
                casProperties.getPac4j().getSaml().getServiceProviderEntityId());
        properties.put(PropertiesConfigFactory.SAML_SERVICE_PROVIDER_METADATA_PATH,
                casProperties.getPac4j().getSaml().getServiceProviderEntityId());

        properties.put(PropertiesConfigFactory.OIDC_CUSTOM_PARAM_KEY1, casProperties.getPac4j().getOidc().getCustomParamKey1());
        properties.put(PropertiesConfigFactory.OIDC_CUSTOM_PARAM_KEY2, casProperties.getPac4j().getOidc().getCustomParamKey2());
        properties.put(PropertiesConfigFactory.OIDC_CUSTOM_PARAM_VALUE1, casProperties.getPac4j().getOidc().getCustomParamValue1());
        properties.put(PropertiesConfigFactory.OIDC_CUSTOM_PARAM_VALUE2, casProperties.getPac4j().getOidc().getCustomParamValue2());
        properties.put(PropertiesConfigFactory.OIDC_DISCOVERY_URI, casProperties.getPac4j().getOidc().getDiscoveryUri());
        properties.put(PropertiesConfigFactory.OIDC_ID, casProperties.getPac4j().getOidc().getId());
        properties.put(PropertiesConfigFactory.OIDC_MAX_CLOCK_SKEW, casProperties.getPac4j().getOidc().getMaxClockSkew());
        properties.put(PropertiesConfigFactory.OIDC_PREFERRED_JWS_ALGORITHM, casProperties.getPac4j().getOidc().getPreferredJwsAlgorithm());
        properties.put(PropertiesConfigFactory.OIDC_SECRET, casProperties.getPac4j().getOidc().getSecret());
        properties.put(PropertiesConfigFactory.OIDC_USE_NONCE, casProperties.getPac4j().getOidc().getUseNonce());

        // add the new clients found via properties first
        final ConfigFactory configFactory = new PropertiesConfigFactory(properties);
        final Config propertiesConfig = configFactory.build();
        allClients.addAll(propertiesConfig.getClients().getClients());

        // add all indirect clients from the Spring context
        if (this.clients != null && this.clients.length > 0) {
            allClients.addAll(Arrays.<Client>asList(this.clients));
        }

        // build a Clients configuration
        if (allClients.isEmpty()) {
            throw new IllegalArgumentException("At least one pac4j client must be defined");
        }
        return new Clients(casProperties.getServer().getLoginUrl(), allClients);
    }
}
