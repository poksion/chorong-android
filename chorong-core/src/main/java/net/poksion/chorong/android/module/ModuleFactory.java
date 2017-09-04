package net.poksion.chorong.android.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.poksion.chorong.android.util.AnnotatedFields;

/**
 * Module Factory.
 * The two important roles of Module Factory are
 *  - Initializing application modules (should be singleton)
 *  - Assembling modules to host (with {@link Assembler}
 */
public final class ModuleFactory {

    public final static class SingletonBinder {
        private Map<String, Object> moduleMap;

        public <T1, T2 extends T1> void bind(Class<T1> representType, T2 singletonInstance) {
            moduleMap.put(representType.getName(), singletonInstance);
        }

        public void bind(String customBindingKey, Object singletonInstance) {
            moduleMap.put(customBindingKey, singletonInstance);
        }
    }

    public interface Initializer {
        void onInit(Object host, SingletonBinder singletonBinder);
    }

    private final static Map<String, Object> MODULES = new ConcurrentHashMap<>();
    private final static AnnotatedFields<Integer> ANNOTATED_FIELDS = new AnnotatedFields<Integer>() {
        @Override
        protected Annotated<Integer> provideAnnotated(Annotation annotation, Field field) {
            if (annotation instanceof Assemble) {
                Assemble assemble = (Assemble)annotation;
                return new Annotated<>(field, assemble.value());
            }

            return null;
        }
    };

    private ModuleFactory() {

    }

    public static void reset() {
        MODULES.clear();
    }

    public static void init(Object host, Initializer initializer) {
        if (!MODULES.isEmpty()) {
            return;
        }

        SingletonBinder singletonBinder = new SingletonBinder();
        singletonBinder.moduleMap = MODULES;

        initializer.onInit(host, singletonBinder);

        singletonBinder.moduleMap = null;
    }

    public static Object get(String name) {
        return MODULES.get(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> moduleClass) {
        return (T) MODULES.get(moduleClass.getName());
    }

    public static <T> void assemble(Class<T> hostClass, T host, Assembler assembler) {

        Class<?> assembleOwnerClass = assembler.getClass();
        while (assembleOwnerClass != Object.class) {
            assemble(assembler, assembleOwnerClass, assembler);
            assembleOwnerClass = assembleOwnerClass.getSuperclass();
        }

        assembleOwnerClass = host.getClass();
        Class<?> hostParentClass = hostClass.getSuperclass();
        while (assembleOwnerClass != hostParentClass) {
            assemble(host, assembleOwnerClass, assembler);
            assembleOwnerClass = assembleOwnerClass.getSuperclass();
        }
    }

    private static void assemble(Object host, Class<?> assembleOwnerClass, Assembler assembler) {
        for (AnnotatedFields.Annotated<Integer> annotated : ANNOTATED_FIELDS.getAnnotatedFields(assembleOwnerClass)) {

            Class<?> fieldClass = annotated.field.getType();

            Object module = assembler.findModule(fieldClass, annotated.id);
            if (module == null) {
                try {
                    Object member = annotated.field.get(host);
                    if (member != null) {
                        return;
                    }
                } catch(IllegalAccessException ignored) {}

                String message =
                        "Fail finding module for : " +
                        fieldClass.getName() + ", (id:" + annotated.id + ") " +
                        "Check findModule method in Assembler or " +
                        "order of ModuleFactory.assemble (The subclass should call it before)";

                throw new RuntimeException(message);
            }

            try {
                assembler.setField(annotated.field, host, module);
            } catch(IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
