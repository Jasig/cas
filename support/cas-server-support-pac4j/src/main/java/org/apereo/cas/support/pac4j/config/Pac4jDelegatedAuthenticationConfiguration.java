package org.apereo.cas.support.pac4j.config;

import java.util.List;
import java.util.Map;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.support.pac4j.web.flow.DelegatedClientAuthenticationAction;
import org.pac4j.core.client.Clients;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.webflow.execution.Action;

/**
 * This is {@link Pac4jDelegatedAuthenticationConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("pac4jDelegatedAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class Pac4jDelegatedAuthenticationConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;
    
    @Autowired
    @Qualifier("defaultAuthenticationSystemSupport")
    private AuthenticationSystemSupport authenticationSystemSupport;

    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;
    
    @Autowired
    @RefreshScope
    @Bean
    @Lazy
    public Action clientAction(@Qualifier("builtClients") final Clients builtClients) {
        return new DelegatedClientAuthenticationAction(builtClients, 
                authenticationSystemSupport, 
                centralAuthenticationService, 
                casProperties.getTheme().getParamName(), 
                casProperties.getLocale().getParamName(), 
                casProperties.getAuthn().getPac4j().isAutoRedirect(),
                pac4jProfileService());
    }


    @Bean
    public ProfileService<CommonProfile> pac4jProfileService() {
        return new AbstractProfileService<CommonProfile>() {

            @Override
            protected void insert(final Map<String, Object> attributes) {
            }

            @Override
            protected void update(final Map<String, Object> attributes) {
            }

            @Override
            protected void deleteById(final String id) {
            }

            @Override
            protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
                return null;
            }
        };
    }

}
