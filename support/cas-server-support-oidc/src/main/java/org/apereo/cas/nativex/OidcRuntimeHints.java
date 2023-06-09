package org.apereo.cas.nativex;

import org.apereo.cas.oidc.ticket.OidcDefaultPushedAuthorizationRequest;
import org.apereo.cas.oidc.token.OidcJwtAccessTokenCipherExecutor;
import org.apereo.cas.oidc.web.response.OidcJwtResponseModeCipherExecutor;
import org.apereo.cas.services.OidcRegisteredService;
import org.apereo.cas.util.nativex.CasRuntimeHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;

import java.util.List;

/**
 * This is {@link OidcRuntimeHints}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
public class OidcRuntimeHints implements CasRuntimeHintsRegistrar {
    @Override
    public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
        hints.serialization()
            .registerType(OidcRegisteredService.class)
            .registerType(OidcDefaultPushedAuthorizationRequest.class);

        List.of(
            OidcRegisteredService.class,
            OidcJwtAccessTokenCipherExecutor.class,
            OidcJwtResponseModeCipherExecutor.class
        ).forEach(el ->
            hints.reflection().registerType(TypeReference.of(el),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.PUBLIC_FIELDS));
    }
}

