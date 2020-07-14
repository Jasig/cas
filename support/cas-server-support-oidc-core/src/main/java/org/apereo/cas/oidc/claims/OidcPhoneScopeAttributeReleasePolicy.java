package org.apereo.cas.oidc.claims;

import org.apereo.cas.oidc.OidcConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * This is {@link OidcPhoneScopeAttributeReleasePolicy}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class OidcPhoneScopeAttributeReleasePolicy extends BaseOidcScopeAttributeReleasePolicy {
    private static final long serialVersionUID = 1532960981124784595L;

    public static final List<String> ALLOWED_ATTRIBUTES = List.of("phone_number", "phone_number_verified");

    public OidcPhoneScopeAttributeReleasePolicy() {
        super(OidcConstants.StandardScopes.PHONE.getScope());
        setAllowedAttributes(ALLOWED_ATTRIBUTES);
    }

    @JsonIgnore
    @Override
    public List<String> getAllowedAttributes() {
        return super.getAllowedAttributes();
    }
}
