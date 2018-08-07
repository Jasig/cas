package org.apereo.cas.support.oauth.authenticator;

import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.DefaultAuthenticationEventExecutionPlan;
import org.apereo.cas.authentication.DefaultAuthenticationSystemSupport;
import org.apereo.cas.authentication.DefaultAuthenticationTransactionManager;
import org.apereo.cas.authentication.DefaultPrincipalElectionStrategy;
import org.apereo.cas.authentication.PolicyBasedAuthenticationManager;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.ServiceFactory;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;
import org.apereo.cas.ticket.registry.DefaultTicketRegistry;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.CollectionUtils;

import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

/**
 * This is {@link BaseOAuth20AuthenticatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class
})
public abstract class BaseOAuth20AuthenticatorTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected ServicesManager servicesManager;
    protected AuthenticationSystemSupport authenticationSystemSupport;
    protected ServiceFactory serviceFactory;
    protected OAuthRegisteredService service;
    protected TicketRegistry ticketRegistry;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @Before
    public void initialize() {
        this.ticketRegistry = new DefaultTicketRegistry();

        val plan = new DefaultAuthenticationEventExecutionPlan();
        plan.registerAuthenticationHandler(new SimpleTestUsernamePasswordAuthenticationHandler());

        authenticationSystemSupport = new DefaultAuthenticationSystemSupport(
            new DefaultAuthenticationTransactionManager(eventPublisher,
                new PolicyBasedAuthenticationManager(plan, true, eventPublisher)),
            new DefaultPrincipalElectionStrategy());

        service = new OAuthRegisteredService();
        service.setName("OAuth");
        service.setId(1);
        service.setServiceId("https://www.example.org");
        service.setClientSecret("secret");
        service.setClientId("client");

        servicesManager = mock(ServicesManager.class);
        when(servicesManager.getAllServices()).thenReturn(CollectionUtils.wrapList(service));

        serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.createService(anyString())).thenReturn(RegisteredServiceTestUtils.getService());

    }
}
