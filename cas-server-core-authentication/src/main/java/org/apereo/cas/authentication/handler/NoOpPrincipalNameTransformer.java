package org.apereo.cas.authentication.handler;

/**
 * Simple implementation that actually does NO transformation.
 *
 * @author Scott Battaglia

 * @since 3.3.6
 */
public class NoOpPrincipalNameTransformer implements PrincipalNameTransformer {

    @Override
    public String transform(final String formUserId) {
        return formUserId;
    }
}
