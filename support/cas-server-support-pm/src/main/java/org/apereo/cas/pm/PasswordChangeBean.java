package org.apereo.cas.pm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.io.Serializable;
import lombok.Getter;

/**
 * This is {@link PasswordChangeBean}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
@Slf4j
@Getter
public class PasswordChangeBean implements Serializable {

    private static final long serialVersionUID = 8885460875620586503L;

    private String password;

    private String confirmedPassword;

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setConfirmedPassword(final String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final PasswordChangeBean rhs = (PasswordChangeBean) obj;
        return new EqualsBuilder().append(this.password, rhs.password).append(this.confirmedPassword, rhs.confirmedPassword).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(password).append(confirmedPassword).toHashCode();
    }
}
