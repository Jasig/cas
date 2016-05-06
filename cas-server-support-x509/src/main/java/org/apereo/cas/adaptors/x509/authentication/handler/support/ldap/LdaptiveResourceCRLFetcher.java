package org.apereo.cas.adaptors.x509.authentication.handler.support.ldap;

import org.apereo.cas.adaptors.x509.authentication.handler.support.ResourceCRLFetcher;
import org.apereo.cas.util.EncodingUtils;

import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchExecutor;
import org.ldaptive.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;

/**
 * Fetches a CRL from an LDAP instance.
 * @author Daniel Fisher
 * @since 4.1
 */
@RefreshScope
@Component("ldaptiveResourceCRLFetcher")
public class LdaptiveResourceCRLFetcher extends ResourceCRLFetcher {

    private static final String LDAP_PREFIX = "ldap";
    
    /** Search exec that looks for the attribute. */
    protected SearchExecutor searchExecutor;

    /** The connection config to prep for connections. **/
    protected ConnectionConfig connectionConfig;

    /** Serialization support. */
    protected LdaptiveResourceCRLFetcher() {}

    /**
     * Instantiates a new Ldap resource cRL fetcher.

     * @param connectionConfig the connection configuration
     * @param searchExecutor the search executor
     */
    public LdaptiveResourceCRLFetcher(
             final ConnectionConfig connectionConfig,
             final SearchExecutor searchExecutor) {
        this.connectionConfig = connectionConfig;
        this.searchExecutor = searchExecutor;
    }

    private boolean isLdap(final String r) {
        return r.toLowerCase().startsWith(LDAP_PREFIX);
    }

    private boolean isLdap(final URI r) {
        return r.getScheme().equalsIgnoreCase(LDAP_PREFIX);
    }

    private boolean isLdap(final URL r) {
        return r.getProtocol().equalsIgnoreCase(LDAP_PREFIX);
    }

    @Override
    public X509CRL fetch(final Resource crl) throws IOException, CRLException, CertificateException {
        if (isLdap(crl.toString())) {
                return fetchCRLFromLdap(crl);
        }
        return super.fetch(crl);
    }

    @Override
    public X509CRL fetch(final URI crl) throws IOException, CRLException, CertificateException {
        if (isLdap(crl)) {
            return fetchCRLFromLdap(crl);
        }
        return super.fetch(crl);
    }

    @Override
    public X509CRL fetch(final URL crl) throws IOException, CRLException, CertificateException {
        if (isLdap(crl)) {
            return fetchCRLFromLdap(crl);
        }
        return super.fetch(crl);
    }

    @Override
    public X509CRL fetch(final String crl) throws IOException, CRLException, CertificateException {
        if (isLdap(crl)) {
            return fetchCRLFromLdap(crl);
        }
        return super.fetch(crl);
    }
    /**
     * Downloads a CRL from given LDAP url.
     *
     * @param r the resource that is the ldap url.
     * @return the x 509 cRL
     * @throws IOException the exception thrown if resources cant be fetched
     * @throws CRLException the exception thrown if resources cant be fetched
     * @throws CertificateException if connection to ldap fails, or attribute to get the revocation list is unavailable
     */
    protected X509CRL fetchCRLFromLdap(final Object r) throws CertificateException, IOException, CRLException {
        try {
            final String ldapURL = r.toString();
            logger.debug("Fetching CRL from ldap {}", ldapURL);

            final Response<SearchResult> result = performLdapSearch(ldapURL);
            if (result.getResultCode() == ResultCode.SUCCESS) {
                final LdapEntry entry = result.getResult().getEntry();
                final LdapAttribute attribute = entry.getAttribute();

                logger.debug("Located entry [{}]. Retrieving first attribute [{}]",
                        entry, attribute);
                return fetchX509CRLFromAttribute(attribute);
            }

            logger.debug("Failed to execute the search [{}]", result);
            throw new CertificateException("Failed to establish a connection ldap and search.");

        } catch (final LdapException e) {
            logger.error(e.getMessage(), e);
            throw new CertificateException(e.getMessage());
        }
    }


    /**
     * Gets x509 cRL from attribute. Retrieves the binary attribute value,
     * decodes it to base64, and fetches it as a byte-array resource.
     *
     * @param aval the attribute, which may be null if it's not found
     * @return the x 509 cRL from attribute
     * @throws IOException the exception thrown if resources cant be fetched
     * @throws CRLException the exception thrown if resources cant be fetched
     * @throws CertificateException if connection to ldap fails, or attribute to get the revocation list is unavailable
     */
    protected X509CRL fetchX509CRLFromAttribute(final LdapAttribute aval) throws CertificateException, IOException, CRLException {
        if (aval != null) {
            final byte[] val = aval.getBinaryValue();
            if (val == null || val.length == 0) {
                throw new CertificateException("Empty attribute. Can not download CRL from ldap");
            }
            final byte[] decoded64 = EncodingUtils.decodeBase64(val);
            if (decoded64 == null) {
                throw new CertificateException("Could not decode the attribute value to base64");
            }
            logger.debug("Retrieved CRL from ldap as byte array decoded in base64. Fetching...");
            return super.fetch(new ByteArrayResource(decoded64));
        }
        throw new CertificateException("Attribute not found. Can not retrieve CRL");
    }

    /**
     * Executes an LDAP search against the supplied URL.
     *
     * @param ldapURL to search
     * @return search result
     * @throws LdapException if an error occurs performing the search
     */
    protected Response<SearchResult> performLdapSearch(final String ldapURL) throws LdapException {
        final ConnectionFactory connectionFactory = prepareConnectionFactory(ldapURL);
        return this.searchExecutor.search(connectionFactory);
    }

    /**
     * Prepare a new LDAP connection.
     *
     * @param ldapURL the ldap uRL
     * @return connection factory
     */
    protected ConnectionFactory prepareConnectionFactory(final String ldapURL) {
        final ConnectionConfig cc = ConnectionConfig.newConnectionConfig(this.connectionConfig);
        cc.setLdapUrl(ldapURL);
        return new DefaultConnectionFactory(cc);
    }

    @Autowired(required=false)
    public void setSearchExecutor(@Qualifier("ldaptiveResourceCRLSearchExecutor") final SearchExecutor searchExecutor) {
        this.searchExecutor = searchExecutor;
    }

    @Autowired(required=false)
    public void setConnectionConfig(@Qualifier("ldaptiveResourceCRLConnectionConfig") final ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }
}
