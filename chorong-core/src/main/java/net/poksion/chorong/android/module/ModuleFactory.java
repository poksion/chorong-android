package net.poksion.chorong.android.module;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ModuleFactory {

    public final static class SingletonBinder {
        private Map<String, Object> moduleMap;

        public void bind(Class<?> representType, Object singletonInstance) {
            moduleMap.put(representType.getName(), singletonInstance);
        }

        public void bind(String customBindingKey, Object singletonInstance) {
            moduleMap.put(customBindingKey, singletonInstance);
        }
    }

    public interface Initializer {
        void onInit(Object host, SingletonBinder singletonBinder);
    }

    private static Map<String, Object> modules = new ConcurrentHashMap<>();

    private ModuleFactory() {

    }

    public static void reset() {
        modules.clear();
    }

    public static void init(Object host, Initializer initializer) {
        if (!modules.isEmpty()) {
            return;
        }

        SingletonBinder singletonBinder = new SingletonBinder();
        singletonBinder.moduleMap = modules;

        initializer.onInit(host, singletonBinder);

        singletonBinder.moduleMap = null;
    }

    public static Object get(String name) {
        return modules.get(name);
    }

    public static void assemble(Object host, Assembler assembler) {
        for(Field field : assembler.getClass().getDeclaredFields()){
            assemble(assembler, field, getAssembleAnnotation(field), assembler);
        }

        for(Field field : host.getClass().getDeclaredFields()){
            assemble(host, field, getAssembleAnnotation(field), assembler);
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

    private static void assemble(Object host, Field field, Assemble assembleAnnotation, Assembler assembler) {
        if (assembleAnnotation == null) {
            return;
        }

        Class<?> filedClass = field.getType();
        int id = assembleAnnotation.value();

        Object module = assembler.findModule(filedClass, id);
        if (module == null) {
            throw new AssertionError("Fail finding module : " + filedClass.getName() + ", (id:" + id + ")");
        }

        try {
            assembler.setField(field, host, module);
        } catch(IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

}
