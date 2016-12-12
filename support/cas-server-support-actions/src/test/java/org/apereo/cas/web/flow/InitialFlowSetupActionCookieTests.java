package org.apereo.cas.web.flow;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.authentication.principal.WebApplicationServiceFactory;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.logout.config.CasCoreLogoutConfiguration;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.config.CasCookieConfiguration;
import org.apereo.cas.web.flow.config.CasCoreWebflowConfiguration;
import org.apereo.cas.web.support.ArgumentExtractor;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.apereo.cas.web.support.DefaultArgumentExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link InitialFlowSetupActionCookieTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = {
                CasCoreWebflowConfiguration.class,
                CasCoreWebConfiguration.class,
                CasCoreConfiguration.class,
                CasCoreTicketsConfiguration.class,
                CasCoreLogoutConfiguration.class,
                CasCoreAuthenticationConfiguration.class,
                CasPersonDirectoryConfiguration.class,
                CasCookieConfiguration.class,
                RefreshAutoConfiguration.class,
                CasCoreServicesConfiguration.class})
@ContextConfiguration(locations = "classpath:/core-context.xml")
@TestPropertySource(properties = "spring.aop.proxy-target-class=true")
public class InitialFlowSetupActionCookieTests {

    private static final String CONST_CONTEXT_PATH = "/test";
    private static final String CONST_CONTEXT_PATH_2 = "/test1";

    @Autowired
    private CasConfigurationProperties casProperties;

    private InitialFlowSetupAction action;
    private CookieRetrievingCookieGenerator warnCookieGenerator;
    private CookieRetrievingCookieGenerator tgtCookieGenerator;

    @Before
    public void setUp() throws Exception {
        this.warnCookieGenerator = new CookieRetrievingCookieGenerator();
        this.warnCookieGenerator.setCookiePath(StringUtils.EMPTY);
        this.tgtCookieGenerator = new CookieRetrievingCookieGenerator();
        this.tgtCookieGenerator.setCookiePath(StringUtils.EMPTY);

        final List<ArgumentExtractor> argExtractors = Collections.singletonList(new DefaultArgumentExtractor(new WebApplicationServiceFactory()));
        final ServicesManager servicesManager = mock(ServicesManager.class);
        when(servicesManager.findServiceBy(any(Service.class))).thenReturn(RegisteredServiceTestUtils.getRegisteredService("test"));
        this.action = new InitialFlowSetupAction(argExtractors, servicesManager, tgtCookieGenerator, warnCookieGenerator, casProperties);

        this.action.afterPropertiesSet();
    }

    @Test
    public void verifySettingContextPath() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath(CONST_CONTEXT_PATH);
        final MockRequestContext context = new MockRequestContext();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));

        this.action.doExecute(context);

        assertEquals(CONST_CONTEXT_PATH + '/', this.warnCookieGenerator.getCookiePath());
        assertEquals(CONST_CONTEXT_PATH + '/', this.tgtCookieGenerator.getCookiePath());
    }

    @Test
    public void verifyResettingContexPath() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath(CONST_CONTEXT_PATH);
        final MockRequestContext context = new MockRequestContext();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));

        this.action.doExecute(context);

        assertEquals(CONST_CONTEXT_PATH + '/', this.warnCookieGenerator.getCookiePath());
        assertEquals(CONST_CONTEXT_PATH + '/', this.tgtCookieGenerator.getCookiePath());

        request.setContextPath(CONST_CONTEXT_PATH_2);
        this.action.doExecute(context);

        assertNotSame(CONST_CONTEXT_PATH_2 + '/', this.warnCookieGenerator.getCookiePath());
        assertNotSame(CONST_CONTEXT_PATH_2 + '/', this.tgtCookieGenerator.getCookiePath());
        assertEquals(CONST_CONTEXT_PATH + '/', this.warnCookieGenerator.getCookiePath());
        assertEquals(CONST_CONTEXT_PATH + '/', this.tgtCookieGenerator.getCookiePath());
    }
}
