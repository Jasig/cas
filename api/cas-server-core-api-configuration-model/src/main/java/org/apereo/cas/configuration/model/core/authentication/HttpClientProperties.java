package org.apereo.cas.configuration.model.core.authentication;

import org.apereo.cas.configuration.support.RequiresModule;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;

/**
 * Configuration properties class for http.client.truststore.
 *
 * @author Dmitriy Kopylenko
 * @since 5.0.0
 */
@RequiresModule(name = "cas-server-core-authentication", automated = true)
@Getter
@Setter
@Accessors(chain = true)
public class HttpClientProperties implements Serializable {

    private static final long serialVersionUID = -7494946569869245770L;

    /**
     * Connection timeout for all operations that reach out to URL endpoints.
     */
    private String connectionTimeout = "PT5S";

    /**
     * Read timeout for all operations that reach out to URL endpoints.
     */
    private String readTimeout = "PT5S";

    /**
     * Indicates timeout for async operations.
     */
    private String asyncTimeout = "PT5S";

    /**
     * Enable hostname verification when attempting to contact URL endpoints.
     * May also be set to {@code none} to disable verification.
     */
    private String hostNameVerifier = "default";

    /**
     * Configuration properties namespace for embedded Java SSL trust store.
     */
    @NestedConfigurationProperty
    private HttpClientTrustStoreProperties truststore = new HttpClientTrustStoreProperties();

    /**
     * Whether CAS should accept local logout URLs.
     * For example http(s)://localhost/logout
     */
    private boolean allowLocalLogoutUrls;

    /**
     * If specified the regular expression will be used to validate the url's authority.
     */
    private String authorityValidationRegEx;

    /**
     * Send requests via a proxy; define the hostname.
     */
    private String proxyHost;

    /**
     * Send requests via a proxy; define the proxy port.
     * Negative/zero values should deactivate the proxy configuration
     * for the http client.
     */
    private int proxyPort;

    /**
     * Whether the regular expression specified with {@code authorityValidationRegEx}
     * should be handled as case-sensitive
     * ({@code true}) or case-insensitive ({@code false}). If
     * no {@code authorityValidationRegEx} is set, this value does not have any effect.
     */
    private boolean authorityValidationRegExCaseSensitive = true;

}
