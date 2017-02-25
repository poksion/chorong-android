package net.poksion.chorong.android.module;

import net.poksion.chorong.android.annotation.Assemble;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ModuleFactory {

    public interface Initializer {
        void onInit(Object host, Map<String, Object> moduleMap);
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

        initializer.onInit(host, modules);
    }

    public static Object get(String name) {
        return modules.get(name);
    }

    public static void assemble(Object host, Assembler assembler) {
        for(Field field : assembler.getClass().getDeclaredFields()){
            assemble(assembler, field, getAssemble(field), assembler);
        }

        for(Field field : host.getClass().getDeclaredFields()){
            assemble(host, field, getAssemble(field), assembler);
        }
    }

    private static Assemble getAssemble(Field field) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            if (annotation instanceof Assemble) {
                return (Assemble)annotation;
            }
        }

        return null;
    }

    private static void assemble(Object host, Field field, Assemble assemble, Assembler assembler) {
        if (assemble == null) {
            return;
        }

        Class<?> filedClass = field.getType();
        int id = assemble.value();

        Object module = assembler.findModule(filedClass, id);
        if (module == null) {
            throw new AssertionError("Fail finding module");
        }

        try {
            assembler.setField(field, host, module);
        } catch(IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

}
