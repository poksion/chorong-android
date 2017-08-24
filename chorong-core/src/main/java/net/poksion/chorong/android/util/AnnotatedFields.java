package net.poksion.chorong.android.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AnnotatedFields<IdT> {
    public final static class Annotated<IdT> {
        public final Field field;
        public final IdT id;

        public Annotated(Field field, IdT id) {
            this.field = field;
            this.id = id;
        }
    }

    protected abstract Annotated<IdT> provideAnnotated(Annotation annotation, Field field);

    private final Map<String, List<Annotated<IdT>>> CLASS_CACHED = new ConcurrentHashMap<>();

    public List<Annotated<IdT>> getAnnotatedFields(Class<?> ownerClass) {
        List<Annotated<IdT>> ownerClassCached = CLASS_CACHED.get(ownerClass.getName());
        if (ownerClassCached == null) {
            ownerClassCached = new ArrayList<>();
            for (Field field : ownerClass.getDeclaredFields()) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    Annotated<IdT> annotatedField = provideAnnotated(annotation, field);
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
