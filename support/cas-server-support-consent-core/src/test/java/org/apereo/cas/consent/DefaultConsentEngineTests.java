package org.apereo.cas.consent;

import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasConsentCoreConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasRegisteredServicesTestConfiguration;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.consent.DefaultRegisteredServiceConsentPolicy;

import lombok.val;
import org.apereo.inspektr.common.web.ClientInfo;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link DefaultConsentEngineTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = {
    CasRegisteredServicesTestConfiguration.class,
    CasConsentCoreConfiguration.class,
    CasCoreAuditConfiguration.class,
    RefreshAutoConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasCoreUtilConfiguration.class
})
@DirtiesContext
@Tag("Simple")
public class DefaultConsentEngineTests {
    @Autowired
    @Qualifier("consentEngine")
    private ConsentEngine consentEngine;

    @BeforeAll
    public static void beforeAll() {
        val request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.setLocalAddr("127.0.0.1");
        ClientInfoHolder.setClientInfo(new ClientInfo(request));
    }

    @Test
    public void verifyConsentIsAlwaysRequired() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication("casuser");
        val service = CoreAuthenticationTestUtils.getService();
        val consentService = CoreAuthenticationTestUtils.getRegisteredService("consentService");
        val policy = new ReturnAllAttributeReleasePolicy();
        policy.setConsentPolicy(new DefaultRegisteredServiceConsentPolicy());
        when(consentService.getAttributeReleasePolicy()).thenReturn(policy);
        val decision = this.consentEngine.storeConsentDecision(service, consentService,
            authentication, 14, ChronoUnit.DAYS, ConsentReminderOptions.ALWAYS);
        assertNotNull(decision);
        val result = this.consentEngine.isConsentRequiredFor(service, consentService, authentication);
        assertNotNull(result);
        assertTrue(result.isRequired());
        assertEquals(decision, result.getConsentDecision());
    }

    @Test
    public void verifyConsentIsRequiredByAttributeName() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication("casuser");
        val service = CoreAuthenticationTestUtils.getService();
        val consentService = CoreAuthenticationTestUtils.getRegisteredService("consentService");
        val policy = new ReturnAllAttributeReleasePolicy();
        policy.setConsentPolicy(new DefaultRegisteredServiceConsentPolicy());
        when(consentService.getAttributeReleasePolicy()).thenReturn(policy);
        val decision = this.consentEngine.storeConsentDecision(service, consentService,
            authentication, 14, ChronoUnit.DAYS, ConsentReminderOptions.ATTRIBUTE_NAME);
        assertNotNull(decision);
        val result = this.consentEngine.isConsentRequiredFor(service, consentService, authentication);
        assertNotNull(result);
        assertFalse(result.isRequired());
    }

    @Test
    public void verifyConsentFound() {
        val authentication = CoreAuthenticationTestUtils.getAuthentication("casuser");
        val service = CoreAuthenticationTestUtils.getService();
        val consentService = CoreAuthenticationTestUtils.getRegisteredService("consentService");
        val policy = new ReturnAllAttributeReleasePolicy();
        policy.setConsentPolicy(new DefaultRegisteredServiceConsentPolicy());
        when(consentService.getAttributeReleasePolicy()).thenReturn(policy);
        val decision = this.consentEngine.storeConsentDecision(service, consentService,
            authentication, 14, ChronoUnit.DAYS, ConsentReminderOptions.ATTRIBUTE_NAME);
        assertNotNull(decision);
        val decision2 = this.consentEngine.findConsentDecision(service, consentService, authentication);
        assertEquals(decision, decision2);
    }
}
