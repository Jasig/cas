#### Hazelcast Configuration

{% capture cfgkey %}{{ include.configKey }}.core{% endcapture %}
{% include_cached casproperties.html properties=cfgkey %}
   
#### Hazelcast Cluster Core

{% capture cfgkey %}{{ include.configKey }}.cluster.core{% endcapture %}
{% include_cached casproperties.html properties=cfgkey %}

#### Hazelcast Cluster Networking

{% capture cfgkey %}{{ include.configKey }}.cluster.network{% endcapture %}
{% include_cached casproperties.html properties=cfgkey excludes=".ssl" %}

#### Hazelcast Network TLS Encryption

You can use the TLS (Transport Layer Security) protocol to establish an encrypted
communication across your Hazelcast cluster with key stores and trust stores. Hazelcast allows you
to encrypt socket level communication between Hazelcast members and
between Hazelcast clients and members, for end to end encryption. 

<div class="alert alert-info">:information_source: <strong>Usage</strong><p>
Hazelcast SSL is an enterprise feature. You will need a proper Hazelcast Enterprise License to use this facility.</p></div>

Hazelcast provides a default SSL context factory implementation, which is guided 
and auto-configured by CAS when enabled to use the configured keystore to initialize SSL context.

{% capture cfgkey %}{{ include.configKey }}.cluster.network.ssl{% endcapture %}
{% include_cached casproperties.html id="tls" properties=cfgkey %}

<div class="alert alert-info">:information_source: <strong>Performance</strong><p>
Under Linux, the JVM automatically makes use of <code>/dev/random</code> for the 
generation of random numbers. If this entropy is insufficient to keep up with the rate 
requiring random numbers, it can slow down the encryption/decryption since it could block for 
minutes waiting for sufficient entropy . This can be fixed 
by setting the <code>-Djava.security.egd=file:/dev/./urandom</code> system property.
Note that if there is a shortage of entropy, this option will not block 
and the returned random values could theoretically be vulnerable to a cryptographic attack.
</p></div>

