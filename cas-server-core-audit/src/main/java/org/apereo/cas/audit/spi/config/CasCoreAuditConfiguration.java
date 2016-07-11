package org.apereo.cas.audit.spi.config;

import com.google.common.collect.ImmutableList;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.audit.spi.AssertionAsReturnValuePrincipalResolver;
import org.apereo.cas.audit.spi.CredentialsAsFirstParameterResourceResolver;
import org.apereo.cas.audit.spi.MessageBundleAwareResourceResolver;
import org.apereo.cas.audit.spi.PrincipalIdProvider;
import org.apereo.cas.audit.spi.ServiceResourceResolver;
import org.apereo.cas.audit.spi.TicketAsFirstParameterResourceResolver;
import org.apereo.cas.audit.spi.TicketOrCredentialPrincipalResolver;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.inspektr.audit.AuditTrailManagementAspect;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.apereo.inspektr.audit.spi.AuditActionResolver;
import org.apereo.inspektr.audit.spi.AuditResourceResolver;
import org.apereo.inspektr.audit.spi.support.DefaultAuditActionResolver;
import org.apereo.inspektr.audit.spi.support.ReturnValueAsStringResourceResolver;
import org.apereo.inspektr.audit.support.Slf4jLoggingAuditTrailManager;
import org.apereo.inspektr.common.spi.PrincipalResolver;
import org.apereo.inspektr.common.web.ClientInfoThreadLocalFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This is {@link CasCoreAuditConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("casCoreAuditConfiguration")
@EnableAspectJAutoProxy
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasCoreAuditConfiguration {

    private static final String AUDIT_ACTION_SUFFIX_FAILED = "_FAILED";

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    public AuditTrailManagementAspect auditTrailManagementAspect(
            @Qualifier("centralAuthenticationService")
            final CentralAuthenticationService centralAuthenticationService,
            @Qualifier("auditTrailManager")
            final AuditTrailManager auditTrailManager) {
        final AuditTrailManagementAspect aspect = new AuditTrailManagementAspect(
                casProperties.getAudit().getAppCode(),
                auditablePrincipalResolver(centralAuthenticationService),
                ImmutableList.of(auditTrailManager), auditActionResolverMap(),
                auditResourceResolverMap());
        aspect.setFailOnAuditFailures(!casProperties.getAudit().isIgnoreAuditFailures());
        return aspect;
    }

    @ConditionalOnMissingBean(name = "auditTrailManager")
    @Bean(name = {"slf4jAuditTrailManager", "auditTrailManager"})
    public AuditTrailManager slf4jAuditTrailManager() {
        final Slf4jLoggingAuditTrailManager mgmr = new Slf4jLoggingAuditTrailManager();
        mgmr.setUseSingleLine(casProperties.getAudit().isUseSingleLine());
        mgmr.setEntrySeparator(casProperties.getAudit().getSinglelineSeparator());
        mgmr.setAuditFormat(casProperties.getAudit().getAuditFormat());
        return mgmr;
    }

    @Bean
    public FilterRegistrationBean casClientInfoLoggingFilter() {
        final FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new ClientInfoThreadLocalFilter());
        bean.setUrlPatterns(Collections.singleton("/*"));
        bean.setName("CAS Client Info Logging Filter");
        return bean;
    }

    @Bean
    public DefaultAuditActionResolver authenticationActionResolver() {
        return new DefaultAuditActionResolver("_SUCCESS", AUDIT_ACTION_SUFFIX_FAILED);
    }

    @Bean
    public DefaultAuditActionResolver ticketCreationActionResolver() {
        return new DefaultAuditActionResolver("_CREATED", "_NOT_CREATED");
    }

    @Bean
    public DefaultAuditActionResolver ticketValidationActionResolver() {
        return new DefaultAuditActionResolver("D", AUDIT_ACTION_SUFFIX_FAILED);
    }

    @Bean
    public ReturnValueAsStringResourceResolver returnValueResourceResolver() {
        return new ReturnValueAsStringResourceResolver();
    }

    @Bean
    public Map auditActionResolverMap() {
        final Map<String, AuditActionResolver> map = new HashMap<>();
        map.put("AUTHENTICATION_RESOLVER", authenticationActionResolver());
        map.put("SAVE_SERVICE_ACTION_RESOLVER", authenticationActionResolver());
        map.put("CREATE_TICKET_GRANTING_TICKET_RESOLVER", ticketCreationActionResolver());
        map.put("DESTROY_TICKET_GRANTING_TICKET_RESOLVER", new DefaultAuditActionResolver());
        map.put("CREATE_PROXY_GRANTING_TICKET_RESOLVER", ticketCreationActionResolver());
        map.put("DESTROY_PROXY_GRANTING_TICKET_RESOLVER", new DefaultAuditActionResolver());
        map.put("GRANT_SERVICE_TICKET_RESOLVER", ticketCreationActionResolver());
        map.put("GRANT_PROXY_TICKET_RESOLVER", ticketCreationActionResolver());
        map.put("VALIDATE_SERVICE_TICKET_RESOLVER", ticketValidationActionResolver());
        return map;
    }

    @Bean
    public Map auditResourceResolverMap() {
        final Map<String, AuditResourceResolver> map = new HashMap<>();
        map.put("AUTHENTICATION_RESOURCE_RESOLVER", new CredentialsAsFirstParameterResourceResolver());
        map.put("CREATE_TICKET_GRANTING_TICKET_RESOURCE_RESOLVER", this.messageBundleAwareResourceResolver());
        map.put("CREATE_PROXY_GRANTING_TICKET_RESOURCE_RESOLVER", this.messageBundleAwareResourceResolver());
        map.put("DESTROY_TICKET_GRANTING_TICKET_RESOURCE_RESOLVER", this.ticketResourceResolver());
        map.put("DESTROY_PROXY_GRANTING_TICKET_RESOURCE_RESOLVER", this.ticketResourceResolver());
        map.put("GRANT_SERVICE_TICKET_RESOURCE_RESOLVER", new ServiceResourceResolver());
        map.put("GRANT_PROXY_TICKET_RESOURCE_RESOLVER", new ServiceResourceResolver());
        map.put("VALIDATE_SERVICE_TICKET_RESOURCE_RESOLVER", this.ticketResourceResolver());
        map.put("SAVE_SERVICE_RESOURCE_RESOLVER", returnValueResourceResolver());
        return map;
    }

    @Bean
    public PrincipalResolver auditablePrincipalResolver(
            @Qualifier("centralAuthenticationService")
            final CentralAuthenticationService centralAuthenticationService
    ) {
        final TicketOrCredentialPrincipalResolver r =
                new TicketOrCredentialPrincipalResolver(centralAuthenticationService);
        r.setPrincipalIdProvider(principalIdProvider());
        return new AssertionAsReturnValuePrincipalResolver(r);
    }

    @Bean
    public TicketAsFirstParameterResourceResolver ticketResourceResolver() {
        return new TicketAsFirstParameterResourceResolver();
    }

    @Bean
    public MessageBundleAwareResourceResolver messageBundleAwareResourceResolver() {
        return new MessageBundleAwareResourceResolver();
    }

    @Bean
    public PrincipalIdProvider principalIdProvider() {
        return authentication -> authentication.getPrincipal().getId();
    }
}
