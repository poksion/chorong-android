package net.poksion.chorong.android.bundle;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation Annotated
 * It is marked on fields to want bundling with {@link Bundling}
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Member {

}
