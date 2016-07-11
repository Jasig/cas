package org.apereo.cas.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * No-Op cipher executor that does nothing for encryption/decryption.
 * @author Misagh Moayyed
 * @since 4.1
 */
public class NoOpCipherExecutor extends AbstractCipherExecutor<String, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpCipherExecutor.class);
    
    @Override
    public String encode(final String value) {
        return value;
    }

    @Override
    public String decode(final String value) {
        return value;
    }
}
