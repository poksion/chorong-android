package net.poksion.chorong.android.bundle;

import android.os.Bundle;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import net.poksion.chorong.android.util.AnnotatedFields;

public abstract class Bundling {

    protected abstract Object getValue(Field field, Object object) throws IllegalArgumentException, IllegalAccessException;
    protected abstract void setValue(Field filed, Object object, Object value) throws IllegalAccessException;

    private final static AnnotatedFields<String> ANNOTATED_FIELDS = new AnnotatedFields<String>() {
        @Override
        protected Annotated<String> provideAnnotated(Annotation annotation, Field field) {
            if (annotation instanceof Member) {
                Member member = (Member) annotation;
                return new Annotated<>(field, member.value());
            }

            return null;
        }
    };

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        try {
            Class<?> ownerClass = getClass();
            while(ownerClass != Bundling.class) {
                for(AnnotatedFields.Annotated<String> annotated : ANNOTATED_FIELDS.getAnnotatedFields(getClass())) {
                    String name = getName(annotated);
                    Class<?> type = annotated.field.getType();
                    if (type == String.class) {
                        String value = (String) getValue(annotated.field, this);
                        bundle.putString(name, value);
                    } else if (type == int.class || type == Integer.class) {
                        Integer value = (Integer) getValue(annotated.field, this);
                        bundle.putInt(name, value);
                    } else if (type == long.class || type == Long.class) {
                        Long value = (Long) getValue(annotated.field, this);
                        bundle.putLong(name, value);
                    } else {
                        throw new UnsupportedOperationException("cannot bundling");
                    }
                }
                ownerClass = ownerClass.getSuperclass();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return bundle;
    }

    public void fromBundle(Bundle bundle) {
        try {
            Class<?> ownerClass = getClass();
            while(ownerClass != Bundling.class) {
                for(AnnotatedFields.Annotated<String> annotated : ANNOTATED_FIELDS.getAnnotatedFields(getClass())) {
                    String name = getName(annotated);
                    Class<?> type = annotated.field.getType();
                    Object value;
                    if (type == String.class) {
                        value = bundle.getString(name);
                    } else if (type == int.class || type == Integer.class) {
                        value = bundle.getInt(name);
                    } else if (type == long.class || type == Long.class) {
                        value = bundle.getLong(name);
                    } else {
                        throw new UnsupportedOperationException("cannot un-bundling");
                    }

                    setValue(annotated.field, this, value);
                }
                ownerClass = ownerClass.getSuperclass();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getName(AnnotatedFields.Annotated<String> annotated) {
        if (annotated.id.length() > 0) {
            return annotated.id;
        }
        return annotated.field.getName();
    }

}
