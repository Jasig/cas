---
layout: default
title: CAS - Configuring Principal Resolution
---

# Configuring Principal Resolution
Principal resolution converts information in the authentication credential into a security principal 
that commonly contains additional
metadata attributes (i.e. user details such as affiliations, group membership, email, display name).

A CAS principal contains a unique identifier by which the authenticated user will be known to all requesting
services. A principal also contains optional [attributes that may be released](../integration/Attribute-Release.html)
to services to support authorization and personalization. Principal resolution is a requisite part of the
authentication process that happens after credential authentication.

CAS `AuthenticationHandler` components provide simple principal resolution machinery by default. For example,
the `LdapAuthenticationHandler` component supports fetching attributes and setting the principal ID attribute from
an LDAP query. In all cases principals are resolved from the same store as that which provides authentication.

In many cases it is necessary to perform authentication by one means and resolve principals by another.
The `PrincipalResolver` component provides this functionality. A common use case for this this mix-and-match strategy
arises with X.509 authentication. It is common to store certificates in an LDAP directory and query the directory to
resolve the principal ID and attributes from directory attributes. The `X509CertificateAuthenticationHandler` may
be be combined with an LDAP-based principal resolver to accommodate this case.

## Principal Resolution Components

### `PersonDirectoryPrincipalResolver`
Uses the Person Directory library to provide a flexible principal resolution services against a number of data
sources. The key to configuring `PersonDirectoryPrincipalResolver` is the definition of an `IPersonAttributeDao` object.
The [Person Directory documentation](https://wiki.jasig.org/display/PDM15/Person+Directory+1.5+Manual) provides
configuration for two common examples:

* [Database (JDBC)](https://wiki.jasig.org/x/bBjP)
* [LDAP](https://wiki.jasig.org/x/iBjP)

We present a stub configuration here that can be modified accordingly by consulting the Person Directory documentation.

{% highlight xml %}
<bean id="attributeRepository" class="org.jasig.services.persondir.support.StubPersonAttributeDao">
  <property name="backingMap">
    <map>
      <entry key="uid" value="username"/>
      <entry key="eduPersonAffiliation" value="affiliation"/>
      <entry key="member" value="member"/>
    </map>
  </property>
</bean>

<bean id="principalResolver"
      class="org.jasig.cas.authentication.principal.PersonDirectoryPrincipalResolver"
      p:principalAttributeName="username"
      p:attributeRepository-ref="attributeRepository"
      p:returnNullIfNoAttributes="true" />
{% endhighlight %}


### `OpenIdPrincipalResolver`
Extension of `PersonDirectoryPrincipalResolver` that is specifically for use with OpenID credentials. The configuration
of this component is identical to that of `PersonDirectoryPrincipalResolver`.


### `SpnegoPrincipalResolver`
Extension of `PersonDirectoryPrincipalResolver` that is specifically for use with SPNEGO credentials. The configuration
is the same as that of `PersonDirectoryPrincipalResolver` but with an additional property, `transformPrincipalId`,
that provides a simple case transform on the principal ID. The following values are supported:

* NONE
* LOWERCASE
* UPPERCASE

{% highlight xml %}
<bean id="principalResolver"
      class="org.jasig.cas.support.spnego.authentication.principal.SpnegoPrincipalResolver"
      p:principalAttributeName="username"
      p:attributeRepository-ref="attributeRepository"
      p:returnNullIfNoAttributes="true"
      p:transformPrincipalId="UPPERCASE" />
{% endhighlight %}

### `X509SubjectPrincipalResolver`
Creates a principal ID from a format string composed of components from the subject distinguished name.
See the [X.509 principal resolver](#x_509) section for more information.

### `X509SubjectDNPrincipalResolver`
Creates a principal ID from the certificate subject distinguished name.

### `ChainingPrincipalResolver`
Delegates to one or more principal resolves in series to resolve a principal. The input to first configured
resolver is the authenticated
credential; for every subsequent resolver, the input is a Credential whose ID is the resolved principal
 ID of the previous resolver.
A common use case for this component is resolving a temporary principal ID from an X.509 credential followed
by a search (e.g. LDAP, database) for the final principal based on the temporary ID.

## PrincipalResolver vs. AuthenticationHandler
The principal resolution machinery provided by `AuthenticationHandler` components should be used in preference to
`PrincipalResolver` in any situation where the former provides adequate functionality.
If the principal that is resolved by the authentication handler
suffices, then a `null` value may be passed in place of the resolver bean id:

{% highlight xml %}
<bean id="authenticationManager"
      class="org.jasig.cas.authentication.PolicyBasedAuthenticationManager"
      p:authenticationPolicy-ref="authenticationPolicy">
  <constructor-arg>
      <map>
          <entry key-ref="passwordHandler" value="#{null}"/>
      </map>
  </constructor-arg>
</bean>
{% endhighlight %}
