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
package org.jasig.cas.adaptors.jdbc;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;

import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.SimplePrincipal;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 * Class that if provided a query that returns a password (parameter of query
 * must be username) will compare that password to a translated version of the
 * password provided by the user. If they match, then authentication succeeds.
 * Default password translator is plaintext translator.
 * 
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @version $Revision$ $Date$
 * @since 3.0
 */
public class QueryDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    @NotNull
    private String sql;

    protected final HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credentials)
            throws GeneralSecurityException, IOException {
        final String transformedUsername = getPrincipalNameTransformer().transform(credentials.getUsername());
        final String password = credentials.getPassword();
        final String encryptedPassword = this.getPasswordEncoder().encode(
            password);
        
        try {
            final String dbPassword = getJdbcTemplate().queryForObject(this.sql, String.class, transformedUsername);
            if (dbPassword.equals(encryptedPassword)) {
                return new HandlerResult(this, new SimplePrincipal(transformedUsername));
            }
        } catch (final IncorrectResultSizeDataAccessException e) {
            log.debug("{} not found", transformedUsername);
            throw new AccountNotFoundException();
        }
        log.info("Failed to authenticate {}", transformedUsername);
        throw new FailedLoginException();
    }

    /**
     * @param sql The sql to set.
     */
    public void setSql(final String sql) {
        this.sql = sql;
    }
}