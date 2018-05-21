package org.apereo.cas.util.cipher;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.util.EncodingUtils;
import org.apereo.cas.util.ResourceUtils;
import org.apereo.cas.util.crypto.PrivateKeyFactoryBean;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.keys.AesKey;
import org.jose4j.keys.RsaKeyUtil;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Security;

/**
 * Abstract cipher to provide common operations around signing objects.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Slf4j
@Setter
@NoArgsConstructor
public abstract class AbstractCipherExecutor<T, R> implements CipherExecutor<T, R> {

    private Key signingKey;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Sign the array by first turning it into a base64 encoded string.
     *
     * @param value the value
     * @return the byte [ ]
     */
    protected byte[] sign(final byte[] value) {
        if ("RSA".equalsIgnoreCase(this.signingKey.getAlgorithm())) {
            return EncodingUtils.signJwsRSASha512(this.signingKey, value);
        }
        return EncodingUtils.signJwsHMACSha512(this.signingKey, value);
    }

    /**
     * Sets signing key. If the key provided is resolved as a private key,
     * then will create use the private key as is, and will sign values
     * using RSA. Otherwise, AES is used.
     *
     * @param signingSecretKey the signing secret key
     */
    protected void configureSigningKey(final String signingSecretKey) {
        try {
            if (ResourceUtils.isFile(signingSecretKey) && ResourceUtils.doesResourceExist(signingSecretKey)) {
                final Resource resource = ResourceUtils.getResourceFrom(signingSecretKey);
                LOGGER.debug("Located signing key resource [{}]. Attempting to extract private key...", resource);
                final PrivateKeyFactoryBean factory = new PrivateKeyFactoryBean();
                factory.setAlgorithm(RsaKeyUtil.RSA);
                factory.setLocation(resource);
                factory.setSingleton(false);
                setSigningKey(factory.getObject());
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (this.signingKey == null) {
                setSigningKey(new AesKey(signingSecretKey.getBytes(StandardCharsets.UTF_8)));
                LOGGER.debug("Created signing key instance [{}] based on provided secret key", this.signingKey.getClass().getSimpleName());
            }
        }
    }

    /**
     * Verify signature.
     *
     * @param value the value
     * @return the value associated with the signature, which may have to
     * be decoded, or null.
     */
    protected byte[] verifySignature(final byte[] value) {
        return EncodingUtils.verifyJwsSignature(this.signingKey, value);
    }

    @Override
    public boolean isEnabled() {
        return this.signingKey != null;
    }
}
