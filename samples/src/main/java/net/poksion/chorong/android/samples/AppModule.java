package net.poksion.chorong.android.samples;

import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.samples.domain.DbRepository;
import net.poksion.chorong.android.samples.domain.SampleItemRepository;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreApplication;
import net.poksion.chorong.android.store.persistence.DatabaseProxyManager;

class AppModule implements ModuleFactory.Initializer {

    @Override
    public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {
        ObjectStoreApplication objectStoreApplication = (ObjectStoreApplication) host;

        singletonBinder.bind(ObjectStore.class, objectStoreApplication);

        // custom key binding is also possible
        singletonBinder.bind("application-object-store", objectStoreApplication);

        DatabaseProxyManager dbProxyManager = new DatabaseProxyManager(objectStoreApplication, DbRepository.DB_NAME, DbRepository.DB_SCHEMES);
        singletonBinder.bind(DatabaseProxyManager.class, dbProxyManager);

        SampleItemRepository sampleItemRepository = new SampleItemRepository(dbProxyManager, objectStoreApplication);
        singletonBinder.bind(SampleItemRepository.class, sampleItemRepository);
    }
}
