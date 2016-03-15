package org.jasig.cas.config;

import com.google.common.collect.ImmutableSet;
import org.jasig.cas.security.RequestParameterPolicyEnforcementFilter;
import org.jasig.cas.security.ResponseHeadersEnforcementFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * This is {@link CasFiltersConfiguration} that attempts to create Spring-managed beans
 * backed by external configuration.
 *
 * @author Misagh Moayyed
 * @since 4.3.0
 */
@Configuration("casFiltersConfiguration")
@Lazy(true)
public class CasFiltersConfiguration {

    /**
     * The Encoding.
     */
    @Value("${httprequest.web.encoding:UTF-8}")
    private String encoding;

    /**
     * The Force encoding.
     */
    @Value("${httprequest.web.encoding.force:true}")
    private boolean forceEncoding;

    /**
     * The Header cache.
     */
    @Value("${httpresponse.header.cache:false}")
    private boolean headerCache;

    /**
     * The Header hsts.
     */
    @Value("${httpresponse.header.hsts:false}")
    private boolean headerHsts;

    /**
     * The Header xframe.
     */
    @Value("${httpresponse.header.xframe:false}")
    private boolean headerXframe;

    /**
     * The Header xcontent.
     */
    @Value("${httpresponse.header.xcontent:false}")
    private boolean headerXcontent;

    /**
     * The Header xss.
     */
    @Value("${httpresponse.header.xss:false}")
    private boolean headerXss;

    /**
     * The Allow multi value parameters.
     */
    @Value("${cas.http.allow.multivalue.params:false}")
    private boolean allowMultiValueParameters;

    /**
     * The Only post params.
     */
    @Value("${cas.http.allow.post.params:username,password}")
    private String onlyPostParams;

    /**
     * The Params to check.
     */
    @Value("${cas.http.check.params:"
            + " ticket,service,renew,gateway,warn,method,target,SAMLart,pgtUrl,pgt,pgtId,pgtIou,targetService,entityId}")
    private String paramsToCheck;

    /**
     * Character encoding filter character encoding filter.
     *
     * @return the character encoding filter
     */
    @Bean(name = "characterEncodingFilter")
    public CharacterEncodingFilter characterEncodingFilter() {
        return new CharacterEncodingFilter(this.encoding, this.forceEncoding);
    }

    /**
     * Rsponse headers security filter response headers enforcement filter.
     *
     * @return the response headers enforcement filter
     */
    @Bean(name = "responseHeadersSecurityFilter")
    public ResponseHeadersEnforcementFilter rsponseHeadersSecurityFilter() {
        final ResponseHeadersEnforcementFilter filter = new ResponseHeadersEnforcementFilter();
        filter.setEnableCacheControl(this.headerCache);
        filter.setEnableStrictTransportSecurity(this.headerHsts);
        filter.setEnableXFrameOptions(this.headerXframe);
        filter.setEnableXContentTypeOptions(this.headerXcontent);
        filter.setEnableXSSProtection(this.headerXss);
        return filter;
    }

    /**
     * Request parameter security filter request parameter policy enforcement filter.
     *
     * @return the request parameter policy enforcement filter
     */
    @Bean(name = "requestParameterSecurityFilter")
    public RequestParameterPolicyEnforcementFilter requestParameterSecurityFilter() {
        final RequestParameterPolicyEnforcementFilter filter = new RequestParameterPolicyEnforcementFilter();
        filter.setAllowMultiValueParameters(this.allowMultiValueParameters);
        filter.setParametersToCheck(StringUtils.commaDelimitedListToSet(this.paramsToCheck));
        filter.setCharactersToForbid(ImmutableSet.of());
        filter.setOnlyPostParameters(StringUtils.commaDelimitedListToSet(this.onlyPostParams));
        return filter;
    }
}
