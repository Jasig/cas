package org.apereo.cas.web.flow;

import org.apereo.cas.config.CoreSamlAutoConfiguration;
import org.apereo.cas.config.DelegatedAuthenticationAutoConfiguration;
import org.apereo.cas.config.SamlIdentityProviderDiscoveryAutoConfiguration;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.webflow.engine.Flow;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link SamlIdentityProviderDiscoveryWebflowConfigurerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Import({
    WebMvcAutoConfiguration.class,
    MockMvcAutoConfiguration.class,
    ErrorMvcAutoConfiguration.class,
    CoreSamlAutoConfiguration.class,
    DelegatedAuthenticationAutoConfiguration.class,
    SamlIdentityProviderDiscoveryAutoConfiguration.class
})
@Tag("SAML2Web")
class SamlIdentityProviderDiscoveryWebflowConfigurerTests extends BaseWebflowConfigurerTests {
    @Test
    void verifyOperation() throws Throwable {
        assertFalse(casWebflowExecutionPlan.getWebflowConfigurers().isEmpty());
        val flow = (Flow) this.loginFlowDefinitionRegistry.getFlowDefinition(CasWebflowConfigurer.FLOW_ID_LOGIN);
        assertNotNull(flow);
    }
}
