package org.apereo.cas.monitor;

import org.apereo.cas.adaptors.ldap.AbstractLdapTests;
import org.apereo.cas.monitor.config.LdapMonitorConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Unit test for {@link PooledLdapConnectionFactoryMonitor} class.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(locations = {"/ldap-context.xml"},
        classes = {LdapMonitorConfiguration.class, RefreshAutoConfiguration.class})
public class PooledConnectionFactoryMonitorTests extends AbstractLdapTests {

    @Autowired
    @Qualifier("pooledLdapConnectionFactoryMonitor")
    private Monitor monitor;

    @BeforeClass
    public static void bootstrap() throws Exception {
        initDirectoryServer();
    }

    @Test
    public void verifyObserve() throws Exception {
        assertEquals(StatusCode.OK, monitor.observe().getCode());
    }
}
