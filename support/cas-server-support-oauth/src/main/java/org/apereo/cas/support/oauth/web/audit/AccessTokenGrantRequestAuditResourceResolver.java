package org.apereo.cas.support.oauth.web.audit;

import lombok.val;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apereo.cas.support.oauth.web.response.accesstoken.ext.AccessTokenRequestDataHolder;
import org.apereo.inspektr.audit.spi.support.ReturnValueAsStringResourceResolver;
import org.aspectj.lang.JoinPoint;

import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.*;

/**
 * The {@link AccessTokenGrantRequestAuditResourceResolver} for audit advice
 * weaved at {@link org.apereo.cas.support.oauth.web.response.accesstoken.ext.BaseAccessTokenGrantRequestExtractor#extract} join point.
 *
 * @author Dmitriy Kopylenko
 * @since 5.3.0
 */
public class AccessTokenGrantRequestAuditResourceResolver extends ReturnValueAsStringResourceResolver {

    @Override
    public String[] resolveFrom(final JoinPoint auditableTarget, final Object retval) {
        Objects.requireNonNull(retval, "AccessTokenRequestDataHolder must not be null");
        val accessTokenRequest = AccessTokenRequestDataHolder.class.cast(retval);
        val tokenId = accessTokenRequest.getToken() == null ? "N/A" : accessTokenRequest.getToken().getId();

        val result = new ToStringBuilder(this, NO_CLASS_NAME_STYLE)
                .append("token", tokenId)
                .append("client_id", accessTokenRequest.getRegisteredService().getClientId())
                .append("service", accessTokenRequest.getService().getId())
                .append("grant_type", accessTokenRequest.getGrantType().getType())
                .append("scopes", accessTokenRequest.getScopes())
                .toString();

        return new String[]{result};
    }
}
