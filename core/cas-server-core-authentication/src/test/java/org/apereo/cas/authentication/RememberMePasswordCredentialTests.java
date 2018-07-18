package org.apereo.cas.authentication;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for RememberMeUsernamePasswordCredential.
 *
 * @author Scott Battaglia
 * @since 3.2.1
 */
@Slf4j
public class RememberMePasswordCredentialTests {

    @Test
    public void verifyGettersAndSetters() {
        val c = new RememberMeUsernamePasswordCredential();
        c.setPassword("password");
        c.setUsername("username");
        c.setRememberMe(true);

        assertEquals("username", c.getUsername());
        assertEquals("password", c.getPassword());
        assertTrue(c.isRememberMe());
    }
}
