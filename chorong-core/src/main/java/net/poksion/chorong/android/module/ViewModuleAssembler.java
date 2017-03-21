package net.poksion.chorong.android.module;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

public abstract class ViewModuleAssembler implements Assembler {

    private final View resIdView;
    private final Activity resIdActivity;

    protected ViewModuleAssembler(@Nullable View resIdView, @Nullable Activity resIdActivity) {
        this.resIdView = resIdView;
        this.resIdActivity = resIdActivity;
    }

    @Override
    public Object findModule(Class<?> filedClass, int id) {
        if (id <= 0) {
            return ModuleFactory.get(filedClass.getName());
        }

        if (!View.class.isAssignableFrom(filedClass)) {
            return null;
        }

        if (resIdView != null) {
            return resIdView.findViewById(id);
        }

        if (resIdActivity != null) {
            return resIdActivity.findViewById(id);
        }

        return null;
    }
}
