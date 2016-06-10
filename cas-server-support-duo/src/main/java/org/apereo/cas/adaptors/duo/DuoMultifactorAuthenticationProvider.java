package org.apereo.cas.adaptors.duo;

import org.apereo.cas.adaptors.duo.web.flow.DuoMultifactorWebflowConfigurer;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.AbstractMultifactorAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * This is {@link DuoMultifactorAuthenticationProvider}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class DuoMultifactorAuthenticationProvider extends AbstractMultifactorAuthenticationProvider {

    private static final long serialVersionUID = 4789727148634156909L;
    
    @Autowired
    private CasConfigurationProperties casProperties;

    @Resource(name = "duoAuthenticationService")
    private DuoAuthenticationService duoAuthenticationService;


    @Override
    public String getId() {
        return DuoMultifactorWebflowConfigurer.MFA_DUO_EVENT_ID;
    }

    @Override
    public int getOrder() {
        return casProperties.getAuthn().getMfa().getDuo().getRank();
    }


    @Override
    protected boolean isAvailable() {
        return this.duoAuthenticationService.canPing();
    }
}
