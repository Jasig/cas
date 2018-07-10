package org.apereo.cas.support.validation;

import lombok.RequiredArgsConstructor;
import lombok.val;

import lombok.extern.slf4j.Slf4j;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.validate.Credential;
import org.apache.wss4j.dom.validate.Validator;
import org.apereo.cas.CipherExecutor;

/**
 * This is {@link CipheredCredentialsValidator}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
@Slf4j
@RequiredArgsConstructor
public class CipheredCredentialsValidator implements Validator {
    private final CipherExecutor cipherExecutor;

    @Override
    public Credential validate(final Credential credential, final RequestData requestData) throws WSSecurityException {
        if (credential != null && credential.getUsernametoken() != null) {
            val usernameToken = credential.getUsernametoken();
            val uid = usernameToken.getName();
            val psw = usernameToken.getPassword();
            if (cipherExecutor.decode(psw).equals(uid)) {
                return credential;
            }
        }
        throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
    }
}
