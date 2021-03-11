package org.apereo.cas.web.flow.configurer;

import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.CasWebflowExecutionPlan;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.State;
import org.springframework.webflow.engine.TransitionableState;
import org.springframework.webflow.engine.SubflowState;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link BaseMultifactorWebflowConfigurerTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Slf4j
public abstract class BaseMultifactorWebflowConfigurerTests {
    @Autowired
    @Qualifier("casWebflowExecutionPlan")
    protected CasWebflowExecutionPlan casWebflowExecutionPlan;

    @Autowired
    @Qualifier("loginFlowRegistry")
    protected FlowDefinitionRegistry loginFlowDefinitionRegistry;

    protected abstract FlowDefinitionRegistry getMultifactorFlowDefinitionRegistry();

    protected abstract String getMultifactorEventId();

    @Test
    public void ensureAllTransitions() {
        val registry = getMultifactorFlowDefinitionRegistry();
        assertTrue(registry.containsFlowDefinition(getMultifactorEventId()));
        val flow = (Flow) registry.getFlowDefinition(getMultifactorEventId());
        val states = Arrays.asList(flow.getStateIds());
        states.forEach(id -> {
            val state = (State) flow.getState(id);
            if (state instanceof TransitionableState) {
                TransitionableState.class.cast(state).getTransitionSet().forEach(t -> {
                    LOGGER.trace("Testing destination of transition {} from state {} to {}",
                            t.getId(), id, t.getTargetStateId());
                    assertTrue(flow.containsState(t.getTargetStateId()));
                });
            }
        });
    }

    @Test
    public void verifyOperation() {
        val registry = getMultifactorFlowDefinitionRegistry();
        assertTrue(registry.containsFlowDefinition(getMultifactorEventId()));
        val flow = (Flow) registry.getFlowDefinition(getMultifactorEventId());
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_MFA_CHECK_BYPASS));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_MFA_CHECK_AVAILABLE));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_MFA_FAILURE));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_SUCCESS));
        val loginFlow = (Flow) loginFlowDefinitionRegistry.getFlowDefinition(CasWebflowConfigurer.FLOW_ID_LOGIN);
        assertTrue(loginFlow.getState(getMultifactorEventId()) instanceof SubflowState);
    }

    @Test
    public void verifyTrustedDevice() {
        val registry = getMultifactorFlowDefinitionRegistry();
        assertTrue(registry.containsFlowDefinition(getMultifactorEventId()));
        val flow = (Flow) registry.getFlowDefinition(getMultifactorEventId());
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_REGISTER_TRUSTED_DEVICE));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_FINISH_MFA_TRUSTED_AUTH));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_PREPARE_REGISTER_TRUSTED_DEVICE));
        assertTrue(flow.containsState(CasWebflowConstants.STATE_ID_REGISTER_DEVICE_VIEW));
    }
}
