package org.apereo.cas;

import org.apereo.cas.web.support.filters.AddResponseHeadersFilterTests;
import org.apereo.cas.web.support.filters.RequestParameterPolicyEnforcementFilterTests;
import org.apereo.cas.web.support.filters.ResponseHeadersEnforcementFilterTests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0-RC3
 */
@SelectClasses({
    RequestParameterPolicyEnforcementFilterTests.class,
    ResponseHeadersEnforcementFilterTests.class,
    AddResponseHeadersFilterTests.class
})
@Suite
public class AllTestsSuite {
}
