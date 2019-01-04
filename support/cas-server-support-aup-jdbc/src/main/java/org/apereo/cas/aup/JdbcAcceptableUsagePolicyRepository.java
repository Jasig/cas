package org.apereo.cas.aup;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.ticket.registry.TicketRegistrySupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.webflow.execution.RequestContext;

import javax.sql.DataSource;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.configuration.model.support.aup.AcceptableUsagePolicyProperties;
import org.apereo.cas.web.support.WebUtils;

/**
 * This is {@link JdbcAcceptableUsagePolicyRepository}.
 * Examines the principal attribute collection to determine if
 * the policy has been accepted, and if not, allows for a configurable
 * way so that user's choice can later be remembered and saved back into
 * the jdbc instance.
 *
 * @author Misagh Moayyed
 * @since 5.2
 */
@Slf4j
public class JdbcAcceptableUsagePolicyRepository extends AbstractPrincipalAttributeAcceptableUsagePolicyRepository {
    private static final long serialVersionUID = 1600024683199961892L;
    
    private final transient JdbcTemplate jdbcTemplate;
    private final AcceptableUsagePolicyProperties properties;

    public JdbcAcceptableUsagePolicyRepository(final TicketRegistrySupport ticketRegistrySupport,
                                               final String aupAttributeName,
                                               final DataSource dataSource,
                                               final AcceptableUsagePolicyProperties properties) {
        super(ticketRegistrySupport, aupAttributeName);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.properties = properties;
    }

    @Override
    public boolean submit(final RequestContext requestContext, final Credential credential) {
        try {
            final AcceptableUsagePolicyProperties.Jdbc jdbc = properties.getJdbc();
            String aupColumnName = properties.getAupAttributeName();
            if (StringUtils.isNotBlank(jdbc.getAupColumn())) {
                aupColumnName = jdbc.getAupColumn();
            }
            final String sql = String.format(jdbc.getSqlUpdateAUP(), jdbc.getTableName(), aupColumnName, jdbc.getPrincipalIdColumn());
            String principalId = credential.getId();
            if (StringUtils.isNotBlank(jdbc.getPrincipalIdAttribute())) {
                @NonNull
                final Principal principal = WebUtils.getAuthentication(requestContext).getPrincipal();
                String pIdAttribName = jdbc.getPrincipalIdAttribute();
                if (principal.getAttributes().containsKey(pIdAttribName)) {
                    Object pIdAttribValue = principal.getAttributes().get(pIdAttribName);
                    if (pIdAttribValue instanceof String) {
                        principalId = pIdAttribValue.toString();
                    } else {
                        LOGGER.warn("Principal attribute [{}] was found, but its value [{}] is not a String", pIdAttribName, pIdAttribValue);
                    }
                } else {
                    LOGGER.warn("Principal attribute [{}] cannot be found. Falling back to principal ID", pIdAttribName);
                }
            }
            LOGGER.debug("Executing update query [{}] for principal [{}]", sql, principalId);
            return this.jdbcTemplate.update(sql, principalId) > 0;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
