package org.apereo.cas;

import org.apereo.cas.support.pac4j.DelegatedClientJacksonModuleTests;
import org.apereo.cas.support.pac4j.authentication.ClientAuthenticationMetaDataPopulatorTests;
import org.apereo.cas.support.pac4j.authentication.attributes.GroovyAttributeConverterTests;
import org.apereo.cas.support.pac4j.authentication.clients.DelegatedClientFactoryTests;
import org.apereo.cas.support.pac4j.authentication.handler.support.DelegatedClientAuthenticationHandlerTests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@SelectClasses({
    GroovyAttributeConverterTests.class,
    DelegatedClientAuthenticationHandlerTests.class,
    DelegatedClientJacksonModuleTests.class,
    ClientAuthenticationMetaDataPopulatorTests.class,
    DelegatedClientFactoryTests.class
})
@Suite
public class AllTestsSuite {
}
