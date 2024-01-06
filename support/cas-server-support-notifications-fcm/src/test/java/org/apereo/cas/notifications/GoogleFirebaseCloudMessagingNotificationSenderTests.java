package org.apereo.cas.notifications;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.config.CasCoreNotificationsAutoConfiguration;
import org.apereo.cas.config.CasCoreUtilAutoConfiguration;
import org.apereo.cas.config.CasGoogleFirebaseCloudMessagingAutoConfiguration;
import org.apereo.cas.notifications.push.NotificationSender;
import com.google.common.io.Files;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link GoogleFirebaseCloudMessagingNotificationSenderTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    CasCoreNotificationsAutoConfiguration.class,
    CasGoogleFirebaseCloudMessagingAutoConfiguration.class,
    CasCoreUtilAutoConfiguration.class
}, properties = {
    "cas.google-firebase-messaging.service-account-key.location=file:${java.io.tmpdir}/account-key.json",
    "cas.google-firebase-messaging.database-url=https://cassso-2531381995058.firebaseio.com",
    "cas.google-firebase-messaging.registration-token-attribute-name=registrationToken"
})
@Tag("Simple")
class GoogleFirebaseCloudMessagingNotificationSenderTests {

    @Autowired
    @Qualifier("firebaseCloudMessagingNotificationSender")
    private NotificationSender firebaseCloudMessagingNotificationSender;

    @Autowired
    @Qualifier("notificationSender")
    private NotificationSender notificationSender;

    @BeforeAll
    public static void beforeAll() throws Exception {
        val key = IOUtils.toString(new ClassPathResource("account-key.json").getInputStream(), StandardCharsets.UTF_8);
        try (val writer = Files.newWriter(
                new File(FileUtils.getTempDirectory(), "account-key.json"), StandardCharsets.UTF_8)) {
            IOUtils.write(key, writer);
            writer.flush();
        }
    }

    @Test
    void verifyOperation() throws Throwable {
        assertNotNull(firebaseCloudMessagingNotificationSender);
        assertNotNull(notificationSender);
        val id = UUID.randomUUID().toString();
        val principal = CoreAuthenticationTestUtils.getPrincipal(Map.of("registrationToken", List.of(id)));
        assertDoesNotThrow(() -> {
            notificationSender.notify(principal, Map.of("title", "Hello", "message", "World"));
        });
    }

}
