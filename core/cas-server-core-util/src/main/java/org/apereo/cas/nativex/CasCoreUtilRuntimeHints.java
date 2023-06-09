package org.apereo.cas.nativex;

import org.apereo.cas.util.CasVersion;
import org.apereo.cas.util.cipher.JsonWebKeySetStringCipherExecutor;
import org.apereo.cas.util.cipher.RsaKeyPairCipherExecutor;
import org.apereo.cas.util.nativex.CasRuntimeHintsRegistrar;
import org.apereo.cas.util.serialization.ComponentSerializationPlanConfigurer;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.web.cors.CorsConfigurationSource;

import java.lang.module.Configuration;
import java.lang.module.ResolvedModule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * This is {@link CasCoreUtilRuntimeHints}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
public class CasCoreUtilRuntimeHints implements CasRuntimeHintsRegistrar {
    private static final int GROOVY_DGM_CLASS_COUNTER = 1500;

    @Override
    public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
        hints.resources().registerType(CasVersion.class);

        hints.proxies()
            .registerJdkProxy(ComponentSerializationPlanConfigurer.class)
            .registerJdkProxy(InitializingBean.class)
            .registerJdkProxy(Supplier.class)
            .registerJdkProxy(Runnable.class)
            .registerJdkProxy(Function.class)
            .registerJdkProxy(Consumer.class)
            .registerJdkProxy(CorsConfigurationSource.class);

        hints.serialization()
            .registerType(Boolean.class)
            .registerType(Double.class)
            .registerType(Integer.class)
            .registerType(Long.class)
            .registerType(String.class)

            .registerType(ZonedDateTime.class)
            .registerType(LocalDateTime.class)
            .registerType(LocalDate.class)
            .registerType(ZoneId.class)
            .registerType(ZoneOffset.class)

            .registerType(ArrayList.class)
            .registerType(Vector.class)
            .registerType(CopyOnWriteArrayList.class)
            .registerType(LinkedList.class)

            .registerType(HashMap.class)
            .registerType(LinkedHashMap.class)
            .registerType(ConcurrentHashMap.class)
            .registerType(TreeMap.class)

            .registerType(ConcurrentSkipListSet.class)
            .registerType(HashSet.class)
            .registerType(LinkedHashSet.class)
            .registerType(CopyOnWriteArraySet.class)
            .registerType(TreeSet.class)

            .registerType(TypeReference.of("java.time.Clock$SystemClock"))
            .registerType(TypeReference.of("java.time.Clock$OffsetClock"))
            .registerType(TypeReference.of("java.time.Clock$FixedClock"))
            .registerType(TypeReference.of("java.lang.String$CaseInsensitiveComparator"));

        registerDeclaredMethod(hints, Map.Entry.class, "getKey");
        registerDeclaredMethod(hints, Map.Entry.class, "getValue");
        registerDeclaredMethod(hints, Map.class, "isEmpty");

        hints.reflection()
            .registerType(Map.Entry.class,
                MemberCategory.INTROSPECT_PUBLIC_METHODS,
                MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.INTROSPECT_PUBLIC_METHODS)

            .registerType(TypeReference.of("java.util.LinkedHashMap$Entry"), MemberCategory.INTROSPECT_PUBLIC_METHODS)
            .registerType(TypeReference.of("java.util.TreeMap$Entry"), MemberCategory.INTROSPECT_PUBLIC_METHODS)
            .registerType(LinkedHashMap.class, MemberCategory.INTROSPECT_DECLARED_METHODS, MemberCategory.DECLARED_FIELDS)
            .registerType(TypeReference.of("java.util.HashMap$Node"))
            .registerType(TypeReference.of("java.util.HashMap$TreeNode"))
            .registerType(HashMap.class,
                MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS)
            .registerType(AbstractCollection.class,
                MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.INTROSPECT_PUBLIC_METHODS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.INVOKE_DECLARED_METHODS)
            .registerType(AbstractMap.class,
                MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.INTROSPECT_PUBLIC_METHODS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.INVOKE_DECLARED_METHODS)
            .registerType(Callable.class,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.INTROSPECT_PUBLIC_METHODS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS)
            .registerType(Map.class,
                MemberCategory.INTROSPECT_DECLARED_METHODS,
                MemberCategory.INTROSPECT_PUBLIC_METHODS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS)

            .registerType(TypeReference.of("java.time.Ser"),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS)

            .registerType(TypeReference.of("java.time.Clock$SystemClock"),
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
                MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS)

            .registerType(CasVersion.class, MemberCategory.INVOKE_DECLARED_METHODS)

            .registerType(Module.class, MemberCategory.INVOKE_DECLARED_METHODS)
            .registerType(Class.class, MemberCategory.INVOKE_DECLARED_METHODS)
            .registerType(ModuleLayer.class, MemberCategory.INVOKE_DECLARED_METHODS)
            .registerType(Configuration.class, MemberCategory.INVOKE_DECLARED_METHODS)
            .registerType(ResolvedModule.class, MemberCategory.INVOKE_DECLARED_METHODS)
            .registerType(ServiceLoader.class, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_PUBLIC_METHODS)
            .registerType(System.class, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.PUBLIC_FIELDS)

            .registerType(PersistenceAnnotationBeanPostProcessor.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(ConfigurationClassPostProcessor.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(EventListenerMethodProcessor.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(DefaultEventListenerFactory.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(AutowiredAnnotationBeanPostProcessor.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(CommonAnnotationBeanPostProcessor.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)

            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.PSW", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.PSWMS", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.PSAMS", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSLA", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSA", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSS", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSLMSW", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSMS", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSMW", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSMSA", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSLMSA", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSMSA", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSMSR", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSMSW", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        IntStream.range(1, GROOVY_DGM_CLASS_COUNTER).forEach(idx ->
            hints.reflection().registerTypeIfPresent(classLoader, "org.codehaus.groovy.runtime.dgm$" + idx,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.PUBLIC_FIELDS));

        hints.reflection()
            .registerType(TypeReference.of("groovy.lang.GroovyClassLoader"),
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS)
            .registerType(TypeReference.of("java.util.Stack"),
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        
        List.of(
                RsaKeyPairCipherExecutor.class.getName(),
                JsonWebKeySetStringCipherExecutor.class.getName(),
                "org.codehaus.groovy.transform.StaticTypesTransformation",
                "groovy.lang.Script",
                "org.slf4j.LoggerFactory",
                "nonapi.io.github.classgraph.classloaderhandler.AntClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.ClassGraphClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry",
                "nonapi.io.github.classgraph.classloaderhandler.CxfContainerClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.EquinoxClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.EquinoxContextFinderClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.FallbackClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.FelixClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.JBossClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.JPMSClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.OSGiDefaultClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.ParentLastDelegationOrderTestClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.PlexusClassWorldsClassRealmClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.QuarkusClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.SpringBootRestartClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.TomcatWebappClassLoaderBaseHandler",
                "nonapi.io.github.classgraph.classloaderhandler.UnoOneJarClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.URLClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.WeblogicClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.WebsphereLibertyClassLoaderHandler",
                "nonapi.io.github.classgraph.classloaderhandler.WebsphereTraditionalClassLoaderHandler")
            .forEach(clazz -> hints.reflection().registerTypeIfPresent(classLoader, clazz,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.PUBLIC_FIELDS));
    }
}
