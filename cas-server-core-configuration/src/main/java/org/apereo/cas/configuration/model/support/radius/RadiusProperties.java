package org.apereo.cas.configuration.model.support.radius;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This is {@link RadiusProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */

public class RadiusProperties {

    private boolean failoverOnException;
    private boolean failoverOnAuthenticationFailure;
    private Server server = new Server();
    private Client client = new Client();
    
    public boolean isFailoverOnException() {
        return failoverOnException;
    }

    public void setFailoverOnException(final boolean failoverOnException) {
        this.failoverOnException = failoverOnException;
    }

    public boolean isFailoverOnAuthenticationFailure() {
        return failoverOnAuthenticationFailure;
    }

    public void setFailoverOnAuthenticationFailure(final boolean failoverOnAuthenticationFailure) {
        this.failoverOnAuthenticationFailure = failoverOnAuthenticationFailure;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(final Server server) {
        this.server = server;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(final Client client) {
        this.client = client;
    }

    public static class Server {
        private String protocol = "EAP_MSCHAPv2";
        private int retries = 3;
        private long nasIdentifier = -1;
        private long nasPort = -1;
        private long nasPortId = -1;
        private long nasRealPort = -1;
        private int nasPortType = -1;
        private String nasIpAddress;
        private String nasIpv6Address;

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(final String protocol) {
            this.protocol = protocol;
        }

        public int getRetries() {
            return retries;
        }

        public void setRetries(final int retries) {
            this.retries = retries;
        }

        public long getNasIdentifier() {
            return nasIdentifier;
        }

        public void setNasIdentifier(final long nasIdentifier) {
            this.nasIdentifier = nasIdentifier;
        }

        public long getNasPort() {
            return nasPort;
        }

        public void setNasPort(final long nasPort) {
            this.nasPort = nasPort;
        }

        public long getNasPortId() {
            return nasPortId;
        }

        public void setNasPortId(final long nasPortId) {
            this.nasPortId = nasPortId;
        }

        public long getNasRealPort() {
            return nasRealPort;
        }

        public void setNasRealPort(final long nasRealPort) {
            this.nasRealPort = nasRealPort;
        }

        public int getNasPortType() {
            return nasPortType;
        }

        public void setNasPortType(final int nasPortType) {
            this.nasPortType = nasPortType;
        }

        public String getNasIpAddress() {
            return nasIpAddress;
        }

        public void setNasIpAddress(final String nasIpAddress) {
            this.nasIpAddress = nasIpAddress;
        }

        public String getNasIpv6Address() {
            return nasIpv6Address;
        }

        public void setNasIpv6Address(final String nasIpv6Address) {
            this.nasIpv6Address = nasIpv6Address;
        }


    }

    public static class Client {
        private String inetAddress = "localhost";
        private String sharedSecret = "N0Sh@ar3d$ecReT";
        private int socketTimeout;
        private int authenticationPort = 1812;
        private int accountingPort = 1813;

        public String getSharedSecret() {
            return sharedSecret;
        }

        public void setSharedSecret(final String sharedSecret) {
            this.sharedSecret = sharedSecret;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(final int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public int getAuthenticationPort() {
            return authenticationPort;
        }

        public void setAuthenticationPort(final int authenticationPort) {
            this.authenticationPort = authenticationPort;
        }

        public int getAccountingPort() {
            return accountingPort;
        }

        public void setAccountingPort(final int accountingPort) {
            this.accountingPort = accountingPort;
        }

        public String getInetAddress() {
            return inetAddress;
        }

        public void setInetAddress(final String inetAddress) {
            this.inetAddress = inetAddress;
        }
    }
}
