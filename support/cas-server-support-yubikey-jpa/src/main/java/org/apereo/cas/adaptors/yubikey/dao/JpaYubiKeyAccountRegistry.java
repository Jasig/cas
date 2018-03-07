package org.apereo.cas.adaptors.yubikey.dao;

import com.yubico.client.v2.YubicoClient;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccount;
import org.apereo.cas.adaptors.yubikey.YubiKeyAccountValidator;
import org.apereo.cas.adaptors.yubikey.registry.BaseYubiKeyAccountRegistry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * This is {@link JpaYubiKeyAccountRegistry}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@EnableTransactionManagement(proxyTargetClass = true)
@Transactional(transactionManager = "transactionManagerYubiKey", readOnly = false)
@Slf4j
public class JpaYubiKeyAccountRegistry extends BaseYubiKeyAccountRegistry {

    private static final String SELECT_QUERY = "SELECT r from YubiKeyAccount r ";

    @PersistenceContext(unitName = "yubiKeyEntityManagerFactory")
    private EntityManager entityManager;

    public JpaYubiKeyAccountRegistry(final YubiKeyAccountValidator accountValidator) {
        super(accountValidator);
    }

    @Override
    public boolean isYubiKeyRegisteredFor(final String uid) {
        try {
            return this.entityManager.createQuery(SELECT_QUERY.concat("where r.username = :username"),
                YubiKeyAccount.class)
                .setParameter("username", uid)
                .getSingleResult() != null;
        } catch (final NoResultException e) {
            LOGGER.debug("No registration record could be found for id [{}]", uid);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean isYubiKeyRegisteredFor(final String uid, final String yubikeyPublicId) {
        try {
            return this.entityManager.createQuery(SELECT_QUERY.concat("where r.username = :username and r.publicId = :publicId"),
                YubiKeyAccount.class)
                .setParameter("username", uid)
                .setParameter("publicId", getCipherExecutor().encode(yubikeyPublicId))
                .getSingleResult() != null;
        } catch (final NoResultException e) {
            LOGGER.debug("No registration record could be found for id [{}] and public id [{}]", uid, yubikeyPublicId);
        } catch (final Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean registerAccountFor(final String uid, final String token) {
        if (accountValidator.isValid(uid, token)) {
            final String yubikeyPublicId = YubicoClient.getPublicId(token);
            final YubiKeyAccount account = new YubiKeyAccount();
            account.setPublicId(getCipherExecutor().encode(yubikeyPublicId));
            account.setUsername(uid);
            return this.entityManager.merge(account) != null;
        }
        return false;
    }

    @Override
    public Collection<YubiKeyAccount> getAccounts() {
        try {
            return this.entityManager.createQuery(SELECT_QUERY, YubiKeyAccount.class)
                    .getResultList()
                    .stream()
                    .map(it -> {
                        it.setPublicId(getCipherExecutor().decode(it.getPublicId()));
                        return it;
                    })
                    .collect(toList());
        } catch (final NoResultException e) {
            LOGGER.debug("No registration record could be found");
        } catch (final Exception e) {
            LOGGER.debug(e.getMessage(), e);
        }
        return new ArrayList<>(0);
    }
}
