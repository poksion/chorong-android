package net.poksion.chorong.android.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AnnotatedFields {
    public final static class Annotated {
        public final Field field;
        public final int id;

        public Annotated(Field field, int id) {
            this.field = field;
            this.id = id;
        }
    }

    protected abstract Annotated provideAnnotated(Annotation annotation, Field field);

    private final Map<String, List<Annotated>> CLASS_CACHED = new ConcurrentHashMap<>();

    public List<Annotated> getAnnotatedFields(Class<?> ownerClass) {
        List<Annotated> ownerClassCached = CLASS_CACHED.get(ownerClass.getName());
        if (ownerClassCached == null) {
            ownerClassCached = new ArrayList<>();
            for (Field field : ownerClass.getDeclaredFields()) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    Annotated annotatedField = provideAnnotated(annotation, field);
                    if (annotatedField != null) {
                        ownerClassCached.add(annotatedField);
                    }
                }
            }
            CLASS_CACHED.put(ownerClass.getName(), ownerClassCached);
        }

        return ownerClassCached;
    }
}
