package net.poksion.chorong.android.samples;

import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreApplication;

public class App extends ObjectStoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // most case, "reset" does not need to call
        // because "App onCreate" is first call in this process
        ModuleFactory.reset();

        ModuleFactory.init(this, new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {
                ObjectStore objectStore = (ObjectStore) host;
                singletonBinder.bind(ObjectStore.class, objectStore);

                // custom key binding is possible
                singletonBinder.bind("application-object-store", objectStore);
            }
        });
    }
}
