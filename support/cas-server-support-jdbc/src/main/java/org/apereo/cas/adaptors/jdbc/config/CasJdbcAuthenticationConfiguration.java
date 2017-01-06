package org.apereo.cas.adaptors.jdbc.config;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.adaptors.jdbc.BindModeSearchDatabaseAuthenticationHandler;
import org.apereo.cas.adaptors.jdbc.QueryAndEncodeDatabaseAuthenticationHandler;
import org.apereo.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler;
import org.apereo.cas.adaptors.jdbc.SearchModeSearchDatabaseAuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.support.password.PasswordPolicyConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jdbc.JdbcAuthenticationProperties;
import org.apereo.cas.configuration.support.Beans;
import org.apereo.cas.services.ServicesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * This is {@link CasJdbcAuthenticationConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("CasJdbcAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasJdbcAuthenticationConfiguration {

    @Autowired(required = false)
    @Qualifier("queryAndEncodePasswordPolicyConfiguration")
    private PasswordPolicyConfiguration queryAndEncodePasswordPolicyConfiguration;

    @Autowired(required = false)
    @Qualifier("searchModePasswordPolicyConfiguration")
    private PasswordPolicyConfiguration searchModePasswordPolicyConfiguration;

    @Autowired(required = false)
    @Qualifier("queryPasswordPolicyConfiguration")
    private PasswordPolicyConfiguration queryPasswordPolicyConfiguration;

    @Autowired(required = false)
    @Qualifier("bindSearchPasswordPolicyConfiguration")
    private PasswordPolicyConfiguration bindSearchPasswordPolicyConfiguration;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("personDirectoryPrincipalResolver")
    private PrincipalResolver personDirectoryPrincipalResolver;

    @Autowired
    @Qualifier("authenticationHandlersResolvers")
    private Map<AuthenticationHandler, PrincipalResolver> authenticationHandlersResolvers;

    @PostConstruct
    public void initializeJdbcAuthenticationHandlers() {
        casProperties.getAuthn().getJdbc()
                .getBind().forEach(b -> authenticationHandlersResolvers.put(
                bindModeSearchDatabaseAuthenticationHandler(b),
                personDirectoryPrincipalResolver));

        casProperties.getAuthn().getJdbc()
                .getEncode().forEach(b -> authenticationHandlersResolvers.put(
                queryAndEncodeDatabaseAuthenticationHandler(b),
                personDirectoryPrincipalResolver));

        casProperties.getAuthn().getJdbc()
                .getQuery().forEach(b -> authenticationHandlersResolvers.put(
                queryDatabaseAuthenticationHandler(b),
                personDirectoryPrincipalResolver));

        casProperties.getAuthn().getJdbc()
                .getSearch().forEach(b -> authenticationHandlersResolvers.put(
                searchModeSearchDatabaseAuthenticationHandler(b),
                personDirectoryPrincipalResolver));
    }

    private AuthenticationHandler bindModeSearchDatabaseAuthenticationHandler(final JdbcAuthenticationProperties.Bind b) {
        final BindModeSearchDatabaseAuthenticationHandler h = new BindModeSearchDatabaseAuthenticationHandler();
        h.setOrder(b.getOrder());
        h.setDataSource(Beans.newHickariDataSource(b));
        h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        if (bindSearchPasswordPolicyConfiguration != null) {
            h.setPasswordPolicyConfiguration(bindSearchPasswordPolicyConfiguration);
        }

        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));
        h.setPrincipalFactory(jdbcPrincipalFactory());
        h.setServicesManager(servicesManager);

        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
            final Predicate<String> predicate = Pattern.compile(b.getCredentialCriteria()).asPredicate();
            h.setCredentialSelectionPredicate(credential -> predicate.test(credential.getId()));
        }

        h.setName(b.getName());
        return h;
    }

    private AuthenticationHandler queryAndEncodeDatabaseAuthenticationHandler(final JdbcAuthenticationProperties.Encode b) {
        final QueryAndEncodeDatabaseAuthenticationHandler h = new QueryAndEncodeDatabaseAuthenticationHandler(b.getAlgorithmName(), b.getSql(),
                b.getPasswordFieldName(), b.getSaltFieldName(), b.getNumberOfIterationsFieldName(), b.getNumberOfIterations(), b.getStaticSalt());

        h.setOrder(b.getOrder());
        h.setDataSource(Beans.newHickariDataSource(b));

        h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        if (queryAndEncodePasswordPolicyConfiguration != null) {
            h.setPasswordPolicyConfiguration(queryAndEncodePasswordPolicyConfiguration);
        }

        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        h.setPrincipalFactory(jdbcPrincipalFactory());
        h.setServicesManager(servicesManager);

        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
            final Predicate<String> predicate = Pattern.compile(b.getCredentialCriteria()).asPredicate();
            h.setCredentialSelectionPredicate(credential -> predicate.test(credential.getId()));
        }
        h.setName(b.getName());
        return h;
    }

    private AuthenticationHandler queryDatabaseAuthenticationHandler(final JdbcAuthenticationProperties.Query b) {
        final QueryDatabaseAuthenticationHandler h = new QueryDatabaseAuthenticationHandler(b.getSql());
        h.setOrder(b.getOrder());
        h.setDataSource(Beans.newHickariDataSource(b));
        h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        if (queryPasswordPolicyConfiguration != null) {
            h.setPasswordPolicyConfiguration(queryPasswordPolicyConfiguration);
        }

        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));
        h.setPrincipalFactory(jdbcPrincipalFactory());
        h.setServicesManager(servicesManager);

        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
            final Predicate<String> predicate = Pattern.compile(b.getCredentialCriteria()).asPredicate();
            h.setCredentialSelectionPredicate(credential -> predicate.test(credential.getId()));
        }
        h.setName(b.getName());
        return h;
    }

    private AuthenticationHandler searchModeSearchDatabaseAuthenticationHandler(final JdbcAuthenticationProperties.Search b) {
        final SearchModeSearchDatabaseAuthenticationHandler h = new SearchModeSearchDatabaseAuthenticationHandler(b.getFieldUser(), b.getFieldPassword(),
                b.getTableUsers());

        h.setOrder(b.getOrder());
        h.setDataSource(Beans.newHickariDataSource(b));

        h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        if (searchModePasswordPolicyConfiguration != null) {
            h.setPasswordPolicyConfiguration(searchModePasswordPolicyConfiguration);
        }

        h.setPrincipalFactory(jdbcPrincipalFactory());
        h.setServicesManager(servicesManager);

        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
            final Predicate<String> predicate = Pattern.compile(b.getCredentialCriteria()).asPredicate();
            h.setCredentialSelectionPredicate(credential -> predicate.test(credential.getId()));
        }
        h.setName(b.getName());
        return h;
    }

    @Bean
    public PrincipalFactory jdbcPrincipalFactory() {
        return new DefaultPrincipalFactory();
    }
}
