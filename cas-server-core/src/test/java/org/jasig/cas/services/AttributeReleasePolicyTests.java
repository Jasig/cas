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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.services.support.RegisteredServiceRegexAttributeFilter;
import org.junit.Test;

/**
 * Attribute filtering policy tests.
 * @author Misagh Moayyed
 */
public class AttributeReleasePolicyTests {
    @Test
    public void testAttributeFilterMappedAttributes() {
        final ReturnMappedAttributeReleasePolicy policy = new ReturnMappedAttributeReleasePolicy();
        final Map<String, String> mappedAttr = new HashMap<String, String>();
        mappedAttr.put("attr1", "newAttr1");
        
        policy.setAllowedAttributes(mappedAttr);
                
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), 1);
        assertTrue(attr.containsKey("newAttr1"));
        
        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnMappedAttributeReleasePolicy p2 = (ReturnMappedAttributeReleasePolicy) SerializationUtils.deserialize(data);
        assertNotNull(p2);
        assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
    }
    
    @Test
    public void testServiceAttributeFilterAllowedAttributes() {
        final ReturnAllowedAttributeReleasePolicy policy = new ReturnAllowedAttributeReleasePolicy();
        policy.setAllowedAttributes(Arrays.asList("attr1", "attr3"));
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), 2);
        assertTrue(attr.containsKey("attr1"));
        assertTrue(attr.containsKey("attr3"));
        
        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnAllowedAttributeReleasePolicy p2 = (ReturnAllowedAttributeReleasePolicy) SerializationUtils.deserialize(data);
        assertNotNull(p2);
        assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
    }
    
    @Test
    public void testServiceAttributeFilterAllowedAttributesWithARegexFilter() {
        final ReturnAllowedAttributeReleasePolicy policy = new ReturnAllowedAttributeReleasePolicy();
        policy.setAllowedAttributes(Arrays.asList("attr1", "attr3", "another"));
        policy.setAttributeFilter(new RegisteredServiceRegexAttributeFilter("v3"));
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), 1);
        assertTrue(attr.containsKey("attr3"));
        
        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnAllowedAttributeReleasePolicy p2 = (ReturnAllowedAttributeReleasePolicy) SerializationUtils.deserialize(data);
        assertNotNull(p2);
        assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
        assertEquals(p2.getAttributeFilter(), policy.getAttributeFilter());
    }
    
    @Test
    public void testServiceAttributeFilterAllAttributes() {
        final ReturnAllAttributeReleasePolicy policy = new ReturnAllAttributeReleasePolicy();
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), map.size());
        
        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnAllAttributeReleasePolicy p2 = (ReturnAllAttributeReleasePolicy) SerializationUtils.deserialize(data);
        assertNotNull(p2);
    }
}
