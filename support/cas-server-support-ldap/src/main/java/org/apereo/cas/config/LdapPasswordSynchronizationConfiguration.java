package org.apereo.cas.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.LdapPasswordSynchronizationAuthenticationPostProcessor;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.core.authentication.passwordsync.LdapPasswordSynchronizationProperties;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

/**
 * This is {@link LdapPasswordSynchronizationConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Configuration(value = "ldapPasswordSynchronizationConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class LdapPasswordSynchronizationConfiguration {
    @Autowired
    private CasConfigurationProperties casProperties;

    @ConditionalOnMissingBean(name = "ldapPasswordSynchronizationAuthenticationEventExecutionPlanConfigurer")
    @Bean(destroyMethod="close")
    public AuthenticationEventExecutionPlanConfigurer ldapPasswordSynchronizationAuthenticationEventExecutionPlanConfigurer() {
        return new AuthenticationEventExecutionPlanConfigurer() {
            private HashSet<LdapPasswordSynchronizationAuthenticationPostProcessor> authenticationProcessors;

            public void close() {
                LOGGER.debug("Closing LDAP connection factories");
                authenticationProcessors.forEach(LdapPasswordSynchronizationAuthenticationPostProcessor::closeSearchFactory);
            }

            @Override
            public void configureAuthenticationExecutionPlan(final AuthenticationEventExecutionPlan plan) {
                val ldap = casProperties.getAuthn().getPasswordSync().getLdap();
                ldap.stream()
                    .filter(LdapPasswordSynchronizationProperties::isEnabled)
                    .forEach(instance -> {
                        val authenticationProcessor = new LdapPasswordSynchronizationAuthenticationPostProcessor(instance);
                        authenticationProcessors.add(authenticationProcessor);
                        plan.registerAuthenticationPostProcessor(authenticationProcessor);
                    });
            }
        };
    }
}
