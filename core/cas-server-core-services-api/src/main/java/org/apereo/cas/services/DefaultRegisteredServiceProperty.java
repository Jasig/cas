package org.apereo.cas.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link DefaultRegisteredServiceProperty} represents
 * a single property associated with a registered service.
 * Properties are assumed to be a set a String values.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Embeddable
@Table(name = "RegexRegisteredServiceProperty")
@Slf4j
@EqualsAndHashCode
public class DefaultRegisteredServiceProperty implements RegisteredServiceProperty {
    private static final long serialVersionUID = 1349556364689133211L;

    @Lob
    @Column(name = "property_values")
    private HashSet<String> values = new HashSet<>();

    @Override
    public Set<String> getValues() {
        if (this.values == null) {
            this.values = new HashSet<>();
        }
        return this.values;
    }

    @Override
    @JsonIgnore
    public String getValue() {
        if (this.values.isEmpty()) {
            return null;
        }
        return this.values.iterator().next();
    }

    @Override
    public boolean contains(final String value) {
        return this.values.contains(value);
    }

    /**
     * Sets values.
     *
     * @param values the values
     */
    public void setValues(final Set<String> values) {
        getValues().clear();
        if (values == null) {
            return;
        }
        getValues().addAll(values);
    }

    /**
     * Add value.
     *
     * @param value the value
     */
    public void addValue(final String value) {
        getValues().add(value);
    }

}
