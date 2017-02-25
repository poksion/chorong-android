package net.poksion.chorong.android.module;

import java.lang.reflect.Field;

public interface Assembler {

    Object findModule(Class<?> filedClass, int id);
    void setField(Field filed, Object object, Object value) throws IllegalAccessException;

}
