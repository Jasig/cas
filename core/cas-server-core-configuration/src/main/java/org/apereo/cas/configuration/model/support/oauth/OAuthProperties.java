package org.apereo.cas.configuration.model.support.oauth;

import org.apereo.cas.configuration.support.RequiresModule;

import java.io.Serializable;

/**
 * This is {@link OAuthProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@RequiresModule(name = "cas-server-support-oauth")
public class OAuthProperties implements Serializable {
    private static final long serialVersionUID = 2677128037234123907L;

    /**
     * Profile view types.
     */
    public enum UserProfileViewTypes {
        /**
         * Return and render the user profile view in nested mode.
         * This is the default option, most usually.
         */
        NESTED,
        /**
         * Return and render the user profile view in flattened mode
         * where all attributes are flattened down to one level only.
         */
        FLAT
    }
    
    /**
     * Settings related to oauth grants.
     */
    private OAuthGrantsProperties grants = new OAuthGrantsProperties();
    /**
     * Settings related to oauth codes.
     */
    private OAuthCodeProperties code = new OAuthCodeProperties();
    /**
     * Settings related to oauth access tokens.
     */
    private OAuthAccessTokenProperties accessToken = new OAuthAccessTokenProperties();
    /**
     * Settings related to oauth refresh tokens.
     */
    private OAuthRefreshTokenProperties refreshToken = new OAuthRefreshTokenProperties();

    /**
     * User profile view type determines how the final user profile should be rendered
     * once an access token is "validated". 
     */
    private UserProfileViewTypes userProfileViewType = UserProfileViewTypes.NESTED;

    /**
     * Name of the authentication throttling bean from cas-server-support-throttle.  Defaults to neverThrottle which
     * disables throttling.  If cas-server-support-throttle module is added then authenticationThrottle bean will be created.
     * This default bean authenticationThrottle can be overridden and/or different ThrottleSubmissionHandlerInterceptor
     * maybe configured for use.
     *
     * @see org.apereo.cas.web.support.ThrottledSubmissionHandlerInterceptor
     * @see org.apereo.cas.web.support.config.CasThrottlingConfiguration#authenticationThrottle()
     */
    private String throttler = "neverThrottle";

    public UserProfileViewTypes getUserProfileViewType() {
        return userProfileViewType;
    }

    public void setUserProfileViewType(final UserProfileViewTypes userProfileViewType) {
        this.userProfileViewType = userProfileViewType;
    }

    public OAuthGrantsProperties getGrants() {
        return grants;
    }

    public void setGrants(final OAuthGrantsProperties grants) {
        this.grants = grants;
    }

    public OAuthAccessTokenProperties getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final OAuthAccessTokenProperties accessToken) {
        this.accessToken = accessToken;
    }

    public OAuthRefreshTokenProperties getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(final OAuthRefreshTokenProperties refreshToken) {
        this.refreshToken = refreshToken;
    }

    public OAuthCodeProperties getCode() {
        return code;
    }

    public void setCode(final OAuthCodeProperties code) {
        this.code = code;
    }

    public String getThrottler() {
        return throttler;
    }

    public void setThrottler(final String throttler) {
        this.throttler = throttler;
    }
}

