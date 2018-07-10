package org.apereo.cas.util;

import lombok.val;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Handles tests for {@link HostNameBasedUniqueTicketIdGenerator}.
 * @author Misagh Moayyed
 * @since 4.1.0
 */
@Slf4j
public class HostNameBasedUniqueTicketIdGeneratorTests {
    
    @Test
    public void verifyUniqueGenerationOfTicketIds() {
        val generator = new HostNameBasedUniqueTicketIdGenerator(10, StringUtils.EMPTY);
        val id1 = generator.getNewTicketId("TEST");
        val id2 = generator.getNewTicketId("TEST");
        assertNotSame(id1, id2);
    }
}
