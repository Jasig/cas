package org.apereo.cas;

import org.apereo.cas.acct.AccountRegistrationRequestAuditPrincipalIdResolverTests;
import org.apereo.cas.acct.AccountRegistrationRequestTests;
import org.apereo.cas.acct.DefaultAccountRegistrationPropertyLoaderTests;
import org.apereo.cas.acct.DefaultAccountRegistrationServiceTests;
import org.apereo.cas.acct.webflow.AccountManagementRegistrationCaptchaWebflowConfigurerTests;
import org.apereo.cas.acct.webflow.AccountManagementWebflowConfigurerTests;
import org.apereo.cas.acct.webflow.LoadAccountRegistrationPropertiesActionTests;
import org.apereo.cas.acct.webflow.SubmitAccountRegistrationActionTests;
import org.apereo.cas.acct.webflow.ValidateAccountRegistrationTokenActionTests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 4.2.0
 */
@SelectClasses({
    ValidateAccountRegistrationTokenActionTests.class,
    AccountRegistrationRequestTests.class,
    DefaultAccountRegistrationServiceTests.class,
    LoadAccountRegistrationPropertiesActionTests.class,
    DefaultAccountRegistrationPropertyLoaderTests.class,
    SubmitAccountRegistrationActionTests.class,
    AccountRegistrationRequestAuditPrincipalIdResolverTests.class,
    AccountManagementRegistrationCaptchaWebflowConfigurerTests.class,
    AccountManagementWebflowConfigurerTests.class
})
@Suite
public class AllTestsSuite {
}
