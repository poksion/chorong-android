package net.poksion.chorong.android.store.persistence;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import android.support.annotation.Nullable;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreApplication;
import net.poksion.chorong.android.store.StoreAccessor;

public class SharedPrefProxyManager extends ProxyManager {

    private static class SharedPrefProxy implements ObjectStore.PersistenceProxy {

        private final SharedPreferences sharedPreferences;
        private final String sharedPrefDataKey;
        private final Result.Primitive type;

        private SharedPrefProxy(SharedPreferences sharedPreferences, String sharedPrefDataKey, Result.Primitive type) {
            this.sharedPreferences = sharedPreferences;
            this.sharedPrefDataKey = sharedPrefDataKey;
            this.type = type;
        }

        @Override
        public void setData(String conditions, Object data) {
            switch (type) {
                case STRING:
                    sharedPreferences.edit().putString(sharedPrefDataKey, (String)data).apply();
                    break;
                case LONG:
                    sharedPreferences.edit().putLong(sharedPrefDataKey, (Long)data).apply();
                    break;
                case INT:
                    sharedPreferences.edit().putInt(sharedPrefDataKey, (Integer)data).apply();
                    break;
                case BOOLEAN:
                    sharedPreferences.edit().putBoolean(sharedPrefDataKey, (Boolean)data).apply();
                    break;
            }
        }

        @Override
        public Object getData(String conditions) {
            Object value = null;
            switch (type) {
                case STRING:
                    value = sharedPreferences.getString(sharedPrefDataKey, null);
                    break;
                case LONG:
                    value = sharedPreferences.getLong(sharedPrefDataKey, 0);
                    break;
                case INT:
                    value = sharedPreferences.getInt(sharedPrefDataKey, 0);
                    break;
                case BOOLEAN:
                    value = sharedPreferences.getBoolean(sharedPrefDataKey, false);
                    break;
            }
            return value;
        }

        private boolean hasData() {
            return sharedPreferences.contains(sharedPrefDataKey);
        }
    }

    private final static String SHARED_PREF_KEY = "app-data-container";
    private final Application application;
    private final SharedPreferences sharedPreferences;

    public SharedPrefProxyManager(ObjectStoreApplication objectStoreApplication) {
        this(objectStoreApplication, objectStoreApplication);
    }

    public SharedPrefProxyManager(ObjectStore objectStore, Application application) {
        this(objectStore, application, SHARED_PREF_KEY);
    }

    public SharedPrefProxyManager(ObjectStore objectStore, Application application, String prefName) {
        super(objectStore);

        this.application = application;
        this.sharedPreferences = application.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public StoreAccessor<String> installStringPrefProxy(String sharedPrefKey) {
        return installPrefProxy(sharedPrefKey, Result.Primitive.STRING);
    }

    public <T> StoreAccessor<T> installPrefProxy(String sharedPrefKey, Result.Primitive type) {
        return installPrefProxy(sharedPrefKey, type, null, null);
    }

    public <T> StoreAccessor<T> installPrefProxy(
            String sharedPreKey,
            Result.Primitive type,
            @Nullable String prevPrefName,
            @Nullable String prevPrefKey) {

        SharedPrefProxy proxy = new SharedPrefProxy(sharedPreferences, sharedPreKey, type);

        ObjectStore objectStore = getRelatedObjectStore();
        objectStore.setPersistenceProxy(sharedPreKey, proxy);

        StoreAccessor<T> storeAccessor = new StoreAccessor<>(sharedPreKey, objectStore);

        if (prevPrefName != null && prevPrefKey != null && !proxy.hasData()) {
            migratePrefData(storeAccessor, prevPrefName, prevPrefKey, type);
        }

        return storeAccessor;
    }

    @SuppressWarnings("unchecked")
    private <T> void migratePrefData(StoreAccessor<T> storeAccessor, String prevPrefName, String prevPrefKey, Result.Primitive type) {
        SharedPreferences sharedPrefForMigration = application.getSharedPreferences(prevPrefName, Context.MODE_PRIVATE);
        SharedPrefProxy proxyForMigration = new SharedPrefProxy(sharedPrefForMigration, prevPrefKey, type);
        Object prevData = proxyForMigration.getData(null);
        storeAccessor.write((T)prevData);
    }
}
