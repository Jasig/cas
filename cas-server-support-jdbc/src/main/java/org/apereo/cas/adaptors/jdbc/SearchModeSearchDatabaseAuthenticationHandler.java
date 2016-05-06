package org.apereo.cas.adaptors.jdbc;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.HandlerResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
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
@RefreshScope
@Component("searchModeSearchDatabaseAuthenticationHandler")
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
            throw new GeneralSecurityException("Authentication handler is not configured correctly");
        }

        final String username = credential.getUsername();
        final String encyptedPassword = getPasswordEncoder().encode(credential.getPassword());
        final int count;
        try {
            count = getJdbcTemplate().queryForObject(this.sql, Integer.class, username, encyptedPassword);
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
    @Autowired
    public void setFieldPassword(@Value("${cas.jdbc.authn.search.password:}") final String fieldPassword) {
        this.fieldPassword = fieldPassword;
    }

    /**
     * @param fieldUser The fieldUser to set.
     */
    @Autowired
    public void setFieldUser(@Value("${cas.jdbc.authn.search.user:}") final String fieldUser) {
        this.fieldUser = fieldUser;
    }

    /**
     * @param tableUsers The tableUsers to set.
     */
    @Autowired
    public void setTableUsers(@Value("${cas.jdbc.authn.search.table:}") final String tableUsers) {
        this.tableUsers = tableUsers;
    }

    @Autowired(required=false)
    @Override
    public void setDataSource(@Qualifier("searchModeDatabaseDataSource") final DataSource dataSource) {
        super.setDataSource(dataSource);
    }
}
