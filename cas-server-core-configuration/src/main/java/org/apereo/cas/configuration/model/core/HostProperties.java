package org.apereo.cas.configuration.model.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties class for host.
 *
 * @author Dmitriy Kopylenko
 * @since 5.0.0
 */
@ConfigurationProperties(prefix = "host", ignoreUnknownFields = false)
public class HostProperties {

    private String name;
    
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
