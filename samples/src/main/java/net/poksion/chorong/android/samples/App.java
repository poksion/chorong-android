package net.poksion.chorong.android.samples;

import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreApplication;

public class App extends ObjectStoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        ModuleFactory.init(this, new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {

                ObjectStore objectStore = (ObjectStore) host;
                singletonBinder.bind(ObjectStore.class, objectStore);
            }
        });
    }
}
