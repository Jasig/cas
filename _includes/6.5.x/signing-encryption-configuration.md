
<p/>

This CAS feature is able to accept signing and encryption crypto keys. In most scenarios if keys are not provided, CAS will
auto-generate them. The following instructions apply if you wish to manually and beforehand create the signing and encryption keys.

Note that if you are asked to create a <a href="https://tools.ietf.org/html/rfc7517">JWK</a> of a certain size for the key, you are to use
the following set of commands to generate the token:

```bash
wget https://raw.githubusercontent.com/apereo/cas/master/etc/jwk-gen.jar
java -jar jwk-gen.jar -t oct -s [size]
```


The outcome would be similar to:

```json
{
  "kty": "oct",
  "kid": "...",
  "k": "..."
}
```

The generated value for `k` needs to be assigned to the relevant CAS settings. Note that keys generated via
the above algorithm are processed by CAS using the Advanced Encryption Standard (`AES`) algorithm which is a
specification for the encryption of electronic data established by the U.S. National Institute of Standards and Technology.

{% if include.includeRsaKeys == "true" %}

#### RSA Keys

Certain CAS features such as the ability to produce JWTs as CAS tickets
may allow you to use the `RSA` algorithm with public/private keypairs for signing and encryption. This
behavior may prove useful generally in cases where the consumer of the CAS-encoded payload is an
outsider and a client application that need not have access to the signing secrets directly and
visibly and may only be given a half truth vis-a-vis a public key to verify the payload authenticity
and decode it. This particular option makes little sense in situations where CAS itself is both
a producer and a consumer of the payload.

<div class="alert alert-info mt-3"><strong>Remember</strong><p>Signing and encryption options are not 
mutually exclusive. While it would be rather nonsensical, it is entirely possible for CAS to 
use <code>AES</code> keys for signing and <code>RSA</code> keys for encryption, or vice versa.</p></div>

In order to enable RSA functionality for signing payloads, you will need to
generate a private/public keypair via the following sample commands:

```bash
openssl genrsa -out private.key 2048
openssl rsa -pubout -in private.key -out public.key -inform PEM -outform DER
```

The private key path (i.e. `file:///path/to/private.key`) needs to be configured for the
signing key in CAS properties for the relevant feature. The public key needs to be shared
with client applications and consumers of the payload in order to validate the payload signature.

<div class="alert alert-info mt-3"><strong>Key Size</strong><p>Remember that RSA key sizes 
are required to be at least <code>2048</code> and above. Smaller key sizes are not 
accepted by CAS and will cause runtime errors. Choose wisely.</p></div>

In order to enable RSA functionality for encrypting payloads, you will need to essentially
execute the reverse of the above operations. The client application will provide you with
a public key which will be used to encrypt the payload and whose path (i.e. `file:///path/to/public.key`)
needs to be configured for the encryption key in CAS properties for the relevant feature.
Once the payload is submitted, the client should use its own private key to decode the payload and unpack it.

{% endif %}

<hr>
