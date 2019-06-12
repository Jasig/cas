package org.apereo.cas.pm.history;

import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.pm.PasswordChangeRequest;
import org.apereo.cas.pm.PasswordHistoryService;
import org.apereo.cas.pm.config.PasswordManagementConfiguration;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link InMemoryPasswordHistoryServiceTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    PasswordManagementConfiguration.class,
    CasCoreUtilConfiguration.class
})
@TestPropertySource(properties = {
    "cas.authn.pm.enabled=true",
    "cas.authn.pm.history.enabled=true"
})
public class InMemoryPasswordHistoryServiceTests {
    @Autowired
    @Qualifier("passwordHistoryService")
    private PasswordHistoryService passwordHistoryService;

    @Test
    public void verifyValidity() {
        val request = new PasswordChangeRequest("casuser", "password", "password");
        assertFalse(passwordHistoryService.exists(request));
        assertTrue(passwordHistoryService.store(request));
        assertTrue(passwordHistoryService.exists(request));
        assertFalse(passwordHistoryService.fetchAll().isEmpty());
        assertFalse(passwordHistoryService.fetch("casuser").isEmpty());
    }
}
