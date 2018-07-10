package org.apereo.cas.services;

import lombok.val;

import static org.junit.Assert.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author Misagh Moayyed
 * @since 4.0.0
 */
@Slf4j
public class UnauthorizedServiceExceptionTests {

    private static final String MESSAGE = "GG";

    @Test
    public void verifyCodeConstructor() {
        val e = new UnauthorizedServiceException(MESSAGE);

        assertEquals(MESSAGE, e.getMessage());
    }

    @Test
    public void verifyThrowableConstructorWithCode() {
        val r = new RuntimeException();
        val e = new UnauthorizedServiceException(MESSAGE, r);

        assertEquals(MESSAGE, e.getMessage());
        assertEquals(r, e.getCause());
    }
}
