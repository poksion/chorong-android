package net.poksion.chorong.android.module;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ViewModuleAssembler implements Assembler {

    public interface Provider {
        boolean isMatchedField(Class<?> filedClass);
        Object provide(int id);
    }

    public interface IndexedProvider {
        Class<?> getIndexClass();
        Object provide();
    }

    protected final class Factory {
        private final List<Provider> providers = new ArrayList<>();
        private final Map<Class<?>, IndexedProvider> indexedProviders = new HashMap<>();

        public void addProvider(Provider provider) {
            providers.add(provider);
        }

        public void addIndexedProvider(IndexedProvider provider) {
            Class<?> indexClass = provider.getIndexClass();
            indexedProviders.put(indexClass, provider);
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
        IndexedProvider indexedProvider = factory.indexedProviders.get(filedClass);
        if (indexedProvider != null) {
            return indexedProvider.provide();
        }

        Object module = (id == 0) ? ModuleFactory.get(filedClass.getName()) : null;
        if (module != null) {
            return module;
        }

        for (Provider provider : factory.providers) {
            if (provider.isMatchedField(filedClass)) {
                return provider.provide(id);
            }
        }

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
