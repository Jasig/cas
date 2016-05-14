package org.apereo.cas.config;

import org.apereo.cas.support.oauth.OAuthConstants;
import org.apereo.cas.support.oauth.web.AccessTokenResponseGenerator;
import org.apereo.cas.support.oauth.web.OAuth20AccessTokenResponseGenerator;
import org.jasig.cas.client.util.URIBuilder;
import org.pac4j.cas.client.CasClient;
import org.pac4j.core.config.Config;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.client.direct.DirectFormClient;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;
import org.pac4j.springframework.web.RequiresAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * This this {@link OAuthConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("oauthConfiguration")
public class OAuthConfiguration extends WebMvcConfigurerAdapter {

    private static final String CAS_OAUTH_CLIENT = "CasOAuthClient";

    @Autowired
    @Qualifier("oAuthUserAuthenticator")
    private UsernamePasswordAuthenticator oAuthUserAuthenticator;

    @Autowired
    @Qualifier("oAuthClientAuthenticator")
    private UsernamePasswordAuthenticator oAuthClientAuthenticator;

    @Value("${server.prefix:http://localhost:8080/cas}/login")
    private String casLoginUrl;

    @Value("${server.prefix:http://localhost:8080/cas}/oauth2.0/callbackAuthorize")
    private String callbackUrl;

    /**
     * Access token response generator access token response generator.
     *
     * @return the access token response generator
     */
    @ConditionalOnMissingBean(name = "accessTokenResponseGenerator")
    @Bean(name = "accessTokenResponseGenerator", autowire = Autowire.BY_NAME)
    public AccessTokenResponseGenerator accessTokenResponseGenerator() {
        return new OAuth20AccessTokenResponseGenerator();
    }

    /**
     * Oauth sec config config.
     *
     * @return the config
     */
    @RefreshScope
    @Bean(name = "oauthSecConfig")
    public Config oauthSecConfig() {
        final CasClient oauthCasClient = new CasClient(this.casLoginUrl);
        oauthCasClient.setName(CAS_OAUTH_CLIENT);
        oauthCasClient.setCallbackUrlResolver((url, context) -> {
            if (url.startsWith(OAuthConfiguration.this.callbackUrl)) {
                final URIBuilder builder = new URIBuilder(url);
                final URIBuilder builderContext = new URIBuilder(context.getFullRequestURL());
                Optional<URIBuilder.BasicNameValuePair> parameter = builderContext.getQueryParams()
                        .stream().filter(p -> p.getName().equals(OAuthConstants.CLIENT_ID))
                        .findFirst();

                if (parameter.isPresent()) {
                    builder.addParameter(parameter.get().getName(), parameter.get().getValue());
                }
                parameter = builderContext.getQueryParams()
                        .stream().filter(p -> p.getName().equals(OAuthConstants.REDIRECT_URI))
                        .findFirst();
                if (parameter.isPresent()) {
                    builder.addParameter(parameter.get().getName(), parameter.get().getValue());
                }
                return builder.build().toString();
            }
            return url;
        });

        final DirectBasicAuthClient basicAuthClient = new DirectBasicAuthClient(this.oAuthClientAuthenticator);
        basicAuthClient.setName("clientBasicAuth");

        final DirectFormClient directFormClient = new DirectFormClient(this.oAuthClientAuthenticator);
        directFormClient.setName("clientForm");
        directFormClient.setUsernameParameter(OAuthConstants.CLIENT_ID);
        directFormClient.setPasswordParameter(OAuthConstants.CLIENT_SECRET);

        final DirectFormClient userFormClient = new DirectFormClient(this.oAuthUserAuthenticator);
        userFormClient.setName("userForm");

        return new Config(this.callbackUrl, oauthCasClient, basicAuthClient, directFormClient, userFormClient);
    }

    /**
     * Requires authentication authorize interceptor requires authentication interceptor.
     *
     * @return the requires authentication interceptor
     */
    @RefreshScope
    @Bean(name = "requiresAuthenticationAuthorizeInterceptor")
    public RequiresAuthenticationInterceptor requiresAuthenticationAuthorizeInterceptor() {
        return new RequiresAuthenticationInterceptor(oauthSecConfig(), CAS_OAUTH_CLIENT);
    }

    /**
     * Requires authentication access token interceptor requires authentication interceptor.
     *
     * @return the requires authentication interceptor
     */
    @RefreshScope
    @Bean(name = "requiresAuthenticationAccessTokenInterceptor")
    public RequiresAuthenticationInterceptor requiresAuthenticationAccessTokenInterceptor() {
        return new RequiresAuthenticationInterceptor(oauthSecConfig(), "clientBasicAuth,clientForm,userForm");
    }


    /**
     * interceptor handler interceptor adapter.
     *
     * @return the handler interceptor adapter
     */
    @Bean(name = "oauthInterceptor")
    public HandlerInterceptorAdapter oauthInterceptor() {
        return new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                                     final Object handler) throws Exception {
                final String requestPath = request.getRequestURI();
                Pattern pattern = Pattern.compile('/' + OAuthConstants.ACCESS_TOKEN_URL + "(/)*$");

                if (pattern.matcher(requestPath).find()) {
                    return requiresAuthenticationAccessTokenInterceptor().preHandle(request, response, handler);
                }

                pattern = Pattern.compile('/' + OAuthConstants.AUTHORIZE_URL + "(/)*$");
                if (pattern.matcher(requestPath).find()) {
                    return requiresAuthenticationAuthorizeInterceptor().preHandle(request, response, handler);
                }
                return true;

            }
        };
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(oauthInterceptor())
                .addPathPatterns(OAuthConstants.BASE_OAUTH20_URL.concat("/").concat("*"));
    }
}
