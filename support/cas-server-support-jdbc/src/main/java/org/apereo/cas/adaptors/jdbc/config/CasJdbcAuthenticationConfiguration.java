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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(CasJdbcAuthenticationConfiguration.class);
    
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
        jdbcAuthenticationHandlers().forEach(h -> authenticationHandlersResolvers.put(h, personDirectoryPrincipalResolver));
    }

    @Bean
    public Collection<AuthenticationHandler> jdbcAuthenticationHandlers() {
        final Collection<AuthenticationHandler> handlers = new TreeSet<>();
        casProperties.getAuthn().getJdbc()
                .getBind().forEach(b -> handlers.add(bindModeSearchDatabaseAuthenticationHandler(b)));
        casProperties.getAuthn().getJdbc()
                .getEncode().forEach(b -> handlers.add(queryAndEncodeDatabaseAuthenticationHandler(b)));
        casProperties.getAuthn().getJdbc()
                .getQuery().forEach(b -> handlers.add(queryDatabaseAuthenticationHandler(b)));
        casProperties.getAuthn().getJdbc()
                .getSearch().forEach(b -> handlers.add(searchModeSearchDatabaseAuthenticationHandler(b)));
        return handlers;
    }

    private AuthenticationHandler bindModeSearchDatabaseAuthenticationHandler(final JdbcAuthenticationProperties.Bind b) {
        final BindModeSearchDatabaseAuthenticationHandler h = new BindModeSearchDatabaseAuthenticationHandler(b.getName(), servicesManager);
        h.setOrder(b.getOrder());
        h.setDataSource(Beans.newHickariDataSource(b));
        h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        if (bindSearchPasswordPolicyConfiguration != null) {
            h.setPasswordPolicyConfiguration(bindSearchPasswordPolicyConfiguration);
        }
        
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));
        h.setPrincipalFactory(jdbcPrincipalFactory());

        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
            final Predicate<String> predicate = Pattern.compile(b.getCredentialCriteria()).asPredicate();
            h.setCredentialSelectionPredicate(credential -> predicate.test(credential.getId()));
        }
        
        LOGGER.debug("Created authentication handler {} to handle database url at {}", h.getName(), b.getUrl());
        return h;
    }

    private AuthenticationHandler queryAndEncodeDatabaseAuthenticationHandler(final JdbcAuthenticationProperties.Encode b) {
        final QueryAndEncodeDatabaseAuthenticationHandler h = new QueryAndEncodeDatabaseAuthenticationHandler(b.getName(), servicesManager,
                b.getAlgorithmName(), b.getSql(), b.getPasswordFieldName(), b.getSaltFieldName(), b.getNumberOfIterationsFieldName(),
                b.getNumberOfIterations(), b.getStaticSalt());

        h.setOrder(b.getOrder());
        h.setDataSource(Beans.newHickariDataSource(b));

        h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        if (queryAndEncodePasswordPolicyConfiguration != null) {
            h.setPasswordPolicyConfiguration(queryAndEncodePasswordPolicyConfiguration);
        }

        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        h.setPrincipalFactory(jdbcPrincipalFactory());

        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
            final Predicate<String> predicate = Pattern.compile(b.getCredentialCriteria()).asPredicate();
            h.setCredentialSelectionPredicate(credential -> predicate.test(credential.getId()));
        }

        LOGGER.debug("Created authentication handler {} to handle database url at {}", h.getName(), b.getUrl());
        return h;
    }

    private AuthenticationHandler queryDatabaseAuthenticationHandler(final JdbcAuthenticationProperties.Query b) {
        final QueryDatabaseAuthenticationHandler h = new QueryDatabaseAuthenticationHandler(b.getName(), servicesManager, b.getSql());
        h.setOrder(b.getOrder());
        h.setDataSource(Beans.newHickariDataSource(b));
        h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        if (queryPasswordPolicyConfiguration != null) {
            h.setPasswordPolicyConfiguration(queryPasswordPolicyConfiguration);
        }

        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));
        h.setPrincipalFactory(jdbcPrincipalFactory());

        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
            final Predicate<String> predicate = Pattern.compile(b.getCredentialCriteria()).asPredicate();
            h.setCredentialSelectionPredicate(credential -> predicate.test(credential.getId()));
        }

        LOGGER.debug("Created authentication handler {} to handle database url at {}", h.getName(), b.getUrl());
        return h;
    }

    private AuthenticationHandler searchModeSearchDatabaseAuthenticationHandler(final JdbcAuthenticationProperties.Search b) {
        final SearchModeSearchDatabaseAuthenticationHandler h = new SearchModeSearchDatabaseAuthenticationHandler(b.getName(), servicesManager,
                b.getFieldUser(), b.getFieldPassword(), b.getTableUsers());

        h.setOrder(b.getOrder());
        h.setDataSource(Beans.newHickariDataSource(b));

        h.setPasswordEncoder(Beans.newPasswordEncoder(b.getPasswordEncoder()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));
        h.setPrincipalNameTransformer(Beans.newPrincipalNameTransformer(b.getPrincipalTransformation()));

        if (searchModePasswordPolicyConfiguration != null) {
            h.setPasswordPolicyConfiguration(searchModePasswordPolicyConfiguration);
        }

        h.setPrincipalFactory(jdbcPrincipalFactory());

        if (StringUtils.isNotBlank(b.getCredentialCriteria())) {
            final Predicate<String> predicate = Pattern.compile(b.getCredentialCriteria()).asPredicate();
            h.setCredentialSelectionPredicate(credential -> predicate.test(credential.getId()));
        }

        LOGGER.debug("Created authentication handler {} to handle database url at {}", h.getName(), b.getUrl());
        return h;
    }

    @ConditionalOnMissingBean(name = "jdbcPrincipalFactory")
    @Bean
    public PrincipalFactory jdbcPrincipalFactory() {
        return new DefaultPrincipalFactory();
    }
}
