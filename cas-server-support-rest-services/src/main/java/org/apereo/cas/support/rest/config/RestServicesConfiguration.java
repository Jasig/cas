package org.apereo.cas.support.rest.config;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.rest.RegisteredServiceResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link RestServicesConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("restServicesConfiguration")
public class RestServicesConfiguration {

    @Autowired
    @Qualifier("centralAuthenticationService")
    private CentralAuthenticationService centralAuthenticationService;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Bean
    public RegisteredServiceResource registeredServiceResourceRestController() {
        final RegisteredServiceResource r = new RegisteredServiceResource();
        r.setCentralAuthenticationService(centralAuthenticationService);
        r.setServicesManager(servicesManager);
        return r;
    }
}



