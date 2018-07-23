package org.apereo.cas.authentication.principal;

import org.apereo.cas.authentication.Credential;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.profile.UserProfile;

/**
 * This class represents client credentials and (after authentication) a user profile.
 *
 * @author Jerome Leleu
 * @since 3.5.0
 */
@ToString
@Setter
@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class ClientCredential implements Credential {

    /***
     * The name of the client used to perform the authentication.
     */
    public static final String AUTHENTICATION_ATTRIBUTE_CLIENT_NAME = "clientName";

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = -7883301304291894763L;
    /**
     * The internal credentials provided by the authentication at the provider.
     */
    private final transient org.pac4j.core.credentials.Credentials credentials;
    /**
     * Name of the client that established the credential.
     */
    private final String clientName;
    private boolean typedIdUsed = true;
    /**
     * The user profile after authentication.
     */
    private UserProfile userProfile;

    @Override
    public String getId() {
        if (this.userProfile != null) {
            if (this.typedIdUsed) {
                return this.userProfile.getTypedId();
            }
            return this.userProfile.getId();
        }
        return null;
    }
}
