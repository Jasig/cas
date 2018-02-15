package org.apereo.cas.web.flow.config;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.saml.OpenSamlConfigBean;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.DelegatedClientAuthenticationAction;
import org.apereo.cas.web.flow.Pac4jErrorViewResolver;
import org.apereo.cas.web.flow.Pac4jWebflowConfigurer;
import org.apereo.cas.web.flow.SAML2ClientLogoutAction;
import org.apereo.cas.web.saml2.Saml2ClientMetadataController;
import org.pac4j.core.client.Clients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

/**
 * This is {@link Pac4jWebflowConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("pac4jWebflowConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class Pac4jWebflowConfiguration {

    @Autowired
    @Qualifier("builtClients")
    private Clients builtClients;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;

    @Autowired
    @Qualifier("shibboleth.OpenSAMLConfig")
    private OpenSamlConfigBean configBean;

    @Autowired
    @Qualifier("loginFlowRegistry")
    private FlowDefinitionRegistry loginFlowDefinitionRegistry;

    @Autowired
    private FlowBuilderServices flowBuilderServices;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("saml2ClientLogoutAction")
    private Action saml2ClientLogoutAction;

    @Autowired
    @Qualifier("logoutFlowRegistry")
    private FlowDefinitionRegistry logoutFlowDefinitionRegistry;

    @ConditionalOnMissingBean(name = "saml2ClientLogoutAction")
    @Bean
    @Lazy
    public Action saml2ClientLogoutAction() {
        return new SAML2ClientLogoutAction(builtClients);
    }

    @RefreshScope
    @Bean
    @Lazy
    public Action clientAction() {
        return new DelegatedClientAuthenticationAction(builtClients,
            authenticationSystemSupport,
            centralAuthenticationService,
            casProperties.getTheme().getParamName(),
            casProperties.getLocale().getParamName(),
            casProperties.getAuthn().getPac4j().isAutoRedirect(),
            servicesManager);
    }

    @ConditionalOnMissingBean(name = "pac4jWebflowConfigurer")
    @Bean
    @DependsOn("defaultWebflowConfigurer")
    public CasWebflowConfigurer pac4jWebflowConfigurer() {
        final CasWebflowConfigurer w = new Pac4jWebflowConfigurer(flowBuilderServices, loginFlowDefinitionRegistry,
            logoutFlowDefinitionRegistry, saml2ClientLogoutAction, applicationContext, casProperties);
        w.initialize();
        return w;
    }

    @Bean
    public ErrorViewResolver pac4jErrorViewResolver() {
        return new Pac4jErrorViewResolver();
    }

    @Bean
    public Saml2ClientMetadataController saml2ClientMetadataController() {
        return new Saml2ClientMetadataController(builtClients, configBean);
    }
}
