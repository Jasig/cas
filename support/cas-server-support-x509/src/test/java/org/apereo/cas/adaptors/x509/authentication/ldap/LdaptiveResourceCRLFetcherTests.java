package org.apereo.cas.adaptors.x509.authentication.ldap;

import org.apereo.cas.adaptors.ldap.LdapIntegrationTestsOperations;
import org.apereo.cas.adaptors.x509.authentication.CRLFetcher;
import org.apereo.cas.adaptors.x509.authentication.handler.support.AbstractX509LdapTests;
import org.apereo.cas.adaptors.x509.authentication.revocation.checker.CRLDistributionPointRevocationChecker;
import org.apereo.cas.adaptors.x509.authentication.revocation.policy.AllowRevocationPolicy;
import org.apereo.cas.util.crypto.CertUtils;

import lombok.val;
import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for {@link LdaptiveResourceCRLFetcher}
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
@Tag("Ldap")
public class LdaptiveResourceCRLFetcherTests {
    private static final int LDAP_PORT = 1389;

    @BeforeAll
    public static void bootstrapTests() throws Exception {
        LdapIntegrationTestsOperations.initDirectoryServer(LDAP_PORT);
        AbstractX509LdapTests.bootstrap(LDAP_PORT);
    }
    
    @Nested
    @Tag("Ldap")
    @TestPropertySource(properties = "cas.authn.x509.ldap.certificate-attribute=cn")
    @SuppressWarnings("ClassCanBeStatic")
    public class InvalidNonBinaryAttributeFetchFromLdap extends BaseX509LdapResourceFetcherTests {
        @Autowired
        @Qualifier("crlFetcher")
        private CRLFetcher fetcher;

        @Test
        public void verifyResourceFromResourceUrl() throws Exception {
            val resource = mock(Resource.class);
            when(resource.toString()).thenReturn("ldap://localhost:1389");
            assertThrows(CertificateException.class, () -> fetcher.fetch(resource));
        }
    }

    @Nested
    @Tag("Ldap")
    @TestPropertySource(properties = "cas.authn.x509.ldap.certificate-attribute=unknown")
    @SuppressWarnings("ClassCanBeStatic")
    public class UnknownAttributeFetchFromLdap extends BaseX509LdapResourceFetcherTests {
        @Autowired
        @Qualifier("crlFetcher")
        private CRLFetcher fetcher;

        @Test
        public void verifyResourceFromResourceUrl() throws Exception {
            val resource = mock(Resource.class);
            when(resource.toString()).thenReturn("ldap://localhost:1389");
            assertThrows(CertificateException.class, () -> fetcher.fetch(resource));
        }
    }
    
    @Nested
    @Tag("Ldap")
    @SuppressWarnings("ClassCanBeStatic")
    public class DefaultFetchFromLdap extends BaseX509LdapResourceFetcherTests {
        @Autowired
        @Qualifier("crlFetcher")
        private CRLFetcher fetcher;
        
        @Test
        public void verifyResourceFromResourceUrl() throws Exception {
            val resource = mock(Resource.class);
            when(resource.toString()).thenReturn("ldap://localhost:1389");
            assertNotNull(fetcher.fetch(resource));

            val uri = new URI("ldap://localhost:1389");
            assertNotNull(fetcher.fetch(uri));

            val url = mock(URL.class);
            when(url.toString()).thenReturn("ldap://localhost:1389");
            when(url.getProtocol()).thenReturn("ldap");
            assertNotNull(fetcher.fetch(url));

            assertNotNull(fetcher.fetch("ldap://localhost:1389"));
        }
        
        @Test
        public void getCrlFromLdap() throws Exception {
            val cache = getCache(100);
            for (var i = 0; i < 10; i++) {
                val checker =
                    new CRLDistributionPointRevocationChecker(false, new AllowRevocationPolicy(), null,
                        cache, fetcher, true);
                val cert = CertUtils.readCertificate(new ClassPathResource("ldap-crl.crt"));
                checker.check(cert);
            }
        }

        @Test
        public void getCrlFromLdapWithNoCaching() throws Exception {
            for (var i = 0; i < 10; i++) {
                val cache = getCache(100);
                val checker = new CRLDistributionPointRevocationChecker(
                    false, new AllowRevocationPolicy(), null,
                    cache, fetcher, true);
                val cert = CertUtils.readCertificate(new ClassPathResource("ldap-crl.crt"));
                checker.check(cert);
            }
        }

        private UserManagedCache<URI, byte[]> getCache(final int entries) {
            return UserManagedCacheBuilder.newUserManagedCacheBuilder(URI.class, byte[].class)
                .withResourcePools(ResourcePoolsBuilder.heap(entries)).build();
        }
    }
}
