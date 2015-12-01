package org.jasig.cas.ticket.support;

import org.jasig.cas.ticket.ExpirationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * This is an {@link org.jasig.cas.ticket.support.AbstractCasExpirationPolicy}
 * that serves as the root parent for all CAS expiration policies
 * and exposes a few internal helper methods to children can access
 * to objects like the request, etc.
 *
 * @author Misagh Moayyed mmoayyed@unicon.net
 * @since 4.1
 */
public abstract class AbstractCasExpirationPolicy implements ExpirationPolicy {

    private static final long serialVersionUID = 8042104336580063690L;

    /** The Logger instance shared by all children of this class. */
    protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Gets the http request based on the
     * {@link org.springframework.web.context.request.RequestContextHolder}.
     * @return the request or null
     */
    protected final HttpServletRequest getRequest() {
        try {
            final ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            if (attrs != null) {
                return attrs.getRequest();
            }
        }  catch (final Exception e) {
            logger.trace("Unable to obtain the http request", e);
        }
        return null;
    }

    /**
     * Override readObject to initialize the transient logger.
     *
     * @param in ObjectInputStream that this object will be read from
     * @throws ClassNotFoundException if the class of a serialized object could not be found.
     * @throws IOException            if an I/O error occurs.
     */
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        logger = LoggerFactory.getLogger(this.getClass());
    }
}
