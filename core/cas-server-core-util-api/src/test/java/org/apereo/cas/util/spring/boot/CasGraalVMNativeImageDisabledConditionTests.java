package org.apereo.cas.util.spring.boot;

import org.apereo.cas.util.nativex.CasRuntimeHintsRegistrar;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link CasGraalVMNativeImageDisabledConditionTests}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
@Tag("Simple")
class CasGraalVMNativeImageDisabledConditionTests {
    @Test
    void verifyNotInNativeImage() throws Throwable {
        val condition = new CasGraalVMNativeImageDisabledCondition();
        assertTrue(condition.getMatchOutcome(mock(ConditionContext.class), mock(AnnotatedTypeMetadata.class)).isMatch());
    }

    @Test
    void verifyInNativeImage() throws Throwable {
        System.setProperty(CasRuntimeHintsRegistrar.SYSTEM_PROPERTY_SPRING_AOT_PROCESSING, "true");
        val condition = new CasGraalVMNativeImageDisabledCondition();
        assertFalse(condition.getMatchOutcome(mock(ConditionContext.class), mock(AnnotatedTypeMetadata.class)).isMatch());
    }
}
