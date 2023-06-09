package org.apereo.cas.nativex;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link CasNativeInfoContributorTests}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
@Tag("Native")
public class CasNativeInfoContributorTests {
    @Test
    public void verifyOperation() {
        val contributor = new CasNativeInfoContributor();
        val builder = new Info.Builder();
        contributor.contribute(builder);
        val info = builder.build();
        assertTrue(info.getDetails().containsKey("nativeImage"));
    }
}
