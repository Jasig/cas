package org.apereo.cas.adaptors.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.springframework.dao.DataAccessException;

import javax.annotation.PostConstruct;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

/**
 * Class that given a table, username field and password field will query a
 * database table with the provided encryption technique to see if the user
 * exists. This class defaults to a PasswordTranslator of
 * PlainTextPasswordTranslator.
 *
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @author Marvin S. Addison
 *
 * @since 3.0.0
 */
public class SearchModeSearchDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler {

    private static final String SQL_PREFIX = "Select count('x') from ";
    
    private String fieldUser;
    
    private String fieldPassword;
    
    private String tableUsers;

    private String sql;

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential)
            throws GeneralSecurityException, PreventedException {

        if (StringUtils.isBlank(this.sql) || getJdbcTemplate() == null) {
            throw new GeneralSecurityException("Authentication handler is not configured correctly. "
                    + "No SQL statement or JDBC template found");
        }

        final String username = credential.getUsername();
        final int count;
        try {
            count = getJdbcTemplate().queryForObject(this.sql, Integer.class, username, credential.getPassword());
        } catch (final DataAccessException e) {
            throw new PreventedException("SQL exception while executing query for " + username, e);
        }
        if (count == 0) {
            throw new FailedLoginException(username + " not found with SQL query.");
        }
        return createHandlerResult(credential, this.principalFactory.createPrincipal(username), null);
    }

    /**
     * After properties set.
     */
    @PostConstruct
    public void afterPropertiesSet() {
        if (StringUtils.isNotBlank(this.tableUsers) || StringUtils.isNotBlank(this.fieldUser)
                || StringUtils.isNotBlank(this.fieldPassword)) {
            this.sql = SQL_PREFIX + this.tableUsers + " WHERE " + this.fieldUser + " = ? AND " + this.fieldPassword
                    + " = ?";
        }
    }

    /**
     * @param fieldPassword The fieldPassword to set.
     */
    public void setFieldPassword(final String fieldPassword) {
        this.fieldPassword = fieldPassword;
    }

    /**
     * @param fieldUser The fieldUser to set.
     */
    public void setFieldUser(final String fieldUser) {
        this.fieldUser = fieldUser;
    }

    /**
     * @param tableUsers The tableUsers to set.
     */
    public void setTableUsers(final String tableUsers) {
        this.tableUsers = tableUsers;
    }
}
