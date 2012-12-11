/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
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
package org.jasig.cas.authentication;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a mutable implementation of an authentication event that supports property changes.
 *
 * @author Scott Battaglia
 * @author Marvin S. Addison
 * @since 3.0.3
 */
public final class MutableAuthentication extends AbstractAuthentication {

    /** Unique Id for serialization. */
    private static final long serialVersionUID = -2639544258220286911L;

    /** Authentication timestamp. */
    private final Date authenticatedDate;


    public MutableAuthentication() {
        this.authenticatedDate = new Date();
        setAttributes(new LinkedHashMap<String, Object>());
        setSuccesses(new LinkedHashMap<HandlerResult, Principal>());
        setFailures(new LinkedHashMap<String, GeneralSecurityException>());
    }

    /**
     * Creates a new mutable clone of the given authentication.
     *
     * @param source Source to clone.
     */
    public MutableAuthentication(final Authentication source) {
        this.authenticatedDate = new Date();
        setPrincipal(source.getPrincipal());
        setAttributes(clone(source.getAttributes()));
        setSuccesses(clone(source.getSuccesses()));
        setFailures(clone(source.getFailures()));
    }

    @Override
    public void setPrincipal(final Principal principal) {
        super.setPrincipal(principal);
    }

    public Date getAuthenticatedDate() {
        return this.authenticatedDate;
    }

    private <K, V> Map<K, V> clone(final Map<K, V> map) {
        if (map != null && !map.isEmpty()) {
            return new LinkedHashMap<K, V>(map);
        }
        return new LinkedHashMap<K, V>();
    }
}
