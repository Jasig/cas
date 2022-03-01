package org.apereo.cas.aup;

import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.mock.MockTicketGrantingTicket;
import org.apereo.cas.util.CollectionUtils;
import org.apereo.cas.web.support.WebUtils;

import lombok.Getter;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.test.MockRequestContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link GroovyAcceptableUsagePolicyRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Getter
@Tag("Groovy")
@TestPropertySource(properties = "cas.acceptable-usage-policy.groovy.location=classpath:/AcceptableUsagePolicy.groovy")
public class GroovyAcceptableUsagePolicyRepositoryTests extends BaseAcceptableUsagePolicyRepositoryTests {

    @Autowired
    @Qualifier(AcceptableUsagePolicyRepository.BEAN_NAME)
    protected AcceptableUsagePolicyRepository acceptableUsagePolicyRepository;

    @Test
    public void verifyRepositoryActionWithAdvancedConfig() throws Exception {
        verifyRepositoryAction("casuser", CollectionUtils.wrap("aupAccepted", "false"));
    }

    @Test
    public void verifyPolicyTerms() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val credential = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser");
        val tgt = new MockTicketGrantingTicket(credential.getId(), credential, Map.of());
        ticketRegistry.addTicket(tgt);

        WebUtils.putAuthentication(tgt.getAuthentication(), context);
        WebUtils.putTicketGrantingTicketInScopes(context, tgt);
        assertTrue(acceptableUsagePolicyRepository.fetchPolicy(context).isPresent());
    }

    @Test
    public void verifyPolicyTermsFails() throws Exception {
        val context = new MockRequestContext();
        val request = new MockHttpServletRequest();
        context.setExternalContext(new ServletExternalContext(new MockServletContext(), request, new MockHttpServletResponse()));
        val credential = CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword("casuser");
        val tgt = new MockTicketGrantingTicket(credential.getId(), credential, Map.of());
        ticketRegistry.addTicket(tgt);
        assertFalse(acceptableUsagePolicyRepository.fetchPolicy(context).isPresent());
    }
}
