package org.apereo.cas.web.flow;

import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * The {@link RemoteAddressWebflowConfigurer} is responsible for
 * adjusting the CAS webflow context for remote address integration.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
public class RemoteAddressWebflowConfigurer extends AbstractCasWebflowConfigurer {

    public RemoteAddressWebflowConfigurer(final FlowBuilderServices flowBuilderServices, final FlowDefinitionRegistry loginFlowDefinitionRegistry) {
        super(flowBuilderServices);
        super.setLoginFlowDefinitionRegistry(loginFlowDefinitionRegistry);
    }

    @Override
    protected void doInitialize() throws Exception {
        final Flow flow = getLoginFlow();
        if (flow != null) {
            final ActionState actionState = createActionState(flow, "startAuthenticate", createEvaluateAction("remoteAddressCheck"));
            actionState.getTransitionSet().add(createTransition(CasWebflowConstants.TRANSITION_ID_SUCCESS,
                    CasWebflowConstants.TRANSITION_ID_SEND_TICKET_GRANTING_TICKET));
            actionState.getTransitionSet().add(createTransition(CasWebflowConstants.TRANSITION_ID_ERROR, getStartState(flow).getId()));
            actionState.getExitActionList().add(createEvaluateAction("clearWebflowCredentialsAction"));
            registerMultifactorProvidersStateTransitionsIntoWebflow(actionState);
        }
    }
}
