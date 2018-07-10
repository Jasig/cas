package org.apereo.cas.interrupt;

import lombok.val;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.services.RegisteredServiceProperty;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link RegexAttributeInterruptInquirerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class RegexAttributeInterruptInquirerTests {
    @Test
    public void verifyResponseCanBeFoundFromAttributes() {
        val q =
            new RegexAttributeInterruptInquirer("member..", "CA.|system");
        val response = q.inquire(CoreAuthenticationTestUtils.getAuthentication("casuser"),
            CoreAuthenticationTestUtils.getRegisteredService(),
            CoreAuthenticationTestUtils.getService(),
            CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        assertNotNull(response);
        assertFalse(response.isBlock());
        assertTrue(response.isSsoEnabled());
        assertTrue(response.isInterrupt());
    }

    @Test
    public void verifyInterruptSkipped() {
        val q =
            new RegexAttributeInterruptInquirer("member..", "CA.|system");
        val registeredService = CoreAuthenticationTestUtils.getRegisteredService();

        val properties = new LinkedHashMap<String, RegisteredServiceProperty>();
        val value = new DefaultRegisteredServiceProperty();
        value.addValue(Boolean.TRUE.toString());
        properties.put(RegisteredServiceProperty.RegisteredServiceProperties.SKIP_INTERRUPT_NOTIFICATIONS.getPropertyName(), value);
        when(registeredService.getProperties()).thenReturn(properties);
        val response = q.inquire(CoreAuthenticationTestUtils.getAuthentication("casuser"),
            registeredService,
            CoreAuthenticationTestUtils.getService(),
            CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword());
        assertNotNull(response);
        assertFalse(response.isInterrupt());
    }
}
