package org.apereo.cas.configuration.model.support.stormpath;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This is {@link StormpathProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@ConfigurationProperties(prefix = "cas.authn.stormpath", ignoreUnknownFields = false)
public class StormpathProperties {

    private String apiKey;
    private String applicationId;
    private String secretkey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(final String applicationId) {
        this.applicationId = applicationId;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(final String secretkey) {
        this.secretkey = secretkey;
    }
}
