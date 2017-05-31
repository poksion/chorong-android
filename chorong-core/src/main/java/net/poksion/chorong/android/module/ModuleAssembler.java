package net.poksion.chorong.android.module;

public abstract class ModuleAssembler implements Assembler {

    @Override
    public Object findModule(Class<?> filedClass, int id) {
        return ModuleFactory.get(filedClass.getName());
    }

}
