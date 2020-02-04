package org.apereo.cas.web.flow;

import org.apereo.cas.web.support.WebUtils;
import org.apereo.cas.api.UserGraphicalAuthenticationRepository;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link DisplayUserGraphicsBeforeAuthenticationActionTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Webflow")
public class DisplayUserGraphicsBeforeAuthenticationActionTests extends AbstractGraphicalAuthenticationActionTests {
    @Autowired
    @Qualifier("displayUserGraphicsBeforeAuthenticationAction")
    protected Action displayUserGraphicsBeforeAuthenticationAction;

    @Test
    public void verifyAction() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        request.addParameter("username", "casuser");
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val event = displayUserGraphicsBeforeAuthenticationAction.execute(context);
        assertEquals(CasWebflowConstants.TRANSITION_ID_SUCCESS, event.getId());
        assertTrue(WebUtils.containsGraphicalUserAuthenticationImage(context));
    }
}
