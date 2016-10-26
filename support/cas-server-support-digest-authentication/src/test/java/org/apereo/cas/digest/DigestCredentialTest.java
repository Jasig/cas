package org.apereo.cas.digest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apereo.cas.authentication.CredentialMetaData;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Misagh Moayyed
 * @since 4.1
 */
public class DigestCredentialTest {

    private static final File JSON_FILE = new File("digestCredential.json");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeADigestCredentialToJson() throws IOException {
        final DigestCredential credentialMetaDataWritten = new DigestCredential("uid", "realm", "hash");

        MAPPER.writeValue(JSON_FILE, credentialMetaDataWritten);

        final CredentialMetaData credentialMetaDataRead = MAPPER.readValue(JSON_FILE, DigestCredential.class);

        assertEquals(credentialMetaDataWritten, credentialMetaDataRead);
    }
}
