package org.apereo.cas.mfa.accepto;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.config.AccepttoMultifactorAuthenticationConfiguration;
import org.apereo.cas.config.AccepttoMultifactorAuthenticationEventExecutionPlanConfiguration;
import org.apereo.cas.config.AccepttoMultifactorAuthenticationMultifactorProviderBypassConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.MockWebServer;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.flow.config.CasWebflowContextConfiguration;
import org.apereo.cas.web.support.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.test.MockRequestContext;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link AccepttoMultifactorAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("RestfulApi")
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    AccepttoMultifactorAuthenticationConfiguration.class,
    AccepttoMultifactorAuthenticationEventExecutionPlanConfiguration.class,
    AccepttoMultifactorAuthenticationMultifactorProviderBypassConfiguration.class,
    CasWebflowContextConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasCoreWebflowConfiguration.class,
    CasWebflowContextConfiguration.class,
    CasCoreServicesConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class
})
@TestPropertySource(properties = {
    "cas.authn.mfa.acceptto.apiUrl=http://localhost:5002",
    "cas.authn.mfa.acceptto.application-id=thisisatestid",
    "cas.authn.mfa.acceptto.secret=thisisasecret"
})
public class AccepttoMultifactorAuthenticationHandlerTests {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private CasConfigurationProperties casProperties;

    @Test
    public void verifyOperation() throws Exception {
        val data = MAPPER.writeValueAsString(CollectionUtils.wrap("device_id", "devicid-test", "status", "approved"));
        try (val webServer = new MockWebServer(5002,
            new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8), "Output"), HttpStatus.OK)) {
            webServer.start();
            val handler = new AccepttoMultifactorAuthenticationHandler(mock(ServicesManager.class),
                PrincipalFactoryUtils.newPrincipalFactory(), casProperties.getAuthn().getMfa().getAcceptto());
            val context = new MockRequestContext();
            val request = new MockHttpServletRequest();
            val response = new MockHttpServletResponse();
            context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, response));
            WebUtils.putAuthentication(CoreAuthenticationTestUtils.getAuthentication("casuser"), context);
            RequestContextHolder.setRequestContext(context);

            val credential = new AccepttoMultifactorTokenCredential("test-channel");
            assertTrue(handler.supports(credential));
            assertTrue(handler.supports(AccepttoMultifactorTokenCredential.class));
            val result = handler.authenticate(credential);
            assertNotNull(result.getPrincipal());
        }
    }
}
