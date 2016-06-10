package org.apereo.cas.mgmt.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.SearchExecutor;
import org.ldaptive.SearchFilter;
import org.pac4j.core.authorization.AuthorizationGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * This is {@link CasManagementLdapAuthorizationConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("casManagementLdapAuthorizationConfiguration")
@ComponentScan(basePackages = {"org.apereo.cas.authorization"})
public class CasManagementLdapAuthorizationConfiguration {

    @Resource(name = "ldapAuthorizationGenerator")
    private AuthorizationGenerator authorizationGenerator;

    @Autowired
    private CasConfigurationProperties casProperties;

    /**
     * Authorization generator for ldap access.
     *
     * @return the authorization generator
     */
    @RefreshScope
    @Bean
    public AuthorizationGenerator authorizationGenerator() {
        return this.authorizationGenerator;
    }

    /**
     * Ldap authorization search executor.
     *
     * @return the search executor
     */
    @RefreshScope
    @Bean
    public SearchExecutor ldapAuthorizationGeneratorUserSearchExecutor() {
        final SearchExecutor executor = new SearchExecutor();
        executor.setBaseDn(casProperties.getLdapAuthorizationProperties().getBaseDn());
        executor.setSearchFilter(new SearchFilter(casProperties.getLdapAuthorizationProperties().getSearchFilter()));
        executor.setReturnAttributes(ReturnAttributes.ALL.value());
        return executor;
    }
}
