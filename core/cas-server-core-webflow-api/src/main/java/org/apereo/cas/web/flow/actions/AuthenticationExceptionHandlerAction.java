package org.apereo.cas.web.flow.actions;

import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.configuration.model.core.web.MessageBundleProperties;
import org.apereo.cas.services.UnauthorizedServiceForPrincipalException;
import org.apereo.cas.ticket.AbstractTicketException;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.support.WebUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Performs two important error handling functions on an
 * {@link org.apereo.cas.authentication.AuthenticationException} raised from the authentication
 * layer:
 * <ol>
 * <li>Maps handler errors onto message bundle strings for display to user.</li>
 * <li>Determines the next webflow state by comparing handler errors against {@link #errors}
 * in list order. The first entry that matches determines the outcome state, which
 * is the simple class name of the exception.</li>
 * </ol>
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationExceptionHandlerAction extends AbstractAction {

    private static final String UNKNOWN = "UNKNOWN";

    /**
     * Ordered list of error classes that this class knows how to handle.
     */
    private final Set<Class<? extends Throwable>> errors;

    /**
     * String appended to exception class name to create a message bundle key for that particular error.
     */
    private final String messageBundlePrefix;

    public AuthenticationExceptionHandlerAction() {
        this(MessageBundleProperties.DEFAULT_BUNDLE_PREFIX_AUTHN_FAILURE);
    }

    public AuthenticationExceptionHandlerAction(final String messageBundlePrefix) {
        this(new LinkedHashSet<>(), messageBundlePrefix);
    }


    public Set<Class<? extends Throwable>> getErrors() {
        return new LinkedHashSet<>(this.errors);
    }

    /**
     * Maps an authentication exception onto a state name.
     * Also sets an ERROR severity message in the message context.
     *
     * @param e              Authentication error to handle.
     * @param requestContext the spring  context
     * @return Name of next flow state to transition to or {@value #UNKNOWN}
     */
    public String handle(final Exception e, final RequestContext requestContext) {
        val messageContext = requestContext.getMessageContext();

        if (e instanceof AuthenticationException) {
            return handleAuthenticationException((AuthenticationException) e, requestContext);
        }

        if (e instanceof AbstractTicketException) {
            return handleAbstractTicketException((AbstractTicketException) e, requestContext);
        }

        LOGGER.trace("Unable to translate errors of the authentication exception [{}]. Returning [{}]", e, UNKNOWN);
        val messageCode = this.messageBundlePrefix + UNKNOWN;
        messageContext.addMessage(new MessageBuilder().error().code(messageCode).build());
        return UNKNOWN;
    }

    /**
     * Maps an authentication exception onto a state name equal to the simple class name of the handler errors.
     * with highest precedence. Also sets an ERROR severity message in the
     * message context of the form {@code [messageBundlePrefix][exceptionClassSimpleName]}
     * for for the first handler
     * error that is configured. If no match is found, {@value #UNKNOWN} is returned.
     *
     * @param e              Authentication error to handle.
     * @param requestContext the spring context
     * @return Name of next flow state to transition to or {@value #UNKNOWN}
     */
    protected String handleAuthenticationException(final AuthenticationException e, final RequestContext requestContext) {
        if (e.getHandlerErrors().containsKey(UnauthorizedServiceForPrincipalException.class.getSimpleName())) {
            val url = WebUtils.getUnauthorizedRedirectUrlFromFlowScope(requestContext);
            if (url != null) {
                LOGGER.warn("Unauthorized service access for principal; CAS will be redirecting to [{}]", url);
                return CasWebflowConstants.STATE_ID_SERVICE_UNAUTHZ_CHECK;
            }
        }
        val values = e.getHandlerErrors().values().stream().map(Throwable::getClass).collect(Collectors.toList());
        val handlerErrorName = this.errors
            .stream()
            .filter(values::contains)
            .map(Class::getSimpleName)
            .findFirst()
            .orElseGet(() -> {
                LOGGER.debug("Unable to translate handler errors of the authentication exception [{}]. Returning [{}]", e, UNKNOWN);
                return UNKNOWN;
            });

        val messageContext = requestContext.getMessageContext();
        val messageCode = this.messageBundlePrefix + handlerErrorName;
        val messageBuilder = new MessageBuilder().error().code(messageCode);
        if (!handlerErrorName.equals(UNKNOWN) && !CollectionUtils.isEmpty(values)) {
            val clazz = values.stream().filter(n -> handlerErrorName.equals(n.getSimpleName())).findFirst().orElse(null);
            val throwable = e.getHandlerErrors().values().stream().filter(n -> clazz.isInstance(n)).findFirst().orElse(null);
            if (throwable instanceof PlaceholderCapableException) {
                val placeholderCapableException = (PlaceholderCapableException) throwable;
                messageBuilder.args(placeholderCapableException.getArgs());
            }
        }
        messageContext.addMessage(messageBuilder.build());
        return handlerErrorName;
    }

    /**
     * Maps an {@link AbstractTicketException} onto a state name equal to the simple class name of the exception with
     * highest precedence. Also sets an ERROR severity message in the message context with the error code found in
     * {@link AbstractTicketException#getCode()}. If no match is found,
     * {@value #UNKNOWN} is returned.
     *
     * @param e              Ticket exception to handle.
     * @param requestContext the spring context
     * @return Name of next flow state to transition to or {@value #UNKNOWN}
     */
    protected String handleAbstractTicketException(final AbstractTicketException e, final RequestContext requestContext) {
        val messageContext = requestContext.getMessageContext();
        val match = this.errors.stream()
            .filter(c -> c.isInstance(e)).map(Class::getSimpleName)
            .findFirst();
        if (match.isPresent()) {
            val messageBuilder = new MessageBuilder().error().code(e.getCode());
            if (e instanceof PlaceholderCapableException) {
                val placeholderCapableException = (PlaceholderCapableException) e;
                messageBuilder.args(placeholderCapableException.getArgs());
            }
            messageContext.addMessage(messageBuilder.build());
        }
        return match.orElse(UNKNOWN);
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        val currentEvent = requestContext.getCurrentEvent();
        LOGGER.debug("Located current event [{}]", currentEvent);

        val error = currentEvent.getAttributes().get(CasWebflowConstants.TRANSITION_ID_ERROR, Exception.class);
        if (error != null) {
            LOGGER.debug("Located error attribute [{}] with message [{}] from the current event", error.getClass(), error.getMessage());

            val event = handle(error, requestContext);
            LOGGER.debug("Final event id resolved from the error is [{}]", event);
            return new EventFactorySupport().event(this, event, currentEvent.getAttributes());
        }
        return new EventFactorySupport().event(this, "error");
    }
}
