package org.apereo.cas.support.saml.authentication;

import org.apereo.cas.support.saml.config.SamlGoogleAppsConfiguration;
import org.apereo.cas.support.saml.util.AbstractSaml20ObjectBuilder;
import org.apereo.cas.util.CompressionUtils;
import org.apereo.cas.support.saml.AbstractOpenSamlTests;
import org.apereo.cas.support.saml.util.GoogleSaml20ObjectBuilder;
import org.apereo.cas.util.ApplicationContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * This is {@link GoogleAppsSamlAuthenticationRequestTests}.
 *
 * @author Misagh Moayyed
 * @since 4.2.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = {SamlGoogleAppsConfiguration.class},
        initializers = ConfigFileApplicationContextInitializer.class)
public class GoogleAppsSamlAuthenticationRequestTests extends AbstractOpenSamlTests {

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    @Before
    public void init() {
        this.applicationContextProvider.setApplicationContext(this.applicationContext);
    }

    @Test
    public void ensureInflation() throws Exception {
        final String deflator = CompressionUtils.deflate(SAML_REQUEST);
        final AbstractSaml20ObjectBuilder builder = new GoogleSaml20ObjectBuilder();
        final String msg = builder.decodeSamlAuthnRequest(deflator);
        assertEquals(msg, SAML_REQUEST);
    }

}
