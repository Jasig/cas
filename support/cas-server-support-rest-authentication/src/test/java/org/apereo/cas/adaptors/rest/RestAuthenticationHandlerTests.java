package org.apereo.cas.adaptors.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apereo.cas.authentication.AccountDisabledException;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasPersonDirectoryAttributeRepositoryConfiguration;
import org.apereo.cas.config.CasRestAuthenticationConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.io.StringWriter;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.ExpectedCount.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * This is {@link RestAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CasRestAuthenticationConfiguration.class,
        CasCoreAuthenticationConfiguration.class,
        CasCoreServicesConfiguration.class,
        RefreshAutoConfiguration.class,
        CasPersonDirectoryAttributeRepositoryConfiguration.class,
        CasCoreUtilConfiguration.class})
@TestPropertySource(properties = "cas.authn.rest.uri=http://localhost:8081/authn")
public class RestAuthenticationHandlerTests {

    @Autowired
    @Qualifier("restAuthenticationHandler")
    private AuthenticationHandler authenticationHandler;

    @Autowired
    @Qualifier("restAuthenticationTemplate")
    private RestTemplate restAuthenticationTemplate;

    @Test
    public void verifySuccess() throws Exception {
        final Principal principalWritten = new DefaultPrincipalFactory().createPrincipal("casuser");

        final ObjectMapper mapper = new ObjectMapper();
        final StringWriter writer = new StringWriter();
        mapper.writeValue(writer, principalWritten);
        
        final MockRestServiceServer server = MockRestServiceServer.bindTo(restAuthenticationTemplate).build();
        server.expect(manyTimes(), requestTo("http://localhost:8081/authn")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(writer.toString(), MediaType.APPLICATION_JSON));
        final HandlerResult res =
                authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        assertEquals(res.getPrincipal().getId(), "casuser");
    }

    @Test(expected= AccountDisabledException.class)
    public void verifyDisabledAccount() throws Exception {
        final MockRestServiceServer server = MockRestServiceServer.bindTo(restAuthenticationTemplate).build();
        server.expect(manyTimes(), requestTo("http://localhost:8081/authn")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));
        authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
    }

    @Test(expected= FailedLoginException.class)
    public void verifyUnauthorized() throws Exception {
        final MockRestServiceServer server = MockRestServiceServer.bindTo(restAuthenticationTemplate).build();
        server.expect(manyTimes(), requestTo("http://localhost:8081/authn")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
    }

    @Test(expected= AccountNotFoundException.class)
    public void verifyNotFound() throws Exception {
        final MockRestServiceServer server = MockRestServiceServer.bindTo(restAuthenticationTemplate).build();
        server.expect(manyTimes(), requestTo("http://localhost:8081/authn")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
    }
}



