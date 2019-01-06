package org.apereo.cas.ticket.support;

import org.apereo.cas.ticket.ExpirationPolicy;
import org.apereo.cas.util.serialization.AbstractJacksonBackedStringSerializer;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This is {@link SurrogateSessionExpirationPolicyJsonSerializerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class SurrogateSessionExpirationPolicyJsonSerializerTests {
    @Test
    public void verifyOperation() {
        val policy = new SurrogateSessionExpirationPolicy(new HardTimeoutExpirationPolicy(100));
        val serializer = new SurrogateSessionExpirationPolicyJsonSerializer();
        val result = serializer.toString(policy);
        assertNotNull(result);
        val newPolicy = serializer.from(result);
        assertNotNull(newPolicy);
        assertEquals(policy, newPolicy);
    }

    private static class SurrogateSessionExpirationPolicyJsonSerializer extends AbstractJacksonBackedStringSerializer<ExpirationPolicy> {
        private static final long serialVersionUID = -7883370764375218898L;

        @Override
        protected Class<ExpirationPolicy> getTypeToSerialize() {
            return ExpirationPolicy.class;
        }
    }

}
