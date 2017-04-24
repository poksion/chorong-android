package net.poksion.chorong.android.module;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation Assemble
 * It is marked on fields to want assembling with {@link Assembler}
 * It can be defined with ID which identifying in Assembler
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Assemble {
    int value() default -1;
}
