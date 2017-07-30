package net.poksion.chorong.android.module;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public abstract class ViewModuleAssembler implements Assembler {

    public interface Provider {
        boolean isMatchedField(Class<?> filedClass);
        Object provide(int id);
    }

    protected final class Factory {
        private List<Provider> providers = new ArrayList<>();

        public void addProvider(Provider provider) {
            providers.add(provider);
        }
    }

    protected abstract void onInit(Factory factory);

    private final View containerView;
    private final Activity containerActivity;
    private final Factory factory = new Factory();

    protected ViewModuleAssembler(@Nullable View containerView, @Nullable Activity containerActivity) {
        this.containerView = containerView;
        this.containerActivity = containerActivity;

        onInit(factory);
    }

    @Override
    public Object findModule(Class<?> filedClass, int id) {

        for (Provider provider : factory.providers) {
            if (provider.isMatchedField(filedClass)) {
                return provider.provide(id);
            }
        }

        if (id <= 0) {
            return ModuleFactory.get(filedClass.getName());
        }

        Object module = null;

        if (View.class.isAssignableFrom(filedClass)) {

            if (containerView != null) {
                module = containerView.findViewById(id);
            }

            if (containerActivity != null && module == null) {
                module = containerActivity.findViewById(id);
            }
        }

        return module;
    }
}
