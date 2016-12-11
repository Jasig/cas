package org.apereo.cas.authentication.principal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.CasProtocolConstants;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Scott Battaglia
 * @author Arnaud Lesueur
 * @since 3.1
 */
public class SimpleWebApplicationServiceImplTests {
    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "simpleWebApplicationServiceImpl.json");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void verifySerializeACompletePrincipalToJson() throws IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CasProtocolConstants.PARAMETER_SERVICE, "service");
        final WebApplicationService serviceWritten = new WebApplicationServiceFactory().createService(request);
        MAPPER.writeValue(JSON_FILE, serviceWritten);
        final SimpleWebApplicationServiceImpl serviceRead = MAPPER.readValue(JSON_FILE, SimpleWebApplicationServiceImpl.class);
        assertEquals(serviceWritten, serviceRead);
    }

    @Test
    public void verifyResponse() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CasProtocolConstants.PARAMETER_SERVICE, "service");
        final WebApplicationService impl = new WebApplicationServiceFactory().createService(request);
        final Response response = new WebApplicationServiceResponseBuilder().build(impl, "ticketId");
        assertNotNull(response);
        assertEquals(Response.ResponseType.REDIRECT, response.getResponseType());
    }

    @Test
    public void verifyCreateSimpleWebApplicationServiceImplFromServiceAttribute() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(CasProtocolConstants.PARAMETER_SERVICE, "service");
        final WebApplicationService impl = new WebApplicationServiceFactory().createService(request);
        assertNotNull(impl);
    }

    @Test
    public void verifyResponseForJsession() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CasProtocolConstants.PARAMETER_SERVICE, "http://www.cnn.com/;jsession=test");
        final WebApplicationService impl = new WebApplicationServiceFactory().createService(request);

        assertEquals("http://www.cnn.com/", impl.getId());
    }

    @Test
    public void verifyResponseWithNoTicket() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CasProtocolConstants.PARAMETER_SERVICE, "service");
        final WebApplicationService impl = new WebApplicationServiceFactory().createService(request);

        final Response response = new WebApplicationServiceResponseBuilder().build(impl, null);
        assertNotNull(response);
        assertEquals(Response.ResponseType.REDIRECT, response.getResponseType());
        assertFalse(response.getUrl().contains("ticket="));
    }

    @Test
    public void verifyResponseWithNoTicketAndNoParameterInServiceURL() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("service", "http://foo.com/");
        final WebApplicationService impl = new WebApplicationServiceFactory().createService(request);
        final Response response = new WebApplicationServiceResponseBuilder().build(impl, null);
        assertNotNull(response);
        assertEquals(Response.ResponseType.REDIRECT, response.getResponseType());
        assertFalse(response.getUrl().contains("ticket="));
        assertEquals("http://foo.com/", response.getUrl());
    }

    @Test
    public void verifyResponseWithNoTicketAndOneParameterInServiceURL() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter(CasProtocolConstants.PARAMETER_SERVICE, "http://foo.com/?param=test");
        final WebApplicationService impl = new WebApplicationServiceFactory().createService(request);
        final Response response = new WebApplicationServiceResponseBuilder().build(impl, null);
        assertNotNull(response);
        assertEquals(Response.ResponseType.REDIRECT, response.getResponseType());
        assertEquals("http://foo.com/?param=test", response.getUrl());
    }
}
