package org.apereo.cas.config;

import lombok.val;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.api.AuthenticationRequestRiskCalculator;
import org.apereo.cas.api.AuthenticationRiskContingencyPlan;
import org.apereo.cas.api.AuthenticationRiskEvaluator;
import org.apereo.cas.api.AuthenticationRiskMitigator;
import org.apereo.cas.api.AuthenticationRiskNotifier;
import org.apereo.cas.audit.AuditTrailRecordResolutionPlan;
import org.apereo.cas.audit.AuditTrailRecordResolutionPlanConfigurer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.impl.calcs.DateTimeAuthenticationRequestRiskCalculator;
import org.apereo.cas.impl.calcs.GeoLocationAuthenticationRequestRiskCalculator;
import org.apereo.cas.impl.calcs.IpAddressAuthenticationRequestRiskCalculator;
import org.apereo.cas.impl.calcs.UserAgentAuthenticationRequestRiskCalculator;
import org.apereo.cas.impl.engine.DefaultAuthenticationRiskEvaluator;
import org.apereo.cas.impl.engine.DefaultAuthenticationRiskMitigator;
import org.apereo.cas.impl.notify.AuthenticationRiskEmailNotifier;
import org.apereo.cas.impl.notify.AuthenticationRiskSmsNotifier;
import org.apereo.cas.impl.plans.BaseAuthenticationRiskContingencyPlan;
import org.apereo.cas.impl.plans.BlockAuthenticationContingencyPlan;
import org.apereo.cas.impl.plans.MultifactorAuthenticationContingencyPlan;
import org.apereo.cas.support.events.CasEventRepository;
import org.apereo.cas.util.io.CommunicationsManager;
import org.apereo.inspektr.audit.spi.AuditResourceResolver;
import org.apereo.inspektr.audit.spi.support.DefaultAuditActionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashSet;

/**
 * This is {@link ElectronicFenceConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Configuration("electronicFenceConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableScheduling
@Slf4j
public class ElectronicFenceConfiguration implements AuditTrailRecordResolutionPlanConfigurer {

    @Autowired
    @Qualifier("returnValueResourceResolver")
    private AuditResourceResolver returnValueResourceResolver;

    @Autowired
    @Qualifier("communicationsManager")
    private CommunicationsManager communicationsManager;

    @Autowired
    @Qualifier("casEventRepository")
    private CasEventRepository casEventRepository;

    @Autowired
    private CasConfigurationProperties casProperties;

    @ConditionalOnMissingBean(name = "authenticationRiskEmailNotifier")
    @Bean
    @RefreshScope
    public AuthenticationRiskNotifier authenticationRiskEmailNotifier() {
        return new AuthenticationRiskEmailNotifier(communicationsManager);
    }

    @ConditionalOnMissingBean(name = "authenticationRiskSmsNotifier")
    @Bean
    @RefreshScope
    public AuthenticationRiskNotifier authenticationRiskSmsNotifier() {
        return new AuthenticationRiskSmsNotifier(communicationsManager);
    }

    @ConditionalOnMissingBean(name = "blockAuthenticationContingencyPlan")
    @Bean
    @RefreshScope
    public AuthenticationRiskContingencyPlan blockAuthenticationContingencyPlan() {
        val b = new BlockAuthenticationContingencyPlan();
        configureContingencyPlan(b);
        return b;
    }

    @ConditionalOnMissingBean(name = "multifactorAuthenticationContingencyPlan")
    @Bean
    @RefreshScope
    public AuthenticationRiskContingencyPlan multifactorAuthenticationContingencyPlan() {
        val b = new MultifactorAuthenticationContingencyPlan();
        configureContingencyPlan(b);
        return b;
    }

    @ConditionalOnMissingBean(name = "authenticationRiskMitigator")
    @Bean
    @RefreshScope
    public AuthenticationRiskMitigator authenticationRiskMitigator() {
        if (casProperties.getAuthn().getAdaptive().getRisk().getResponse().isBlockAttempt()) {
            return new DefaultAuthenticationRiskMitigator(blockAuthenticationContingencyPlan());
        }
        return new DefaultAuthenticationRiskMitigator(multifactorAuthenticationContingencyPlan());
    }

    @ConditionalOnMissingBean(name = "ipAddressAuthenticationRequestRiskCalculator")
    @Bean
    @RefreshScope
    public AuthenticationRequestRiskCalculator ipAddressAuthenticationRequestRiskCalculator() {
        return new IpAddressAuthenticationRequestRiskCalculator(this.casEventRepository);
    }

    @ConditionalOnMissingBean(name = "userAgentAuthenticationRequestRiskCalculator")
    @Bean
    @RefreshScope
    public AuthenticationRequestRiskCalculator userAgentAuthenticationRequestRiskCalculator() {
        return new UserAgentAuthenticationRequestRiskCalculator(this.casEventRepository);
    }

    @ConditionalOnMissingBean(name = "dateTimeAuthenticationRequestRiskCalculator")
    @Bean
    @RefreshScope
    public AuthenticationRequestRiskCalculator dateTimeAuthenticationRequestRiskCalculator() {
        return new DateTimeAuthenticationRequestRiskCalculator(this.casEventRepository,
            casProperties.getAuthn().getAdaptive().getRisk().getDateTime().getWindowInHours());
    }

    @ConditionalOnMissingBean(name = "geoLocationAuthenticationRequestRiskCalculator")
    @Bean
    @RefreshScope
    public AuthenticationRequestRiskCalculator geoLocationAuthenticationRequestRiskCalculator() {
        return new GeoLocationAuthenticationRequestRiskCalculator(this.casEventRepository);
    }

    @ConditionalOnMissingBean(name = "authenticationRiskEvaluator")
    @Bean
    @RefreshScope
    public AuthenticationRiskEvaluator authenticationRiskEvaluator() {
        val risk = casProperties.getAuthn().getAdaptive().getRisk();
        val calculators = new HashSet<AuthenticationRequestRiskCalculator>();

        if (risk.getIp().isEnabled()) {
            calculators.add(ipAddressAuthenticationRequestRiskCalculator());
        }
        if (risk.getAgent().isEnabled()) {
            calculators.add(userAgentAuthenticationRequestRiskCalculator());
        }
        if (risk.getDateTime().isEnabled()) {
            calculators.add(dateTimeAuthenticationRequestRiskCalculator());
        }
        if (risk.getGeoLocation().isEnabled()) {
            calculators.add(geoLocationAuthenticationRequestRiskCalculator());
        }

        if (calculators.isEmpty()) {
            LOGGER.warn("No risk calculators are defined to examine authentication requests");
        }

        return new DefaultAuthenticationRiskEvaluator(calculators);
    }

    private void configureContingencyPlan(final BaseAuthenticationRiskContingencyPlan b) {
        val mail = casProperties.getAuthn().getAdaptive().getRisk().getResponse().getMail();
        if (StringUtils.isNotBlank(mail.getText()) && StringUtils.isNotBlank(mail.getFrom()) && StringUtils.isNotBlank(mail.getSubject())) {
            b.getNotifiers().add(authenticationRiskEmailNotifier());
        }

        val sms = casProperties.getAuthn().getAdaptive().getRisk().getResponse().getSms();
        if (StringUtils.isNotBlank(sms.getText()) && StringUtils.isNotBlank(sms.getFrom())) {
            b.getNotifiers().add(authenticationRiskSmsNotifier());
        }
    }

    @Override
    public void configureAuditTrailRecordResolutionPlan(final AuditTrailRecordResolutionPlan plan) {
        plan.registerAuditActionResolver("ADAPTIVE_RISKY_AUTHENTICATION_ACTION_RESOLVER", new DefaultAuditActionResolver());
        plan.registerAuditResourceResolver("ADAPTIVE_RISKY_AUTHENTICATION_RESOURCE_RESOLVER", returnValueResourceResolver);
    }
}
