package org.apereo.cas;

/**
 * Responsible to define operation that deal with encryption, signing
 * and verification of a value.
 *
 * @author Misagh Moayyed
 * @since 4.1
 */
public interface CipherExecutor<T, R> {

    /**
     * Encrypt the value. Implementations may
     * choose to also sign the final value.
     * @param value the value
     * @return the encrypted value or null
     */
    R encode(T value);

    /**
     * Decode the value. Signatures may also be verified.
     * @param value encrypted value
     * @return the decoded value. 
     */
    R decode(T value);
}
