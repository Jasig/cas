package org.apereo.cas.configuration.model.support.ldap;

import org.ldaptive.handler.CaseChangeEntryHandler;

import java.io.Serializable;

/**
 * This is {@link CaseChangeSearchEntryHandlersProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class CaseChangeSearchEntryHandlersProperties implements Serializable {
    private static final long serialVersionUID = 2420895955116725666L;
    /**
     * The Dn case change.
     */
    private CaseChangeEntryHandler.CaseChange dnCaseChange;
    /**
     * The Attribute name case change.
     */
    private CaseChangeEntryHandler.CaseChange attributeNameCaseChange;
    /**
     * The Attribute value case change.
     */
    private CaseChangeEntryHandler.CaseChange attributeValueCaseChange;
    /**
     * The Attribute names.
     */
    private String[] attributeNames;

    public CaseChangeEntryHandler.CaseChange getDnCaseChange() {
        return dnCaseChange;
    }

    public void setDnCaseChange(final CaseChangeEntryHandler.CaseChange dnCaseChange) {
        this.dnCaseChange = dnCaseChange;
    }

    public CaseChangeEntryHandler.CaseChange getAttributeNameCaseChange() {
        return attributeNameCaseChange;
    }

    public void setAttributeNameCaseChange(final CaseChangeEntryHandler.CaseChange attributeNameCaseChange) {
        this.attributeNameCaseChange = attributeNameCaseChange;
    }

    public CaseChangeEntryHandler.CaseChange getAttributeValueCaseChange() {
        return attributeValueCaseChange;
    }

    public void setAttributeValueCaseChange(final CaseChangeEntryHandler.CaseChange attributeValueCaseChange) {
        this.attributeValueCaseChange = attributeValueCaseChange;
    }

    public String[] getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(final String[] attributeNames) {
        this.attributeNames = attributeNames;
    }
}
