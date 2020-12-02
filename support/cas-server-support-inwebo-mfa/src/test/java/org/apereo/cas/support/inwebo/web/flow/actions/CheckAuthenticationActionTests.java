package org.apereo.cas.support.inwebo.web.flow.actions;

import org.apereo.cas.support.inwebo.service.response.Result;
import org.apereo.cas.web.flow.CasWebflowConstants;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests {@link CheckAuthenticationAction}.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Tag("WebflowMfaActions")
public class CheckAuthenticationActionTests extends BaseActionTests {

    private static final String OTP = "4q5dslf";

    private CheckAuthenticationAction action;

    @BeforeEach
    public void setUp() {
        super.setUp();

        action = new CheckAuthenticationAction(mock(MessageSource.class), service, resolver);
    }

    @Test
    public void verifyGoodOtp() {
        request.addParameter("otp", OTP);
        when(service.authenticateExtended(LOGIN, OTP)).thenReturn(deviceResponse(Result.NOK.OK));

        val event = action.doExecute(requestContext);
        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, event.getId());
        assertMfa();
    }

    @Test
    public void verifyBadOtp() {
        request.addParameter("otp", OTP);
        when(service.authenticateExtended(LOGIN, OTP)).thenReturn(deviceResponse(Result.NOK));

        val event = action.doExecute(requestContext);
        assertEquals(CasWebflowConstants.TRANSITION_ID_ERROR, event.getId());
        assertNoMfa();
    }

    @Test
    public void verifyPushValidated() {
        requestContext.getFlowScope().put(WebflowConstants.INWEBO_SESSION_ID, SESSION_ID);
        when(service.checkPushResult(LOGIN, SESSION_ID)).thenReturn(deviceResponse(Result.OK));

        val event = action.doExecute(requestContext);
        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, event.getId());
        assertMfa();
    }

    @Test
    public void verifyPushNotValidatedYet() {
        requestContext.getFlowScope().put(WebflowConstants.INWEBO_SESSION_ID, SESSION_ID);
        when(service.checkPushResult(LOGIN, SESSION_ID)).thenReturn(deviceResponse(Result.WAITING));

        val event = action.doExecute(requestContext);
        assertEquals(WebflowConstants.PENDING, event.getId());
        assertNoMfa();
    }

    @Test
    public void verifyPushRefusedOrTimeout() {
        requestContext.getFlowScope().put(WebflowConstants.INWEBO_SESSION_ID, SESSION_ID);
        when(service.checkPushResult(LOGIN, SESSION_ID)).thenReturn(deviceResponse(Result.REFUSED));

        val event = action.doExecute(requestContext);
        assertEquals(CasWebflowConstants.TRANSITION_ID_ERROR, event.getId());
        assertTrue(requestContext.getFlowScope().contains(WebflowConstants.INWEBO_ERROR_MESSAGE));
        assertNoMfa();
    }

    @Test
    public void verifyPushError() {
        requestContext.getFlowScope().put(WebflowConstants.INWEBO_SESSION_ID, SESSION_ID);
        when(service.checkPushResult(LOGIN, SESSION_ID)).thenReturn(deviceResponse(Result.NOK));

        val event = action.doExecute(requestContext);
        assertEquals(CasWebflowConstants.TRANSITION_ID_ERROR, event.getId());
        assertFalse(requestContext.getFlowScope().contains(WebflowConstants.INWEBO_ERROR_MESSAGE));
        assertNoMfa();
    }
}
