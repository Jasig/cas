package org.apereo.cas.audit.spi;

import org.apereo.cas.authentication.AuthenticationTransaction;
import org.apereo.cas.util.AopUtils;
import org.apereo.inspektr.audit.spi.AuditResourceResolver;
import org.aspectj.lang.JoinPoint;

import java.util.Collections;

/**
 * Converts the Credential object into a String resource identifier.
 *
 * @author Scott Battaglia
 * @since 3.1.2
 *
 */
public class CredentialsAsFirstParameterResourceResolver implements AuditResourceResolver {

    private static final String SUPPLIED_CREDENTIALS = "Supplied credentials: ";

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Object retval) {
        return toResources(AopUtils.unWrapJoinPoint(joinPoint).getArgs());
    }

    @Override
    public String[] resolveFrom(final JoinPoint joinPoint, final Exception exception) {
        return toResources(AopUtils.unWrapJoinPoint(joinPoint).getArgs());
    }

    /**
     * Turn the arguments into a list.
     *
     * @param args the args
     * @return the string[]
     */
    private static String[] toResources(final Object[] args) {
        final Object object = args[0];
        if (object instanceof AuthenticationTransaction) {
            final AuthenticationTransaction transaction = AuthenticationTransaction.class.cast(object);
            return new String[] {SUPPLIED_CREDENTIALS + transaction.getCredentials()};
        }
        // for reviewers: is this List<Object> needed for the concatenation???
        return new String[] {SUPPLIED_CREDENTIALS + Collections.singletonList(object)};
    }
}
