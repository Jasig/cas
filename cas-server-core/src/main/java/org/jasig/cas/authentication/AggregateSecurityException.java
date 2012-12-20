/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.authentication;

import java.security.GeneralSecurityException;
import java.util.Collection;

/**
 * Aggregates a collection of {@link GeneralSecurityException} that occur during a batch process where security
 * exceptions may be thrown multiple times during the process. This is effectively a container type for bulk processes
 * such as authenticating multiple credentials, each of which may fail with {@link GeneralSecurityException}.
 *
 * @author Marvin S. Addison
 * @since 4.0
 */
public class AggregateSecurityException extends GeneralSecurityException {

    private final GeneralSecurityException[] errors;

    public AggregateSecurityException(final GeneralSecurityException ... errors) {
        this.errors = errors;
    }

    public AggregateSecurityException(final Collection<GeneralSecurityException> errors) {
        this.errors = errors.toArray(new GeneralSecurityException[errors.size()]);
    }

    public GeneralSecurityException[] getErrors() {
        return this.errors;
    }

    public String getMessage() {
        final StringBuilder sb = new StringBuilder("Errors: [");
        int i = 0;
        for (final GeneralSecurityException e : this.errors) {
            sb.append(e.getClass().getName()).append(':').append(e.getMessage());
            if (i++ > 0) {
                sb.append(", ");
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
