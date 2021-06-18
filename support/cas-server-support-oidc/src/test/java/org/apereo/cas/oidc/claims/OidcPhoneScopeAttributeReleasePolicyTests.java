package org.apereo.cas.oidc.claims;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.oidc.AbstractOidcTests;
import org.apereo.cas.oidc.OidcConstants;
import org.apereo.cas.services.ChainingAttributeReleasePolicy;
import org.apereo.cas.services.util.RegisteredServiceJsonSerializer;
import org.apereo.cas.util.CollectionUtils;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link OidcPhoneScopeAttributeReleasePolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("OIDC")
public class OidcPhoneScopeAttributeReleasePolicyTests extends AbstractOidcTests {
    @Test
    public void verifyOperation() {
        val policy = new OidcPhoneScopeAttributeReleasePolicy();
        assertEquals(OidcConstants.StandardScopes.PHONE.getScope(), policy.getScopeType());
        assertNotNull(policy.getAllowedNormalClaims());
        val principal = CoreAuthenticationTestUtils.getPrincipal(CollectionUtils.wrap("phone_number_verified", List.of("12134321245"),
            "phone_number", List.of("12134321245")));
        val attrs = policy.getAttributes(principal,
            CoreAuthenticationTestUtils.getService(),
            CoreAuthenticationTestUtils.getRegisteredService());
        assertTrue(policy.getAllowedNormalClaims().stream().allMatch(attrs::containsKey));
        assertTrue(policy.determineRequestedAttributeDefinitions().containsAll(policy.getAllowedNormalClaims()));
    }

    @Test
    public void verifySerialization() {
        val policy = new OidcPhoneScopeAttributeReleasePolicy();
        val chain = new ChainingAttributeReleasePolicy();
        chain.addPolicy(policy);
        val service = getOidcRegisteredService();
        service.setAttributeReleasePolicy(chain);
        val serializer = new RegisteredServiceJsonSerializer();
        val json = serializer.toString(service);
        assertNotNull(json);
        assertNotNull(serializer.from(json));
    }
}
