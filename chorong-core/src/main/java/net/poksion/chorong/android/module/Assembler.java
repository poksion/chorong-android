package net.poksion.chorong.android.module;

import java.lang.reflect.Field;

/**
 * Assembler. It is working with {@link ModuleFactory} and {@link Assemble} annotation.
 */
public interface Assembler {

    /**
     * findModule is called in ModuleFactory on fields marked {@link Assemble}
     * It is possible to pass ID (integer value) when marking Assemble
     *
     * @param filedClass the filed type marked Assemble
     * @param id the Assemble ID. default value is -1
     * @return Module that assembling on the field (marked with {@link Assemble} annotation
     */
    Object findModule(Class<?> filedClass, int id);

    /**
     * Since the assembler set the instance to filed using reflection,
     * the assembler should have right to access the fields.
     *
     * Most implementing looks like
     *   filed.set(object, value)
     *
     * @param filed the Field (reflection instance) to set the instance.
     * @param object the Instance having field
     * @param value the Instance (maybe module) to want to set in field
     * @throws IllegalAccessException when do not have right setting filed (as same as reflection)
     */
    void setField(Field filed, Object object, Object value) throws IllegalAccessException;

}
