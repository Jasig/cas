package org.apereo.cas;

import org.apereo.cas.util.spring.boot.AbstractCasBanner;

import lombok.extern.slf4j.Slf4j;

/**
 * This is {@link CasSpringBootAdminServerBanner}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
public class CasSpringBootAdminServerBanner extends AbstractCasBanner {
    @Override
    protected String getTitle() {
        return "CAS Admin Server";
    }
}
