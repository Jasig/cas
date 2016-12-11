package org.apereo.cas.web.config;

import org.apereo.cas.CipherExecutor;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.cookie.CookieProperties;
import org.apereo.cas.util.cipher.NoOpCipherExecutor;
import org.apereo.cas.util.cipher.TicketGrantingCookieCipherExecutor;
import org.apereo.cas.web.WarningCookieRetrievingCookieGenerator;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.apereo.cas.web.support.CookieValueManager;
import org.apereo.cas.web.support.DefaultCasCookieValueManager;
import org.apereo.cas.web.support.NoOpCookieValueManager;
import org.apereo.cas.web.support.TGCCookieRetrievingCookieGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link CasCookieConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Configuration("casCookieConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class CasCookieConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CasCookieConfiguration.class);

    @Autowired
    private CasConfigurationProperties casProperties;

    @Bean
    @RefreshScope
    public CookieRetrievingCookieGenerator warnCookieGenerator() {
        return configureCookieGenerator(new WarningCookieRetrievingCookieGenerator(),
                casProperties.getWarningCookie());
    }

    @Autowired
    @Bean(name = {"defaultCookieValueManager", "cookieValueManager"})
    public CookieValueManager defaultCookieValueManager(@Qualifier("cookieCipherExecutor") final CipherExecutor cipherExecutor) {
        if (casProperties.getTgc().isCipherEnabled()) {
            return new DefaultCasCookieValueManager(cipherExecutor);
        }
        return new NoOpCookieValueManager();
    }

    @ConditionalOnMissingBean(name = "cookieCipherExecutor")
    @RefreshScope
    @Bean
    public CipherExecutor cookieCipherExecutor() {
        if (casProperties.getTgc().isCipherEnabled()) {
            return new TicketGrantingCookieCipherExecutor(casProperties.getTgc().getEncryptionKey(),
                    casProperties.getTgc().getSigningKey());
        }

        LOGGER.info("Ticket-granting cookie encryption/signing is turned off and "
                + "MAY NOT be safe in a production environment. "
                + "Consider using other choices to handle encryption, signing and verification of "
                + "ticket-granting cookies.");
        return NoOpCipherExecutor.getInstance();
    }

    @Autowired
    @Bean
    @RefreshScope
    public CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator(
            @Qualifier("cookieCipherExecutor") final CipherExecutor cipherExecutor) {
        final CookieRetrievingCookieGenerator bean =
                configureCookieGenerator(new TGCCookieRetrievingCookieGenerator(defaultCookieValueManager(cipherExecutor)),
                        casProperties.getTgc());
        bean.setCookieDomain(casProperties.getTgc().getDomain());
        bean.setRememberMeMaxAge(Long.valueOf(casProperties.getTgc().getRememberMeMaxAge()).intValue());
        return bean;
    }

    /**
     * Configure.
     *
     * @param cookieGenerator cookie gen
     * @param props           props
     * @return cookie gen
     */
    private static CookieRetrievingCookieGenerator configureCookieGenerator(final CookieRetrievingCookieGenerator cookieGenerator,
                                                                            final CookieProperties props) {

        cookieGenerator.setCookieName(props.getName());
        cookieGenerator.setCookiePath(props.getPath());
        cookieGenerator.setCookieMaxAge(props.getMaxAge());
        cookieGenerator.setCookieSecure(props.isSecure());
        return cookieGenerator;
    }
}
