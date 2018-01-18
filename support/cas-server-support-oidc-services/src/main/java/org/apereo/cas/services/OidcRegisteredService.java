package org.apereo.cas.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.support.oauth.services.OAuthRegisteredService;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PostLoad;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * This is {@link OidcRegisteredService}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Entity
@DiscriminatorValue("oidc")
@Slf4j
@ToString(callSuper = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class OidcRegisteredService extends OAuthRegisteredService {

    private static final long serialVersionUID = 1310899699465091444L;

    @Column(length = 255, updatable = true, insertable = true)
    private String jwks;

    @Column(updatable = true, insertable = true)
    private boolean signIdToken = true;

    @Column(updatable = true, insertable = true)
    private boolean encryptIdToken;

    @Column(length = 255, updatable = true, insertable = true)
    private String idTokenEncryptionAlg;

    @Column(length = 255, updatable = true, insertable = true)
    private String idTokenEncryptionEncoding;

    @Column(length = 255, updatable = true, insertable = true)
    private String sectorIdentifierUri;

    @Column(length = 255, updatable = true, insertable = true)
    private String subjectType;

    @Column(updatable = true, insertable = true)
    private boolean dynamicallyRegistered;

    @Column(updatable = true, insertable = true)
    private boolean implicit;

    @Column(name = "DYNAMIC_REG_TIME")
    private ZonedDateTime dynamicRegistrationDateTime;

    @Lob
    @Column(name = "scopes", length = Integer.MAX_VALUE)
    private HashSet<String> scopes = new HashSet<>();

    public OidcRegisteredService() {
        setJsonFormat(Boolean.TRUE);
    }

    /**
     * Gets subject type.
     *
     * @return the subject type
     */
    public String getSubjectType() {
        if (StringUtils.isBlank(this.subjectType)) {
            return OidcSubjectTypes.PUBLIC.getType();
        }
        return subjectType;
    }

    /**
     * Indicates the service was dynamically registered.
     * Records the registration time automatically.
     *
     * @param dynamicallyRegistered dynamically registered.
     */
    public void setDynamicallyRegistered(final boolean dynamicallyRegistered) {
        if (dynamicallyRegistered && !this.dynamicallyRegistered && dynamicRegistrationDateTime == null) {
            setDynamicRegistrationDateTime(ZonedDateTime.now());
        }
        this.dynamicallyRegistered = dynamicallyRegistered;
    }

    /**
     * Gets scopes.
     *
     * @return the scopes
     */
    public Set<String> getScopes() {
        if (this.scopes == null) {
            this.scopes = new HashSet<>();
        }
        return scopes;
    }

    /**
     * Sets scopes.
     *
     * @param scopes the scopes
     */
    public void setScopes(final Set<String> scopes) {
        getScopes().clear();
        getScopes().addAll(scopes);
    }

    /**
     * Initializes the registered service with default values
     * for fields that are unspecified. Only triggered by JPA.
     */
    @Override
    @PostLoad
    public void postLoad() {
        if (this.scopes == null) {
            this.scopes = new HashSet<>();
        }
    }

    @Override
    protected AbstractRegisteredService newInstance() {
        return new OidcRegisteredService();
    }

    @Override
    @SneakyThrows
    public void copyFrom(final RegisteredService source) {
        super.copyFrom(source);

        final OidcRegisteredService oidcService = (OidcRegisteredService) source;
        setJwks(oidcService.getJwks());
        setImplicit(oidcService.isImplicit());
        setSignIdToken(oidcService.isSignIdToken());
        setIdTokenEncryptionAlg(oidcService.getIdTokenEncryptionAlg());
        setIdTokenEncryptionEncoding(oidcService.idTokenEncryptionEncoding);
        setEncryptIdToken(oidcService.isEncryptIdToken());
        setDynamicallyRegistered(oidcService.isDynamicallyRegistered());
        setScopes(oidcService.getScopes());
        setSectorIdentifierUri(oidcService.getSectorIdentifierUri());
    }

    @JsonIgnore
    @Override
    public String getFriendlyName() {
        return "OpenID Connect Relying Party";
    }
}
