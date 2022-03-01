package org.apereo.cas.configuration.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.lambda.Unchecked;
import org.reflections.ReflectionUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;

/**
 * This is {@link CasFeatureModule}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
public interface CasFeatureModule {

    private static String getMethodName(final Field field, final String prefix) {
        return prefix
               + field.getName().substring(0, 1).toUpperCase()
               + field.getName().substring(1);
    }

    /**
     * Is defined?
     *
     * @return true/false
     */
    @JsonIgnore
    default boolean isDefined() {
        val fields = ReflectionUtils.getAllFields(getClass(), field -> field.getAnnotation(RequiredProperty.class) != null);
        return fields
            .stream()
            .allMatch(Unchecked.predicate(field -> {
                var getter = getMethodName(field, "get");
                if (ClassUtils.hasMethod(getClass(), getter)) {
                    val method = ClassUtils.getMethod(getClass(), getter);
                    val value = method.invoke(this);
                    return value != null && StringUtils.isNotBlank(value.toString());
                }
                getter = getMethodName(field, "is");
                if (ClassUtils.hasMethod(getClass(), getter)) {
                    val method = ClassUtils.getMethod(getClass(), getter);
                    val value = method.invoke(this);
                    return value != null && BooleanUtils.toBoolean(value.toString());
                }
                return false;
            }));
    }

    /**
     * Is undefined ?.
     *
     * @return true/false
     */
    @JsonIgnore
    default boolean isUndefined() {
        return !isDefined();
    }

    enum FeatureCatalog {
        /**
         * Authentication and login.
         */
        Authentication,
        /**
         * MFA.
         */
        MultifactorAuthentication,
        /**
         * MFA trusted devices.
         */
        MultifactorAuthenticationTrustedDevices,
        /**
         * Delegated authn.
         */
        DelegatedAuthentication,
        /**
         * Auditing and audit log.
         */
        Audit,
        /**
         * Authy MFA.
         */
        Authy,
        /**
         * Authentication events.
         */
        Events,
        /**
         * Account management and signup.
         */
        AccountManagement,
        /**
         * AUP feature.
         */
        AcceptableUsagePolicy,
        /**
         * Person directory and attribute resolution feature.
         */
        PersonDirectory,
        /**
         * SPNEGO authentication.
         */
        SPNEGO,
        /**
         * Passwordless authN.
         */
        PasswordlessAuthn,
        /**
         * U2F MFA.
         */
        U2F,
        /**
         * YubiKey MFA.
         */
        YubiKey,
        /**
         * Electrofence adaptive authentication.
         */
        Electrofence,
        /**
         * ACME.
         */
        ACME,
        /**
         * CAPTCHA integrations.
         */
        CAPTCHA,
        /**
         * Forgot/reset username.
         */
        ForgotUsername,
        /**
         * LDAP authentication and general integrations.
         */
        LDAP,
        /**
         * Interrupt notifications.
         */
        InterruptNotifications,
        /**
         * Radius authn.
         */
        Radius,
        /**
         * RADIUS MFA.
         */
        RadiusMFA,
        /**
         * WebAuthn MFA.
         */
        WebAuthn,
        /**
         * Google Auth MFA.
         */
        GoogleAuthenticator,
        /**
         * SCIM Integration.
         */
        SCIM,
        /**
         * Service registry and management.
         */
        ServiceRegistry,
        /**
         * Service registry streaming files and services.
         */
        ServiceRegistryStreaming,
        /**
         * Surrogate Authn.
         */
        SurrogateAuthentication,
        /**
         * SAML IDP.
         */
        SamlIdP,
        /**
         * SAML IDP metadata management.
         */
        SamlIdPMetadata,
        /**
         * SAML SP metadata management.
         */
        SamlServiceProviderMetadata,
        /**
         * OAuth.
         */
        OAuth,
        /**
         * OIDC.
         */
        OpenIDConnect,
        /**
         * Authn throttling.
         */
        Throttling,
        /**
         * Password management.
         */
        PasswordManagement,
        /**
         * Password history management for history.
         */
        PasswordManagementHistory,
        /**
         * Ticket registry operations.
         */
        TicketRegistry,
        /**
         * Ticket registry locking operations.
         */
        TicketRegistryLocking,
        /**
         * Attribute consent management.
         */
        Consent,
        /**
         * OAuth user managed access.
         */
        UMA
    }
}
