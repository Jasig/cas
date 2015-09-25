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

import org.jasig.cas.support.openid.OpenIdProtocolConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

/**
 * OpenID url handling mappings.
 * @author Scott Battaglia
 * @since 3.1
 */
@Component("openIdPostUrlHandlerMapping")
public final class OpenIdPostUrlHandlerMapping extends SimpleUrlHandlerMapping {

    @Autowired
    @Qualifier("openidDelegatingController")
    private Controller controller;

    @Override
    public void initApplicationContext() throws BeansException {
        setOrder(1);

        final Properties mappings = new Properties();
        mappings.put("/login", this.controller);
        setMappings(mappings);

        super.initApplicationContext();
    }

    @Override
    protected Object lookupHandler(final String urlPath, final HttpServletRequest request) throws Exception {
        if (HttpMethod.POST.name().equals(request.getMethod())
                && (OpenIdProtocolConstants.CHECK_AUTHENTICATION
                .equals(request.getParameter(OpenIdProtocolConstants.OPENID_MODE))
                    || OpenIdProtocolConstants.ASSOCIATE
                .equals(request.getParameter(OpenIdProtocolConstants.OPENID_MODE)))) {
            return super.lookupHandler(urlPath, request);
        }

        return null;
    }
}
