/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.support.openid.web.support;

import org.apache.commons.lang3.NotImplementedException;
import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.web.support.AbstractArgumentExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 * @deprecated As of 4.2, use {@link org.jasig.cas.web.support.DefaultArgumentExtractor}.
 * Constructs an OpenId Service.
 *
 * @author Scott Battaglia
 * @since 3.1
 */
@Deprecated
@Component("openIdArgumentExtractor")
public class OpenIdArgumentExtractor extends AbstractArgumentExtractor {
    /**
     * The prefix url for OpenID (without the trailing slash).
     */
    @NotNull
    @Value("${server.prefix}/openid")
    private String openIdPrefixUrl;

    @Override
    protected WebApplicationService extractServiceInternal(final HttpServletRequest request) {
        throw new NotImplementedException("This operation is not supported. "
                + "The class is deprecated and will be removed in future versions");
    }

    public String getOpenIdPrefixUrl() {
        return openIdPrefixUrl;
    }

    public void setOpenIdPrefixUrl(final String openIdPrefixUrl) {
        this.openIdPrefixUrl = openIdPrefixUrl;
    }
}
