package org.apereo.cas.support.saml.services;

import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.ServicesManagerRegisteredServiceLocator;
import org.apereo.cas.support.saml.BaseSamlIdPConfigurationTests;
import org.apereo.cas.support.saml.SamlIdPConstants;
import org.apereo.cas.support.saml.SamlProtocolConstants;
import org.apereo.cas.support.saml.services.idp.metadata.SamlRegisteredServiceServiceProviderMetadataFacade;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.util.EncodingUtils;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link SamlIdPServicesManagerRegisteredServiceLocatorTests}.
 *
 * @author Misagh Moayyed
 * @author Hayden Sartoris
 * @since 6.3.0
 */
@Tag("SAML")
public class SamlIdPServicesManagerRegisteredServiceLocatorTests extends BaseSamlIdPConfigurationTests {
    private static final String SAML_AUTHN_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><saml2p:AuthnRequest "
        + "xmlns:saml2p=\"urn:oasis:names:tc:SAML:2.0:protocol\" AssertionConsumerServiceURL=\"http://localhost:8081/callback"
        + "?client_name=SAML2Client\" ForceAuthn=\"false\" IssueInstant=\"2018-10-05T14:52:47.084Z\" "
        + "ProtocolBinding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Version=\"2.0\"><saml2:Issuer "
        + "xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">%s</saml2:Issuer><saml2p:NameIDPolicy "
        + "AllowCreate=\"true\"/></saml2p:AuthnRequest>";

    @Autowired
    @Qualifier("samlIdPServicesManagerRegisteredServiceLocator")
    private ServicesManagerRegisteredServiceLocator samlIdPServicesManagerRegisteredServiceLocator;

    @BeforeEach
    public void setup() {
        servicesManager.deleteAll();
    }

    @Test
    public void verifyOperation() {
        assertNotNull(samlIdPServicesManagerRegisteredServiceLocator);
        assertEquals(Ordered.HIGHEST_PRECEDENCE, samlIdPServicesManagerRegisteredServiceLocator.getOrder());
        val service1 = RegisteredServiceTestUtils.getRegisteredService(".+");
        service1.setEvaluationOrder(10);

        val service2 = getSamlRegisteredServiceFor(false, false, false, ".+");
        service2.setEvaluationOrder(9);

        val candidateServices = CollectionUtils.wrapList(service1, service2);
        Collections.sort(candidateServices);

        val service = webApplicationServiceFactory.createService("https://sp.testshib.org/shibboleth-sp");
        val samlRequest = EncodingUtils.encodeBase64(String.format(SAML_AUTHN_REQUEST, service.getId()));
        service.setAttributes(Map.of(SamlProtocolConstants.PARAMETER_SAML_REQUEST, List.of(samlRequest)));

        val result = samlIdPServicesManagerRegisteredServiceLocator.locate(
            (List) candidateServices,
            service, r -> r.matches("https://sp.testshib.org/shibboleth-sp"));
        assertNotNull(result);
    }

    @Test
    public void verifyEntityIdParam() {
        assertNotNull(samlIdPServicesManagerRegisteredServiceLocator);
        assertEquals(Ordered.HIGHEST_PRECEDENCE, samlIdPServicesManagerRegisteredServiceLocator.getOrder());
        val service1 = RegisteredServiceTestUtils.getRegisteredService(".+");
        service1.setEvaluationOrder(10);

        val service2 = getSamlRegisteredServiceFor(false, false, false, ".+");
        service2.setEvaluationOrder(9);

        val candidateServices = CollectionUtils.wrapList(service1, service2);
        Collections.sort(candidateServices);

        val service = webApplicationServiceFactory.createService("https://sp.testshib.org/shibboleth-sp");
        service.setAttributes(Map.of(SamlProtocolConstants.PARAMETER_ENTITY_ID, List.of(service.getId())));

        val result = samlIdPServicesManagerRegisteredServiceLocator.locate(
            (List) candidateServices,
            service, r -> r.matches("https://sp.testshib.org/shibboleth-sp"));
        assertNotNull(result);
    }

    @Test
    public void verifyProviderIdParam() {
        assertNotNull(samlIdPServicesManagerRegisteredServiceLocator);
        assertEquals(Ordered.HIGHEST_PRECEDENCE, samlIdPServicesManagerRegisteredServiceLocator.getOrder());
        val service1 = RegisteredServiceTestUtils.getRegisteredService(".+");
        service1.setEvaluationOrder(10);

        val service2 = getSamlRegisteredServiceFor(false, false, false, ".+");
        service2.setEvaluationOrder(9);

        val candidateServices = CollectionUtils.wrapList(service1, service2);
        Collections.sort(candidateServices);

        val service = webApplicationServiceFactory.createService("https://sp.testshib.org/shibboleth-sp");
        service.setAttributes(Map.of(SamlIdPConstants.PROVIDER_ID, List.of(service.getId())));

        val result = samlIdPServicesManagerRegisteredServiceLocator.locate(
            (List) candidateServices,
            service, r -> r.matches("https://sp.testshib.org/shibboleth-sp"));
        assertNotNull(result);
    }

    @Test
    public void verifyReverseOperation() {
        val service1 = RegisteredServiceTestUtils.getRegisteredService(".+");
        service1.setEvaluationOrder(9);

        val service2 = getSamlRegisteredServiceFor(false, false, false, ".+");
        service2.setEvaluationOrder(10);

        servicesManager.save(service1, service2);
        val service = webApplicationServiceFactory.createService("https://sp.testshib.org/shibboleth-sp");

        val samlRequest = EncodingUtils.encodeBase64(String.format(SAML_AUTHN_REQUEST, service.getId()));
        service.setAttributes(Map.of(SamlProtocolConstants.PARAMETER_SAML_REQUEST, List.of(samlRequest)));

        val result = servicesManager.findServiceBy(service);
        assertNotNull(result);
        assertTrue(result instanceof SamlRegisteredService);
    }

    /**
     * Locator should not trigger metadata lookups when requested
     * entityID does not match pattern for service in question.
     * <p>
     * This test first verifies that, in the case of one service entry that does not match the requested entityID, no
     * metadata lookups are performed. It then verifies that, in the case of two service entries, one matching the
     * requested entityID, exactly one metadata lookup is performed.
     */
    @Test
    public void verifyEntityIDFilter() {
        try (val mockFacade = mockStatic(SamlRegisteredServiceServiceProviderMetadataFacade.class)) {
            val service1 = getSamlRegisteredServiceFor(false, false, false, "urn:abc:def.+");
            service1.setEvaluationOrder(9);
            servicesManager.save(service1);
            mockFacade.when(() -> SamlRegisteredServiceServiceProviderMetadataFacade.get(any(), any(), anyString())).thenCallRealMethod();
            val entityID = "https://sp.testshib.org/shibboleth-sp";
            val service = webApplicationServiceFactory.createService(entityID);
            val samlRequest = EncodingUtils.encodeBase64(String.format(SAML_AUTHN_REQUEST, entityID));
            service.setAttributes(Map.of(SamlProtocolConstants.PARAMETER_SAML_REQUEST, List.of(samlRequest)));

            val res1 = servicesManager.findServiceBy(service);
            assertNull(res1);

            mockFacade.verify(() -> SamlRegisteredServiceServiceProviderMetadataFacade.get(any(), eq(service1), anyString()), never());
            val service2 = getSamlRegisteredServiceFor(false, false, false, ".+");
            service2.setEvaluationOrder(10);
            servicesManager.save(service2);

            val res2 = servicesManager.findServiceBy(service);
            assertNotNull(res2);
            mockFacade.verify(() -> SamlRegisteredServiceServiceProviderMetadataFacade.get(any(), eq(service2), eq(entityID)));
        }
    }

    @Test
    public void verifyMatchWithEncodedParam() {
        try (val mockFacade = mockStatic(SamlRegisteredServiceServiceProviderMetadataFacade.class)) {

            val service1 = getSamlRegisteredServiceFor(".*app.samlclient.edu.*/sp");
            service1.setEvaluationOrder(4);
            servicesManager.save(service1);

            val service2 = getSamlRegisteredServiceFor("4464.+");
            service2.setMetadataLocation("http://localhost:9428/entities/{0}");
            service2.setEvaluationOrder(1000);
            servicesManager.save(service2);

            mockFacade.when(() -> SamlRegisteredServiceServiceProviderMetadataFacade.get(any(), any(), anyString()))
                .thenCallRealMethod();

            val service = mock(WebApplicationService.class);
            when(service.getId()).thenReturn("https://sso.cas.edu/cas?entityId=https%3A%2F%2Fapp.samlclient.edu%3A9443%2Fsp");
            when(service.getAttributes()).thenReturn(Map.of("entityId", List.of("https://app.samlclient.edu:9443/sp")));

            val res1 = servicesManager.findServiceBy(service);
            assertNull(res1);

            mockFacade.verify(() -> SamlRegisteredServiceServiceProviderMetadataFacade.get(any(), eq(service2), anyString()), never());
            mockFacade.verify(() -> SamlRegisteredServiceServiceProviderMetadataFacade.get(any(), eq(service1), anyString()));
        }
    }

    @Test
    public void verifyNoSamlService() {
        try (val mockFacade = mockStatic(SamlRegisteredServiceServiceProviderMetadataFacade.class)) {

            val service1 = RegisteredServiceTestUtils.getRegisteredService(".*app.samlclient.edu.*/sp");
            service1.setEvaluationOrder(4);
            servicesManager.save(service1);

            mockFacade.when(() -> SamlRegisteredServiceServiceProviderMetadataFacade.get(any(), any(), anyString()))
                .thenCallRealMethod();

            val service = RegisteredServiceTestUtils.getService("app.samlclient.edu");
            val res1 = servicesManager.findServiceBy(service);
            assertNull(res1);
        }
    }
}
