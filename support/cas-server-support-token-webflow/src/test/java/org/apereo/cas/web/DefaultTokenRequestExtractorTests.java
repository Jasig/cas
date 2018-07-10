package org.apereo.cas.web;

import lombok.val;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.token.TokenConstants;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

/**
 * This is {@link DefaultTokenRequestExtractorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@Slf4j
public class DefaultTokenRequestExtractorTests {

    @Test
    public void verifyTokenFromParameter() {
        val request = new MockHttpServletRequest();
        request.addParameter(TokenConstants.PARAMETER_NAME_TOKEN, "test");
        val e = new DefaultTokenRequestExtractor();
        val token = e.extract(request);
        assertEquals("test", token);
    }

    @Test
    public void verifyTokenFromHeader() {
        val request = new MockHttpServletRequest();
        request.addHeader(TokenConstants.PARAMETER_NAME_TOKEN, "test");
        val e = new DefaultTokenRequestExtractor();
        val token = e.extract(request);
        assertEquals("test", token);
    }

    @Test
    public void verifyTokenNotFound() {
        val request = new MockHttpServletRequest();
        val e = new DefaultTokenRequestExtractor();
        val token = e.extract(request);
        assertNull(token);
    }
}
