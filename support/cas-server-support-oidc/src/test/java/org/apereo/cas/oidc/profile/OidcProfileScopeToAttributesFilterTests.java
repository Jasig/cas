package org.apereo.cas.oidc.profile;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.oidc.OidcConstants;
import org.apereo.cas.services.ChainingAttributeReleasePolicy;
import org.apereo.cas.services.OidcRegisteredService;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.util.CollectionUtils;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.J2EContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link OidcProfileScopeToAttributesFilterTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("OIDC")
public class OidcProfileScopeToAttributesFilterTests extends AbstractOidcTests {

    @Test
    public void verifyOperationFilterWithoutOpenId() {
        val service = getOidcRegisteredService();
        val accessToken = mock(AccessToken.class);
        val context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        val original = CoreAuthenticationTestUtils.getPrincipal();
        val principal = profileScopeToAttributesFilter.filter(CoreAuthenticationTestUtils.getService(),
            original, service, context, accessToken);
        assertEquals(original, principal);
    }

    @Test
    public void verifyOperationFilterWithOpenId() {
        val service = getOidcRegisteredService();
        val accessToken = mock(AccessToken.class);
        when(accessToken.getTicketGrantingTicket()).thenReturn(new MockTicketGrantingTicket("casuser"));
        when(accessToken.getScopes()).thenReturn(CollectionUtils.wrapList(
            OidcConstants.StandardScopes.OPENID.getScope(),
            OidcConstants.StandardScopes.PHONE.getScope(),
            OidcConstants.StandardScopes.PROFILE.getScope(),
            OidcConstants.StandardScopes.ADDRESS.getScope(),
            OidcConstants.StandardScopes.EMAIL.getScope()));

        service.getScopes().add(OidcConstants.StandardScopes.EMAIL.getScope());
        service.getScopes().add(OidcConstants.StandardScopes.ADDRESS.getScope());
        service.getScopes().add(OidcConstants.StandardScopes.PHONE.getScope());
        service.getScopes().add(OidcConstants.StandardScopes.PROFILE.getScope());
        val context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        val original = CoreAuthenticationTestUtils.getPrincipal(
            CollectionUtils.wrap("email", "casuser@example.org", "address", "1234 Main Street",
                "phone", "123445677", "name", "CAS", "gender", "male"));
        val principal = profileScopeToAttributesFilter.filter(CoreAuthenticationTestUtils.getService(),
            original, service, context, accessToken);
        assertTrue(principal.getAttributes().containsKey("name"));
        assertTrue(principal.getAttributes().containsKey("address"));
        assertTrue(principal.getAttributes().containsKey("gender"));
        assertTrue(principal.getAttributes().containsKey("email"));
        assertEquals(4, principal.getAttributes().size());
    }

    @Test
    public void verifyOperationRecon() {
        var service = getOidcRegisteredService();
        service.getScopes().add(OidcConstants.StandardScopes.ADDRESS.getScope());
        service.getScopes().add(OidcConstants.StandardScopes.EMAIL.getScope());
        service.getScopes().add(OidcConstants.StandardScopes.OFFLINE_ACCESS.getScope());
        service.getScopes().add(OidcConstants.StandardScopes.OPENID.getScope());
        service.getScopes().add(OidcConstants.StandardScopes.PHONE.getScope());
        service.getScopes().add(OidcConstants.StandardScopes.PROFILE.getScope());
        service = (OidcRegisteredService) profileScopeToAttributesFilter.reconcile(service);
        val policy = service.getAttributeReleasePolicy();
        assertTrue(policy instanceof ChainingAttributeReleasePolicy);
        val chain = (ChainingAttributeReleasePolicy) policy;
        assertEquals(4, chain.size());
    }
}
