package org.apereo.cas.logging;

import org.apereo.cas.util.AsciiArtUtils;
import org.apereo.cas.util.function.FunctionUtils;
import org.apereo.cas.util.serialization.JacksonObjectMapperFactory;
import org.apereo.cas.util.spring.ApplicationContextProvider;
import org.apereo.cas.util.text.MessageSanitizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.logging.StackdriverTraceConstants;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.JsonLayout;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * This is {@link GoogleCloudAppender}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
@Plugin(name = "GoogleCloudAppender", category = "Core", elementType = "appender", printObject = true)
public class GoogleCloudAppender extends AbstractAppender {
    private static final ObjectMapper MAPPER = JacksonObjectMapperFactory.builder()
        .defaultTypingEnabled(false).build().toObjectMapper();

    private static final int TRACE_ID_64_BIT_LENGTH = 16;

    private final Configuration configuration;

    private final AppenderRef appenderRef;

    /**
     * If no Project ID set, then attempts to resolve it with the default project ID provider.
     */
    private final String projectId;

    public GoogleCloudAppender(final String name, final Configuration config,
                               final AppenderRef appenderRef,
                               final Filter filter, final String projectId) {
        super(name, filter, JsonLayout.createDefaultLayout(), false, Property.EMPTY_ARRAY);
        this.configuration = config;
        this.appenderRef = appenderRef;
        this.projectId = FunctionUtils.doIfNull(projectId, () -> {
            val projectIdProvider = new DefaultGcpProjectIdProvider();
            return projectIdProvider.getProjectId();
        }, () -> projectId).get();
    }

    /**
     * Build google cloud appender.
     *
     * @param name        the name
     * @param appenderRef the appender ref
     * @param filter      the filter
     * @param config      the config
     * @return the google cloud appender
     */
    @PluginFactory
    public static GoogleCloudAppender build(
        @PluginAttribute("name")
        final String name,
        @PluginAttribute("projectId")
        final String projectId,
        @PluginElement("AppenderRef")
        final AppenderRef appenderRef,
        @PluginElement("Filter")
        final Filter filter,
        @PluginConfiguration
        final Configuration config) {
        return new GoogleCloudAppender(name, config,
            appenderRef, filter, projectId);
    }

    @Override
    public void append(final LogEvent logEvent) {
        val contextData = new SortedArrayStringMap(logEvent.getContextData());
        contextData.putValue("sourceLocation", Optional.ofNullable(logEvent.getSource())
            .map(Object::toString).orElse(StringUtils.EMPTY));
        contextData.putValue("severity", logEvent.getLevel().name());
        collectTraceId(contextData);
        collectHttpRequest(contextData);
        collectTimestamps(logEvent, contextData);
        appendLogEvent(logEvent, contextData);
    }

    protected void collectHttpRequest(final StringMap contextData) {
        val requestUrl = StringUtils.defaultString(contextData.getValue("requestUrl"));
        if (StringUtils.isNotBlank(requestUrl)) {
            val httpRequest = Map.of(
                "requestMethod", StringUtils.defaultString(contextData.getValue("method")),
                "requestUrl", requestUrl,
                "protocol", StringUtils.defaultString(contextData.getValue("protocol")),
                "userAgent", StringUtils.defaultString(contextData.getValue("user-agent")),
                "remoteIp", StringUtils.defaultString(contextData.getValue("remoteAddress")));
            contextData.putValue("httpRequest", FunctionUtils.doUnchecked(() -> MAPPER.writeValueAsString(httpRequest)));
        }
    }

    protected void appendLogEvent(final LogEvent logEvent, final StringMap contextData) {
        val appender = configuration.getAppender(appenderRef.getRef());
        appender.append(finalizeLogEvent(logEvent, contextData));
    }

    protected LogEvent finalizeLogEvent(final LogEvent logEvent, final StringMap contextData) {
        val messagePayload = new LinkedHashMap<>();
        val message = buildLogMessage(logEvent, messagePayload);
        return LoggingUtils.getLogEventBuilder(logEvent)
            .setContextData(contextData)
            .setMessage(message)
            .build();
    }

    protected void collectTimestamps(final LogEvent logEvent, final StringMap contextData) {
        contextData.putValue(
            StackdriverTraceConstants.TIMESTAMP_SECONDS_ATTRIBUTE,
            String.valueOf(TimeUnit.MILLISECONDS.toSeconds(logEvent.getTimeMillis())));
        contextData.putValue(
            StackdriverTraceConstants.TIMESTAMP_NANOS_ATTRIBUTE,
            String.valueOf(TimeUnit.MILLISECONDS.toNanos(logEvent.getTimeMillis())));
    }

    protected void collectTraceId(final StringMap contextData) {
        val traceId = (String) contextData.getValue(StackdriverTraceConstants.MDC_FIELD_TRACE_ID);
        if (StringUtils.isNotBlank(traceId) && StringUtils.isNotBlank(this.projectId)) {
            contextData.putValue(StackdriverTraceConstants.MDC_FIELD_TRACE_ID,
                StackdriverTraceConstants.composeFullTraceName(this.projectId, formatTraceId(traceId)));
        }
    }

    protected ObjectMessage buildLogMessage(final LogEvent logEvent,
                                            final Map<Object, Object> messagePayload) {
        val buildMessage = logEvent.getMarker() == null
                           || !logEvent.getMarker().getName().equals(AsciiArtUtils.ASCII_ART_LOGGER_MARKER.getName());
        if (buildMessage) {
            val messagSanitizer = ApplicationContextProvider.getMessagSanitizer().orElseGet(MessageSanitizer::disabled);
            messagePayload.put("text", messagSanitizer.sanitize(logEvent.getMessage().getFormattedMessage()));
            if (logEvent.getMessage() instanceof ObjectMessage objectMessage) {
                if (objectMessage.getParameter() instanceof Map parameters) {
                    collectMessageParameters(messagePayload, messagSanitizer, parameters);
                } else {
                    try {
                        val parameters = MAPPER.convertValue(objectMessage.getParameter(), Map.class);
                        collectMessageParameters(messagePayload, messagSanitizer, parameters);
                    } catch (final Exception e) {
                        val parameters = Map.of("payload", objectMessage.getParameter().toString());
                        collectMessageParameters(messagePayload, messagSanitizer, parameters);
                    }
                }
            }
        }
        return new ObjectMessage(messagePayload);
    }

    private static void collectMessageParameters(final Map<Object, Object> messagePayload,
                                                 final MessageSanitizer messagSanitizer,
                                                 final Map parameters) {
        parameters.forEach((key, value) -> messagePayload.put(key, messagSanitizer.sanitize(value.toString())));
    }

    /**
     * Format trace id.
     * Trace IDs are either 64-bit or 128-bit, which is 16-digit hex, or 32-digit hex.
     * If traceId is 64-bit (16-digit hex), then we need to prepend 0's to make a 32-digit hex.
     *
     * @param traceId the trace id
     * @return the string
     */
    protected String formatTraceId(final String traceId) {
        if (traceId != null && traceId.length() == TRACE_ID_64_BIT_LENGTH) {
            return "0000000000000000" + traceId;
        }
        return traceId;
    }
}
