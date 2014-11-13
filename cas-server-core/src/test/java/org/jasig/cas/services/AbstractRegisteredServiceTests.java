/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.ShibbolethCompatiblePersistentIdGenerator;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit test for {@link AbstractRegisteredService}.
 *
 * @author Marvin S. Addison
 */
public class AbstractRegisteredServiceTests {

    private static final long ID = 1000;
    private static final String DESCRIPTION = "test";
    private static final String SERVICEID = "serviceId";
    private static final String THEME = "theme";
    private static final String NAME = "name";
    private static final boolean ENABLED = false;
    private static final boolean ALLOWED_TO_PROXY = false;
    private static final boolean SSO_ENABLED = false;
    
    private final AbstractRegisteredService r = new AbstractRegisteredService() {
        private static final long serialVersionUID = 1L;

        public void setServiceId(final String id) {
            serviceId = id;
        }

        protected AbstractRegisteredService newInstance() {
            return this;
        }

        public boolean matches(final Service service) {
            return true;
        }
    };

    @Test
    public void testAllowToProxyIsFalseByDefault() {
        final RegexRegisteredService regexRegisteredService = new RegexRegisteredService();
        assertFalse(regexRegisteredService.getProxyPolicy().isAllowedToProxy());
        final RegisteredServiceImpl registeredServiceImpl = new RegisteredServiceImpl();
        assertFalse(registeredServiceImpl.getProxyPolicy().isAllowedToProxy());
    }

    @Test
    public void testSettersAndGetters() {
        prepareService();

        assertEquals(ALLOWED_TO_PROXY, this.r.getProxyPolicy().isAllowedToProxy());
        assertEquals(DESCRIPTION, this.r.getDescription());
        assertEquals(ENABLED, this.r.isEnabled());
        assertEquals(ID, this.r.getId());
        assertEquals(NAME, this.r.getName());
        assertEquals(SERVICEID, this.r.getServiceId());
        assertEquals(SSO_ENABLED, this.r.isSsoEnabled());
        assertEquals(THEME, this.r.getTheme());

        assertFalse(this.r.equals(null));
        assertFalse(this.r.equals(new Object()));
        assertTrue(this.r.equals(this.r));
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(r.equals(r.clone()));
        assertFalse(new RegisteredServiceImpl().equals(null));
        assertFalse(new RegisteredServiceImpl().equals(new Object()));
    }
    
    private void prepareService() {
        this.r.setUsernameAttributeProvider(
                new AnonymousRegisteredServiceUsernameAttributeProvider(
                new ShibbolethCompatiblePersistentIdGenerator("casrox")));
        this.r.setDescription(DESCRIPTION);
        this.r.setEnabled(ENABLED);
        this.r.setId(ID);
        this.r.setName(NAME);
        this.r.setServiceId(SERVICEID);
        this.r.setSsoEnabled(SSO_ENABLED);
        this.r.setTheme(THEME);
    }
    
    @Test
    public void testServiceAttributeFilterAllAttributes() {
        prepareService();
        this.r.setAttributeReleasePolicy(new ReturnAllAttributeReleasePolicy());
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = this.r.getAttributeReleasePolicy().getAttributes(p);
        assertEquals(attr.size(), map.size());
    }
    
    @Test
    public void testServiceAttributeFilterAllowedAttributes() {
        prepareService();
        final ReturnAllowedAttributeReleasePolicy policy = new ReturnAllowedAttributeReleasePolicy();
        policy.setAllowedAttributes(Arrays.asList("attr1", "attr3"));
        this.r.setAttributeReleasePolicy(policy);
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = this.r.getAttributeReleasePolicy().getAttributes(p);
        assertEquals(attr.size(), 2);
        assertTrue(attr.containsKey("attr1"));
        assertTrue(attr.containsKey("attr3"));
    }
    
    @Test
    public void testServiceAttributeFilterMappedAttributes() {
        prepareService();
        final ReturnMappedAttributeReleasePolicy policy = new ReturnMappedAttributeReleasePolicy();
        final Map<String, String> mappedAttr = new HashMap<String, String>();
        mappedAttr.put("attr1", "newAttr1");
        
        policy.setAllowedAttributes(mappedAttr);
                
        this.r.setAttributeReleasePolicy(policy);
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = this.r.getAttributeReleasePolicy().getAttributes(p);
        assertEquals(attr.size(), 1);
        assertTrue(attr.containsKey("newAttr1"));
    }
}
