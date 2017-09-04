package net.poksion.chorong.android.module;

public abstract class ModuleAssembler implements Assembler {

    @Override
    public Object findModule(Class<?> fieldClass, int id) {
        return ModuleFactory.get(fieldClass.getName());
    }

}
