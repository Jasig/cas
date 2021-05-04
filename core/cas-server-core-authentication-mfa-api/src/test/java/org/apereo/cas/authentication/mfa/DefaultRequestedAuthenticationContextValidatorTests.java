package org.apereo.cas.authentication.mfa;

import org.apereo.cas.authentication.DefaultMultifactorAuthenticationFailureModeEvaluator;
import org.apereo.cas.authentication.MultifactorAuthenticationPrincipalResolver;
import org.apereo.cas.authentication.MultifactorAuthenticationUtils;
import org.apereo.cas.authentication.bypass.AuthenticationMultifactorAuthenticationProviderBypassEvaluator;
import org.apereo.cas.authentication.bypass.MultifactorAuthenticationProviderBypassEvaluator;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.mfa.BaseMultifactorAuthenticationProviderProperties;
import org.apereo.cas.configuration.model.support.mfa.MultifactorAuthenticationProviderBypassProperties;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.spring.ApplicationContextProvider;
import org.apereo.cas.validation.Assertion;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link DefaultMultifactorAuthenticationContextValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.0.5
 */
@Tag("MFA")
public class DefaultRequestedAuthenticationContextValidatorTests {

    private static final String CASUSER = "casuser";

    private static final Map<String, List<Object>> AUTH_ATTRIBUTES = CollectionUtils.wrap("givenName", "CAS");

    @Test
    public void verifyNoRequestedAuthenticationContext() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());

        val validator = MultifactorAuthenticationTestUtils.mockRequestAuthnContextValidator(Optional.empty(),
            applicationContext, BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.UNDEFINED.toString());
        val assertion = mock(Assertion.class);
        val auth = MultifactorAuthenticationTestUtils.getAuthentication(CASUSER);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertTrue(result.isSuccess());
    }

    @Test
    public void verifyRequestedAuthenticationContextBypassed() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());

        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val props = MultifactorAuthenticationTestUtils.getAuthenticationBypassProperties();
        val bypass = new AuthenticationMultifactorAuthenticationProviderBypassEvaluator(props, provider.getId());
        provider.setBypassEvaluator(bypass);
        val validator = MultifactorAuthenticationTestUtils.mockRequestAuthnContextValidator(Optional.of(provider),
            applicationContext, BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.UNDEFINED.toString());
        val assertion = mock(Assertion.class);
        val principal = MultifactorAuthenticationTestUtils.getPrincipal(CASUSER, CollectionUtils.wrap(CASUSER, AUTH_ATTRIBUTES));
        val auth = MultifactorAuthenticationTestUtils.getAuthentication(principal, AUTH_ATTRIBUTES);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertTrue(result.isSuccess());
    }

    @Test
    public void verifyRequestedAuthenticationContextNotBypassed() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());
        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setAuthenticationAttributeName("givenName");
        props.setAuthenticationAttributeValue("Not Bypassed");
        val bypass = new AuthenticationMultifactorAuthenticationProviderBypassEvaluator(props, TestMultifactorAuthenticationProvider.ID);
        provider.setBypassEvaluator(bypass);
        val validator = MultifactorAuthenticationTestUtils.mockRequestAuthnContextValidator(Optional.of(provider),
            applicationContext, BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.UNDEFINED.toString());
        val assertion = mock(Assertion.class);
        val principal = MultifactorAuthenticationTestUtils.getPrincipal(CASUSER);
        val auth = MultifactorAuthenticationTestUtils.getAuthentication(principal, AUTH_ATTRIBUTES);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertFalse(result.isSuccess());
    }

    @Test
    public void verifyRequestedAuthenticationIsAlreadyBypass() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());

        val provider = TestMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val props = new MultifactorAuthenticationProviderBypassProperties();
        props.setAuthenticationAttributeName("givenName");
        props.setAuthenticationAttributeValue("Not Bypassed");
        val bypass = new AuthenticationMultifactorAuthenticationProviderBypassEvaluator(props, TestMultifactorAuthenticationProvider.ID);
        provider.setBypassEvaluator(bypass);
        val validator = MultifactorAuthenticationTestUtils.mockRequestAuthnContextValidator(Optional.of(provider),
            applicationContext, BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.UNDEFINED.toString());
        val assertion = mock(Assertion.class);
        val principal = MultifactorAuthenticationTestUtils.getPrincipal(CASUSER);

        val attrs = new HashMap<String, List<Object>>();
        attrs.put(MultifactorAuthenticationProviderBypassEvaluator.AUTHENTICATION_ATTRIBUTE_BYPASS_MFA, List.of(true));
        attrs.put(MultifactorAuthenticationProviderBypassEvaluator.AUTHENTICATION_ATTRIBUTE_BYPASS_MFA_PROVIDER,
            List.of(TestMultifactorAuthenticationProvider.ID));

        val auth = MultifactorAuthenticationTestUtils.getAuthentication(principal, attrs);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertTrue(result.isSuccess());
    }

    @Test
    public void verifyRequestedAuthenticationContextNoProvider() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());

        val validator = MultifactorAuthenticationTestUtils
            .mockRequestAuthnContextValidator(Optional.of(new TestMultifactorAuthenticationProvider()),
                applicationContext, BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.UNDEFINED.toString());
        val assertion = mock(Assertion.class);
        val principal = MultifactorAuthenticationTestUtils.getPrincipal(CASUSER);
        val auth = MultifactorAuthenticationTestUtils.getAuthentication(principal, AUTH_ATTRIBUTES);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertFalse(result.isSuccess());
    }

    @Test
    public void verifyGlobalFailureModeFailsOpen() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());

        val provider = TestUnavailableMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val casProperties = new CasConfigurationProperties();
        casProperties.getAuthn().getMfa().getCore().setGlobalFailureMode(BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.OPEN);
        val failureEvaluator = new DefaultMultifactorAuthenticationFailureModeEvaluator(casProperties);
        provider.setFailureModeEvaluator(failureEvaluator);
        val validator = MultifactorAuthenticationTestUtils
            .mockRequestAuthnContextValidator(Optional.of(provider),
                applicationContext, BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.UNDEFINED.toString());
        val assertion = mock(Assertion.class);
        val principal = MultifactorAuthenticationTestUtils.getPrincipal(CASUSER);
        val auth = MultifactorAuthenticationTestUtils.getAuthentication(principal, AUTH_ATTRIBUTES);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertTrue(result.isSuccess());
    }

    @Test
    public void verifyGlobalFailureModeFailsClosed() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());

        TestUnavailableMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val provider = MultifactorAuthenticationUtils.getMultifactorAuthenticationProviderById(TestUnavailableMultifactorAuthenticationProvider.ID,
            applicationContext);
        val casProperties = new CasConfigurationProperties();
        casProperties.getAuthn().getMfa().getCore().setGlobalFailureMode(BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.CLOSED);
        val failureEvaluator = new DefaultMultifactorAuthenticationFailureModeEvaluator(casProperties);
        ((TestUnavailableMultifactorAuthenticationProvider) provider.get()).setFailureModeEvaluator(failureEvaluator);
        val validator = MultifactorAuthenticationTestUtils
            .mockRequestAuthnContextValidator(Optional.of(new TestMultifactorAuthenticationProvider()),
                applicationContext, BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.UNDEFINED.toString());
        val assertion = mock(Assertion.class);
        val principal = MultifactorAuthenticationTestUtils.getPrincipal(CASUSER);
        val auth = MultifactorAuthenticationTestUtils.getAuthentication(principal, AUTH_ATTRIBUTES);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertFalse(result.isSuccess());
    }

    @Test
    public void verifyServiceFailureModeFailsOpen() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());

        val provider = TestUnavailableMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val casProperties = new CasConfigurationProperties();
        casProperties.getAuthn().getMfa().getCore()
            .setGlobalFailureMode(BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.CLOSED);
        val failureEvaluator = new DefaultMultifactorAuthenticationFailureModeEvaluator(casProperties);
        provider.setFailureModeEvaluator(failureEvaluator);
        val validator = MultifactorAuthenticationTestUtils
            .mockRequestAuthnContextValidator(Optional.of(provider),
                applicationContext, BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.OPEN.toString());
        val assertion = mock(Assertion.class);
        val service = MultifactorAuthenticationTestUtils.getService("service");
        when(assertion.getService()).thenReturn(service);
        val principal = MultifactorAuthenticationTestUtils.getPrincipal(CASUSER);
        val auth = MultifactorAuthenticationTestUtils.getAuthentication(principal, AUTH_ATTRIBUTES);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertTrue(result.isSuccess());
    }

    @Test
    public void verifyServiceFailureModeFailsClosed() {
        val applicationContext = new StaticApplicationContext();
        applicationContext.refresh();
        ApplicationContextProvider.holdApplicationContext(applicationContext);
        ApplicationContextProvider.registerBeanIntoApplicationContext(applicationContext,
            MultifactorAuthenticationPrincipalResolver.identical(), UUID.randomUUID().toString());

        val provider = TestUnavailableMultifactorAuthenticationProvider.registerProviderIntoApplicationContext(applicationContext);
        val casProperties = new CasConfigurationProperties();
        casProperties.getAuthn().getMfa().getCore().setGlobalFailureMode(BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.OPEN);
        val failureEvaluator = new DefaultMultifactorAuthenticationFailureModeEvaluator(casProperties);
        provider.setFailureModeEvaluator(failureEvaluator);
        val validator = MultifactorAuthenticationTestUtils
            .mockRequestAuthnContextValidator(Optional.of(provider), applicationContext,
                BaseMultifactorAuthenticationProviderProperties.MultifactorAuthenticationProviderFailureModes.CLOSED.toString());
        val assertion = mock(Assertion.class);
        val service = MultifactorAuthenticationTestUtils.getService("service");
        when(assertion.getService()).thenReturn(service);
        val principal = MultifactorAuthenticationTestUtils.getPrincipal(CASUSER);
        val auth = MultifactorAuthenticationTestUtils.getAuthentication(principal, AUTH_ATTRIBUTES);
        when(assertion.getPrimaryAuthentication()).thenReturn(auth);
        val result = validator.validateAuthenticationContext(assertion, new MockHttpServletRequest());
        assertFalse(result.isSuccess());
    }
}
