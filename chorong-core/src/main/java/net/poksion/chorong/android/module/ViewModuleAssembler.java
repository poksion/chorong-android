package net.poksion.chorong.android.module;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ViewModuleAssembler implements Assembler {

    public interface Provider {
        boolean isMatchedField(Class<?> fieldClass);
        Object provide(int id);
    }

    public static abstract class IndexedProvider<T> {
        protected abstract T provide();

        private String getIndexName() {
            Type superclass = getClass().getGenericSuperclass();
            Type type = ((ParameterizedType)superclass).getActualTypeArguments()[0];
            if (type instanceof ParameterizedType) {
                throw new IllegalArgumentException( "The index class(" + type.toString() + ") do not permit generic");
            }
            return type.toString().replace("class ", "");
        }
    }

    protected final class Factory {
        private final List<Provider> providers = new ArrayList<>();
        private final Map<String, IndexedProvider<?>> indexedProviders = new HashMap<>();

        public void addProvider(Provider provider) {
            providers.add(provider);
        }

        public <T> void addIndexedProvider(IndexedProvider<T> provider) {
            indexedProviders.put(provider.getIndexName(), provider);
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
    public Object findModule(Class<?> fieldClass, int id) {
        IndexedProvider<?> indexedProvider = factory.indexedProviders.get(fieldClass.getName());
        if (indexedProvider != null) {
            return indexedProvider.provide();
        }

        Object module = (id == -1) ? ModuleFactory.get(fieldClass.getName()) : null;
        if (module != null) {
            return module;
        }

        for (Provider provider : factory.providers) {
            if (provider.isMatchedField(fieldClass)) {
                return provider.provide(id);
            }
        }

        if (View.class.isAssignableFrom(fieldClass)) {
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
