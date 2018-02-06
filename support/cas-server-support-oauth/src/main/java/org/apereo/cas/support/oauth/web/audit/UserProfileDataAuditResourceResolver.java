package org.apereo.cas.support.oauth.web.audit;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.inspektr.audit.spi.support.ReturnValueAsStringResourceResolver;
import org.aspectj.lang.JoinPoint;

import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.NO_CLASS_NAME_STYLE;
import static org.apereo.cas.CasProtocolConstants.PARAMETER_SERVICE;
import static org.apereo.cas.support.oauth.OAuth20Constants.CLIENT_ID;
import static org.apereo.cas.support.oauth.web.views.OAuth20UserProfileViewRenderer.MODEL_ATTRIBUTE_ATTRIBUTES;
import static org.apereo.cas.support.oauth.web.views.OAuth20UserProfileViewRenderer.MODEL_ATTRIBUTE_ID;

/**
 * The {@link UserProfileDataAuditResourceResolver}.
 *
 * @author Dmitriy Kopylenko
 * @since 5.3.0
 */
public class UserProfileDataAuditResourceResolver extends ReturnValueAsStringResourceResolver {
    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Object retval) {
        Objects.requireNonNull(retval, "User profile data Map<String, Object> must not be null");
        final Map profileMap = Map.class.cast(retval);
        final AccessToken accessToken = AccessToken.class.cast(auditableTarget.getArgs()[0]);

        final String result = new ToStringBuilder(this, NO_CLASS_NAME_STYLE)
            .append("user_profile_id", profileMap.get(MODEL_ATTRIBUTE_ID))
            .append("client_id", profileMap.get(CLIENT_ID))
            .append("client_service", profileMap.get(PARAMETER_SERVICE))
            .append("scopes", accessToken.getScopes())
            .append("user_profile_attributes", profileMap.get(MODEL_ATTRIBUTE_ATTRIBUTES))
            .toString();

        return new String[]{result};
    }
}
