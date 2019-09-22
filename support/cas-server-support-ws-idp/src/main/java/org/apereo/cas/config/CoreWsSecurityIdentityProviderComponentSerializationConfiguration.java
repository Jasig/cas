package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.serialization.ComponentSerializationPlan;
import org.apereo.cas.util.serialization.ComponentSerializationPlanConfigurer;
import org.apereo.cas.ws.idp.services.CustomNamespaceWSFederationClaimsReleasePolicy;
import org.apereo.cas.ws.idp.services.WSFederationClaimsReleasePolicy;
import org.apereo.cas.ws.idp.services.WSFederationRegisteredService;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link CoreWsSecurityIdentityProviderComponentSerializationConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration(value = "coreWsSecurityIdentityProviderComponentSerializationConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CoreWsSecurityIdentityProviderComponentSerializationConfiguration implements ComponentSerializationPlanConfigurer {
    @Override
    public void configureComponentSerializationPlan(final ComponentSerializationPlan plan) {
        plan.registerSerializableClass(WSFederationRegisteredService.class);
        plan.registerSerializableClass(CustomNamespaceWSFederationClaimsReleasePolicy.class);
        plan.registerSerializableClass(WSFederationClaimsReleasePolicy.class);
    }
}
