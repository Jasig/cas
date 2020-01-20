package org.apereo.cas.web.flow;

import org.apereo.cas.configuration.CasConfigurationProperties;

import lombok.val;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * This is {@link PasswordlessAuthenticationWebflowConfigurer}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class PasswordlessAuthenticationWebflowConfigurer extends MultiphaseAuthenticationWebflowConfigurer {
    /**
     * Transition to obtain username.
     */
    //static final String TRANSITION_ID_PASSWORDLESS_GET_USERID = "passwordlessGetUserId";
    
    static final String STATE_ID_PASSWORDLESS_DISPLAY = "passwordlessDisplayUser";
    static final String STATE_ID_PASSWORDLESS_VERIFY_ACCOUNT = "passwordlessVerifyAccount";
    static final String STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION = "acceptPasswordlessAuthentication";
    //static final String STATE_ID_PASSWORDLESS_GET_USERID = "passwordlessGetUserIdView";
    
    public PasswordlessAuthenticationWebflowConfigurer(final FlowBuilderServices flowBuilderServices,
                                                       final FlowDefinitionRegistry loginFlowDefinitionRegistry,
                                                       final ApplicationContext applicationContext,
                                                       final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    protected void doInitialize() {
        super.doInitialize();
        val flow = getLoginFlow();
        if (flow != null) {
            val state = getState(flow, CasWebflowConstants.STATE_ID_MULTIPHASE_STORE_USERID, ActionState.class);
            /*
            createTransitionForState(state, TRANSITION_ID_PASSWORDLESS_GET_USERID, STATE_ID_PASSWORDLESS_GET_USERID);

            val viewState = createViewState(flow, STATE_ID_PASSWORDLESS_GET_USERID, "casPasswordlessGetUserIdView");
            createTransitionForState(viewState, CasWebflowConstants.TRANSITION_ID_SUBMIT, STATE_ID_PASSWORDLESS_VERIFY_ACCOUNT);
            */
            createTransitionForState(state, CasWebflowConstants.TRANSITION_ID_SUCCESS,
                    STATE_ID_PASSWORDLESS_VERIFY_ACCOUNT);

            val verifyAccountState = createActionState(flow, STATE_ID_PASSWORDLESS_VERIFY_ACCOUNT, "verifyPasswordlessAccountAuthenticationAction");
            createTransitionForState(verifyAccountState, CasWebflowConstants.TRANSITION_ID_ERROR, 
                    CasWebflowConstants.VIEW_ID_MULTIPHASE_GET_USERID);
            createTransitionForState(verifyAccountState, CasWebflowConstants.TRANSITION_ID_SUCCESS, STATE_ID_PASSWORDLESS_DISPLAY);

            val viewStateDisplay = createViewState(flow, STATE_ID_PASSWORDLESS_DISPLAY, "casPasswordlessDisplayView");
            viewStateDisplay.getEntryActionList().add(createEvaluateAction("displayBeforePasswordlessAuthenticationAction"));
            createTransitionForState(viewStateDisplay, CasWebflowConstants.TRANSITION_ID_SUBMIT, STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION);

            val acceptAction = createEvaluateAction("acceptPasswordlessAuthenticationAction");
            val acceptState = createActionState(flow, STATE_ID_ACCEPT_PASSWORDLESS_AUTHENTICATION, acceptAction);
            createTransitionForState(acceptState, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, STATE_ID_PASSWORDLESS_DISPLAY);

            val submission = getState(flow, CasWebflowConstants.STATE_ID_REAL_SUBMIT, ActionState.class);
            val transition = (Transition) submission.getTransition(CasWebflowConstants.TRANSITION_ID_SUCCESS);
            val targetStateId = transition.getTargetStateId();
            createTransitionForState(acceptState, CasWebflowConstants.TRANSITION_ID_SUCCESS, targetStateId);

        }
    }
}
