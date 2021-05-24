package org.apereo.cas.web.flow;

import org.apereo.cas.config.CoreSamlConfiguration;
import org.apereo.cas.config.SamlIdPAuthenticationServiceSelectionStrategyConfiguration;
import org.apereo.cas.config.SamlIdPConfiguration;
import org.apereo.cas.config.SamlIdPEndpointsConfiguration;
import org.apereo.cas.config.SamlIdPMetadataConfiguration;
import org.apereo.cas.config.SamlIdPTicketSerializationConfiguration;
import org.apereo.cas.config.SamlIdPWebflowConfiguration;
import org.apereo.cas.support.saml.idp.metadata.locator.FileSystemSamlIdPMetadataLocator;
import org.apereo.cas.support.saml.idp.metadata.locator.SamlIdPMetadataLocator;
import org.apereo.cas.support.saml.services.idp.metadata.SamlIdPMetadataDocument;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * This is {@link BaseSamlIdPWebflowTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Import({
    BaseSamlIdPWebflowTests.SamlIdPMetadataTestConfiguration.class,
    CoreSamlConfiguration.class,
    SamlIdPConfiguration.class,
    SamlIdPTicketSerializationConfiguration.class,
    SamlIdPAuthenticationServiceSelectionStrategyConfiguration.class,
    SamlIdPMetadataConfiguration.class,
    SamlIdPEndpointsConfiguration.class,
    SamlIdPWebflowConfiguration.class,
    BaseWebflowConfigurerTests.SharedTestConfiguration.class
})
public abstract class BaseSamlIdPWebflowTests extends BaseWebflowConfigurerTests {

    protected static AuthnRequest getAuthnRequestFor(final String service) {
        val authnRequest = mock(AuthnRequest.class);
        when(authnRequest.getID()).thenReturn(UUID.randomUUID().toString());
        val issuer = mock(Issuer.class);
        when(issuer.getValue()).thenReturn(service);
        when(authnRequest.getIssuer()).thenReturn(issuer);
        return authnRequest;
    }

    @TestConfiguration
    @Lazy(false)
    public static class SamlIdPMetadataTestConfiguration {
        @Autowired
        @Qualifier("samlIdPMetadataCache")
        private Cache<String, SamlIdPMetadataDocument> samlIdPMetadataCache;

        @SneakyThrows
        @Bean
        public SamlIdPMetadataLocator samlIdPMetadataLocator() {
            return new FileSystemSamlIdPMetadataLocator(
                new FileSystemResource(FileUtils.getTempDirectory()),
                samlIdPMetadataCache);
        }
    }
}


