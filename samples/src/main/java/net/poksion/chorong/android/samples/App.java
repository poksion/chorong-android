package net.poksion.chorong.android.samples;

import java.util.Map;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreApplication;

public class App extends ObjectStoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        ModuleFactory.init(this, new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, Map<String, Object> moduleMap) {

                // hosted first argument of init method
                App appAsHost = (App) host;
                moduleMap.put(ObjectStore.class.getName(), appAsHost);
            }
        });
    }
}
