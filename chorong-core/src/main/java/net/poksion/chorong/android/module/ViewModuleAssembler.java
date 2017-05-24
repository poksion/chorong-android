package net.poksion.chorong.android.module;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

public abstract class ViewModuleAssembler implements Assembler {

    private final View containerView;
    private final Activity containerActivity;

    protected ViewModuleAssembler(@Nullable View containerView, @Nullable Activity containerActivity) {
        this.containerView = containerView;
        this.containerActivity = containerActivity;
    }

    @Override
    public Object findModule(Class<?> filedClass, int id) {
        if (id <= 0) {
            return ModuleFactory.get(filedClass.getName());
        }

        if (!View.class.isAssignableFrom(filedClass)) {
            return null;
        }

        if (containerView != null) {
            return containerView.findViewById(id);
        }

        if (containerActivity != null) {
            return containerActivity.findViewById(id);
        }

        return null;
    }
}
