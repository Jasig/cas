package org.apereo.cas.mgmt.services.web.beans;

/**
 * The type Registered service multifactor authentication edit bean.
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class RegisteredServiceMultifactorAuthenticationEditBean {
    private String failureMode;
    private String providers;
    private PrincipalAttribute principalAttr = new PrincipalAttribute();

    public String getFailureMode() {
        return failureMode;
    }

    public void setFailureMode(final String failureMode) {
        this.failureMode = failureMode;
    }

    public String getProviders() {
        return providers;
    }

    public void setProviders(final String providers) {
        this.providers = providers;
    }

    public PrincipalAttribute getPrincipalAttr() {
        return principalAttr;
    }

    public void setPrincipalAttr(final PrincipalAttribute principalAttr) {
        this.principalAttr = principalAttr;
    }

    public static class PrincipalAttribute {
        private String valueMatch;
        private String nameTrigger;

        public String getValueMatch() {
            return valueMatch;
        }

        public void setValueMatch(final String valueMatch) {
            this.valueMatch = valueMatch;
        }

        public String getNameTrigger() {
            return nameTrigger;
        }

        public void setNameTrigger(final String nameTrigger) {
            this.nameTrigger = nameTrigger;
        }
    }
}
