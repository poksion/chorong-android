package net.poksion.chorong.android.module;

import android.app.Activity;
import android.view.View;

public abstract class ViewModuleAssembler implements Assembler {

    private final Activity activity;
    private final View view;

    public ViewModuleAssembler(Activity activity) {
        this.activity = activity;
        this.view = null;
    }

    public ViewModuleAssembler(View view) {
        this.activity = null;
        this.view = view;
    }

    @Override
    public Object findModule(Class<?> filedClass, int id) {
        if (id <= 0) {
            return ModuleFactory.get(filedClass.getName());
        }

        if (!View.class.isAssignableFrom(filedClass)) {
            return null;
        }

        if (activity != null) {
            return activity.findViewById(id);
        }

        if (view != null) {
            return view.findViewById(id);
        }

        return null;
    }
}
