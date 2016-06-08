package org.apereo.cas.configuration.model.core.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This is {@link MetricsProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@ConfigurationProperties(prefix = "metrics", ignoreUnknownFields = false)
public class MetricsProperties {

    private long refreshInterval = 30;
    private String loggerName = "perfStatsLogger";

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(final long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(final String loggerName) {
        this.loggerName = loggerName;
    }
}
