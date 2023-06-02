package org.apereo.cas.web.flow;

import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link CasFlowHandlerMappingTests}.
 *
 * @author Milan Siebenburger
 * @since 7.0.0
 */
@Tag("WebflowConfig")
public class CasFlowHandlerMappingTests extends BaseWebflowConfigurerTests {

    @Autowired
    @Qualifier("loginFlowHandlerMapping")
    protected HandlerMapping casFlowHandlerMapping;

    @Test
    public void verifyCasFlowHandlerMappingInitialization() throws Exception {
        assertFalse(casWebflowExecutionPlan.getWebflowConfigurers().isEmpty());
        val interceptors = casWebflowExecutionPlan.getWebflowInterceptors();
        assertEquals(2, interceptors.size());

        val request = new MockHttpServletRequest();
        request.setPathInfo("/login");
        val handlerExecutionChain = casFlowHandlerMapping.getHandler(request);
        assertNotNull(handlerExecutionChain);

        val adaptedInterceptors = handlerExecutionChain.getInterceptorList();
        assertEquals(interceptors.size(), adaptedInterceptors.size());
    }
}
