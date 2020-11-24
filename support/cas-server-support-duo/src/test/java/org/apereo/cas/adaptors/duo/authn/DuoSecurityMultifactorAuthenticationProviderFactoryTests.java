package org.apereo.cas.adaptors.duo.authn;

import org.apereo.cas.BaseCasWebflowMultifactorAuthenticationTests;
import org.apereo.cas.adaptors.duo.BaseDuoSecurityTests;
import org.apereo.cas.authentication.MultifactorAuthenticationProviderFactoryBean;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.mfa.DuoSecurityMultifactorProperties;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link DuoSecurityMultifactorAuthenticationProviderFactoryTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@Tag("MFA")
@SpringBootTest(classes = BaseDuoSecurityTests.SharedTestConfiguration.class,
    properties = {
        "cas.authn.mfa.duo[0].duo-secret-key=Q2IU2i8BFNd6VYflZT8Evl6lF7oPlj3PM15BmRU7",
        "cas.authn.mfa.duo[0].duo-application-key=abcdefghijklmnop",
        "cas.authn.mfa.duo[0].duo-integration-key=DIOXVRZD2UMZ8XXMNFQ5",
        "cas.authn.mfa.duo[0].duo-api-host=theapi.duosecurity.com"
    })
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class DuoSecurityMultifactorAuthenticationProviderFactoryTests extends BaseCasWebflowMultifactorAuthenticationTests {
    @Autowired
    @Qualifier("duoProviderFactory")
    private MultifactorAuthenticationProviderFactoryBean<
        DuoSecurityMultifactorAuthenticationProvider, DuoSecurityMultifactorProperties>
        duoProviderFactory;

    @Test
    public void verifyBasicProvider() {
        val props = casProperties.getAuthn().getMfa().getDuo().get(0);
        props.setMode(DuoSecurityMultifactorProperties.DuoSecurityIntegrationModes.WEBSDK);
        val provider = duoProviderFactory.createProvider(props);
        assertTrue(provider.getDuoAuthenticationService() instanceof BasicDuoSecurityAuthenticationService);
    }

    @Test
    public void verifyUniversalProvider() {
        val props = casProperties.getAuthn().getMfa().getDuo().get(0);
        props.setMode(DuoSecurityMultifactorProperties.DuoSecurityIntegrationModes.UNIVERSAL);
        val provider = duoProviderFactory.createProvider(props);
        assertTrue(provider.getDuoAuthenticationService() instanceof UniversalPromptDuoSecurityAuthenticationService);
    }

}
