package org.apereo.cas;

import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * The {@link AllCoreTestsSuite} is responsible for
 * running all cas test cases.
 *
 * @author Misagh Moayyed
 * @since 4.2.0
 */
@RunWith(Enclosed.class)
@Suite.SuiteClasses({
    DefaultCentralAuthenticationServiceTests.class,
    DefaultCentralAuthenticationServiceMockitoTests.class,
    DefaultCasAttributeEncoderTests.class,
    AdaptiveMultifactorAuthenticationPolicyEventResolverTests.class,
    DefaultPrincipalAttributesRepositoryTests.class,
    MultifactorAuthenticationTests.class
})
public class AllCoreTestsSuite {
}
