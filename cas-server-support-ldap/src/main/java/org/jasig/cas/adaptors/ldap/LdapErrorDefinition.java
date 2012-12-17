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
package org.jasig.cas.adaptors.ldap;

import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LdapErrorDefinition {

    private Pattern ldapPattern = null;

    private GeneralSecurityException mappedException;

    public GeneralSecurityException getMappedException() {
        return mappedException;
    }

    public void setMappedException(final GeneralSecurityException mappedException) {
        this.mappedException = mappedException;
    }

    public boolean matches(final String msg) {
        final Matcher matcher = getLdapPattern().matcher(msg);
        return matcher.find();
    }

    public void setLdapPattern(final String ldapPattern) {
        this.ldapPattern = Pattern.compile(ldapPattern);
    }

    private Pattern getLdapPattern() {
        return this.ldapPattern;
    }

    @Override
    public String toString() {
        return "LdapErrorDefinition for " + this.mappedException.getClass().getSimpleName();
    }
}
