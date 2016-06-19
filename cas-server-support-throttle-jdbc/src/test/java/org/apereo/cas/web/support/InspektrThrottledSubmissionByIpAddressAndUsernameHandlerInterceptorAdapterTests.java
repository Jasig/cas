package org.apereo.cas.web.support;

import org.apereo.cas.audit.config.CasSupportJdbcAuditConfiguration;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.AuthenticationManager;
import org.apereo.cas.authentication.AuthenticationTransaction;
import org.apereo.cas.authentication.TestUtils;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.web.support.config.CasJdbcThrottlingConfiguration;
import org.apereo.inspektr.common.web.ClientInfo;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

/**
 * Unit test for {@link InspektrThrottledSubmissionByIpAddressAndUsernameHandlerInterceptorAdapter}.
 *
 * @author Marvin S. Addison
 * @since 3.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(locations = {
        "classpath:/jdbc-audit-context.xml"
}, classes = {CasJdbcThrottlingConfiguration.class, CasCoreAuditConfiguration.class,
        CasCoreConfiguration.class, CasCoreServicesConfiguration.class,
        CasCoreUtilConfiguration.class, CasCoreTicketsConfiguration.class,
        CasCoreLogoutConfiguration.class,
        CasCoreAuthenticationConfiguration.class, CasSupportJdbcAuditConfiguration.class},
        initializers = ConfigFileApplicationContextInitializer.class)
public class InspektrThrottledSubmissionByIpAddressAndUsernameHandlerInterceptorAdapterTests extends
                AbstractThrottledSubmissionHandlerInterceptorAdapterTests {

    @Autowired
    @Qualifier("inspektrIpAddressUsernameThrottle")
    private HandlerInterceptorAdapter throttle;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Override
    protected AsyncHandlerInterceptor getThrottle() {
        return throttle;
    }

    @Override
    protected MockHttpServletResponse loginUnsuccessfully(final String username, final String fromAddress)
            throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        request.setMethod("POST");
        request.setParameter("username", username);
        request.setRemoteAddr(fromAddress);
        final MockRequestContext context = new MockRequestContext();
        context.setCurrentEvent(new Event("", "error"));
        request.setAttribute("flowRequestContext", context);
        ClientInfoHolder.setClientInfo(new ClientInfo(request));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        getThrottle().preHandle(request, response, null);

        try {
            authenticationManager.authenticate(AuthenticationTransaction.wrap(TestUtils.getService(), 
                    badCredentials(username)));
        } catch (final AuthenticationException e) {
            getThrottle().postHandle(request, response, null, null);
            return response;
        }
        fail("Expected AbstractAuthenticationException");
        return null;
    }

    private static UsernamePasswordCredential badCredentials(final String username) {
        final UsernamePasswordCredential credentials = new UsernamePasswordCredential();
        credentials.setUsername(username);
        credentials.setPassword("badpassword");
        return credentials;
    }
}
