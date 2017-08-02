package net.poksion.chorong.android.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private final static class AssemblingField {
        private final Field field;
        private final int id;

        private AssemblingField(Field field, int id) {
            this.field = field;
            this.id = id;
        }
    }

    private final static Map<String, Object> MODULES = new ConcurrentHashMap<>();
    private final static Map<String, List<AssemblingField>> CACHED_ASSEMBLE_FIELDS = new ConcurrentHashMap<>();

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
            assemble(assembler, getAssembleFields(assembleOwnerClass), assembler);
            assembleOwnerClass = assembleOwnerClass.getSuperclass();
        }

        assembleOwnerClass = host.getClass();
        Class<?> hostParentClass = hostClass.getSuperclass();
        while (assembleOwnerClass != hostParentClass) {
            assemble(host, getAssembleFields(assembleOwnerClass), assembler);
            assembleOwnerClass = assembleOwnerClass.getSuperclass();
        }
    }

    private static List<AssemblingField> getAssembleFields(Class<?> assembleOwnerClass) {
        List<AssemblingField> cachedFields = CACHED_ASSEMBLE_FIELDS.get(assembleOwnerClass.getName());
        if (cachedFields == null) {
            cachedFields = new ArrayList<>();
            for (Field field : assembleOwnerClass.getDeclaredFields()) {
                Assemble assembleAnnotation = getAssembleAnnotation(field);
                if (assembleAnnotation != null) {
                    cachedFields.add(new AssemblingField(field, assembleAnnotation.value()));
                }
            }
            CACHED_ASSEMBLE_FIELDS.put(assembleOwnerClass.getName(), cachedFields);
        }

        return cachedFields;
    }

    private static void assemble(Object host, List<AssemblingField> assemblingFields, Assembler assembler) {
        for (AssemblingField assemblingField : assemblingFields) {

            Class<?> filedClass = assemblingField.field.getType();

            Object module = assembler.findModule(filedClass, assemblingField.id);
            if (module == null) {
                try {
                    Object member = assemblingField.field.get(host);
                    if (member != null) {
                        return;
                    }
                } catch(IllegalAccessException ignored) {}

                String message =
                        "Fail finding module for : " +
                        filedClass.getName() + ", (id:" + assemblingField.id + ") " +
                        "Check findModule method in Assembler or " +
                        "order of ModuleFactory.assemble (The subclass should call it before)";

                throw new RuntimeException(message);
            }

            try {
                assembler.setField(assemblingField.field, host, module);
            } catch(IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Assemble getAssembleAnnotation(Field field) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            if (annotation instanceof Assemble) {
                return (Assemble)annotation;
            }
        }

        return null;
    }

}
