package org.apereo.cas.services;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.util.serialization.JacksonObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link StaticRegisteredServiceUsernameProviderTests}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
@Tag("RegisteredService")
public class StaticRegisteredServiceUsernameProviderTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(),
        "StaticRegisteredServiceUsernameProviderTests.json");

    private static final ObjectMapper MAPPER = JacksonObjectMapperFactory.builder()
        .defaultTypingEnabled(true).build().toObjectMapper();

    @Test
    public void verifyOperation() throws Exception {
        System.setProperty("CAS_UID", "casuser");
        val usernameContext = RegisteredServiceUsernameProviderContext.builder()
            .registeredService(CoreAuthenticationTestUtils.getRegisteredService())
            .service(CoreAuthenticationTestUtils.getService())
            .principal(CoreAuthenticationTestUtils.getPrincipal("casuser"))
            .build();
        val provider = new StaticRegisteredServiceUsernameProvider();
        provider.setValue("${#systemProperties['CAS_UID']}");
        assertEquals("casuser", provider.resolveUsername(usernameContext));

        MAPPER.writeValue(JSON_FILE, provider);
        val policyRead = MAPPER.readValue(JSON_FILE, StaticRegisteredServiceUsernameProvider.class);
        assertEquals(provider, policyRead);
    }
}
