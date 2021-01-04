{% include {{ version }}/couchdb-configuration.md configKey="cas.authn.saml-idp.metadata" %}

```properties
# cas.authn.saml-idp.metadata.couch-db.idp-metadata-enabled=true
```

{% include {{ version }}/signing-encryption-configuration.md configKey="cas.authn.saml-idp.metadata.couch-db" signingKeySize="512" encryptionKeySize="256" encryptionAlg="AES_128_CBC_HMAC_SHA_256" %}
