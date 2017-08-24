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
                    if (type == String.class || type == NullSafe.String.class) {
                        String value = (String) NullSafe.getNullSafeValue(getValue(annotated.field, this));
                        bundle.putString(name, value);
                    } else if (type == int.class || type == Integer.class || type == NullSafe.Integer.class) {
                        Integer value = (Integer) NullSafe.getNullSafeValue(getValue(annotated.field, this));
                        bundle.putInt(name, value);
                    } else if (type == long.class || type == Long.class || type == NullSafe.Long.class) {
                        Long value = (Long) NullSafe.getNullSafeValue(getValue(annotated.field, this));
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
                    if (type == String.class || type == NullSafe.String.class) {
                        value = bundle.getString(name);
                        if (type == NullSafe.String.class) {
                            value = new NullSafe.String().set((String)value);
                        }
                    } else if (type == int.class || type == Integer.class || type == NullSafe.Integer.class) {
                        value = bundle.getInt(name);
                        if (type == NullSafe.Integer.class) {
                            value = new NullSafe.Integer().set((Integer)value);
                        }
                    } else if (type == long.class || type == Long.class || type == NullSafe.Long.class) {
                        value = bundle.getLong(name);
                        if (type == NullSafe.Long.class) {
                            value = new NullSafe.Long().set((Long)value);
                        }
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
