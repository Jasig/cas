package org.apereo.cas.support.rest;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link TicketsResource}.
 *
 * @author Dmitriy Kopylenko
 * @since 4.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisteredServiceResourceTests {

    @Mock
    private CentralAuthenticationService casMock;

    @Mock
    private ServicesManager servicesManager;

    @InjectMocks
    private RegisteredServiceResource registeredServiceResource;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.registeredServiceResource)
                .defaultRequest(get("/")
                        .contextPath("/cas")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .build();
    }

    @Test
    public void checkRegisteredServiceNotAuthorized() throws Exception {
        configureCasMockToCreateValidTGT();


        registeredServiceResource.setAttributeName("memberOf");
        registeredServiceResource.setAttributeValue("staff");


        this.mockMvc.perform(post("/cas/v1/services/add/TGT-1")
                .param("serviceId", "serviceId")
                .param("name", "name")
                .param("description", "description")
                .param("evaluationOrder", "1000")
                .param("enabled", "false")
                .param("ssoEnabled", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkRegisteredServiceNormal() throws Exception {
        configureCasMockToCreateValidTGT();

        registeredServiceResource.setAttributeName("memberOf");
        registeredServiceResource.setAttributeValue("cas");


        this.mockMvc.perform(post("/cas/v1/services/add/TGT-1")
                .param("serviceId", "serviceId")
                .param("name", "name")
                .param("description", "description")
                .param("evaluationOrder", "1000")
                .param("enabled", "false")
                .param("ssoEnabled", "true"))
                .andExpect(status().isOk());
    }

    @Test
    public void checkRegisteredServiceNoTgt() throws Exception {


        registeredServiceResource.setAttributeName("memberOf");
        registeredServiceResource.setAttributeValue("staff");


        this.mockMvc.perform(post("/cas/v1/services/add/TGT-1")
                .param("serviceId", "serviceId")
                .param("name", "name")
                .param("description", "description")
                .param("evaluationOrder", "1000")
                .param("enabled", "false")
                .param("ssoEnabled", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void checkRegisteredServiceNoAttributeValue() throws Exception {

        registeredServiceResource.setAttributeName("Test");
        registeredServiceResource.setAttributeValue(StringUtils.EMPTY);

        this.mockMvc.perform(post("/cas/v1/services/add/TGT-12345"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkRegisteredServiceNoAttributeName() throws Exception {
        registeredServiceResource.setAttributeValue("Test");
        this.mockMvc.perform(post("/cas/v1/services/add/TGT-12345"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkRegisteredServiceNoAttributes() throws Exception {
        this.mockMvc.perform(post("/cas/v1/services/add/TGT-12345"))
                .andExpect(status().isBadRequest());
    }


    private void configureCasMockToCreateValidTGT() throws Exception {
        final TicketGrantingTicket tgt = mock(TicketGrantingTicket.class);
        when(tgt.getId()).thenReturn("TGT-1");
        when(tgt.getAuthentication()).thenReturn(CoreAuthenticationTestUtils.getAuthentication(
                CoreAuthenticationTestUtils.getPrincipal("casuser",
                        new HashMap<>(RegisteredServiceTestUtils.getTestAttributes()))));
        final Class<TicketGrantingTicket> clazz = TicketGrantingTicket.class;

        when(this.casMock.getTicket(anyString(), any(clazz.getClass()))).thenReturn(tgt);
        when(this.servicesManager.save(any(RegisteredService.class))).thenReturn(
                RegisteredServiceTestUtils.getRegisteredService("TEST"));
    }
}
