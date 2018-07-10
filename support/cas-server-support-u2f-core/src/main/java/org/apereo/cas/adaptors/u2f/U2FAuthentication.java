package org.apereo.cas.adaptors.u2f;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.Serializable;
import lombok.Getter;

/**
 * This is {@link U2FAuthentication}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class U2FAuthentication implements Serializable {

    private static final long serialVersionUID = 334984331545697641L;

    private final String challenge;

    private final String appId;

    private final String keyHandle;

    public String getVersion() {
        return "U2F_V2";
    }
}
