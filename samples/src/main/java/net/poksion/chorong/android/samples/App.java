package net.poksion.chorong.android.samples;

import com.facebook.stetho.Stetho;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.store.ObjectStoreApplication;

public class App extends ObjectStoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // in most case, "reset" does not need to call
        // because "App onCreate" is first call in this process
        ModuleFactory.reset();

        ModuleFactory.init(this, new AppModule());

        // for debugging
        Stetho.initializeWithDefaults(this);
    }
}
