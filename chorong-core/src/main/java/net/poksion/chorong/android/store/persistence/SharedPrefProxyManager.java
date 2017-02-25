package net.poksion.chorong.android.store.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import net.poksion.chorong.android.annotation.Nullable;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreApplication;
import net.poksion.chorong.android.store.StoreAccessor;

public class SharedPrefProxyManager {

    public enum DataType {
        STRING,
        LONG,
        INT,
        BOOLEAN
    }

    private static class SharedPrefProxy implements ObjectStore.PersistenceProxy {

        private final SharedPreferences sharedPreferences;
        private final String sharedPrefDataKey;
        private final DataType dataType;

        private SharedPrefProxy(SharedPreferences sharedPreferences, String sharedPrefDataKey, DataType dataType) {
            this.sharedPreferences = sharedPreferences;
            this.sharedPrefDataKey = sharedPrefDataKey;
            this.dataType = dataType;
        }

        @Override
        public void setData(String conditions, Object data) {
            switch (dataType) {
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
            switch (dataType) {
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
    private ObjectStoreApplication objectStoreApplication;
    private SharedPreferences sharedPreferences;

    public SharedPrefProxyManager(ObjectStoreApplication objectStoreApplication) {
        this(objectStoreApplication, SHARED_PREF_KEY);
    }

    public SharedPrefProxyManager(ObjectStoreApplication objectStoreApplication, String prefName) {
        this.objectStoreApplication = objectStoreApplication;
        this.sharedPreferences = objectStoreApplication.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public <T> StoreAccessor<T> installProxy(String sharedPreKey, DataType dataType) {
        return installProxy(sharedPreKey, dataType, null, null);
    }

    public <T> StoreAccessor<T> installProxy(
            String sharedPreKey,
            DataType dataType,
            @Nullable String prevPrefName,
            @Nullable String prevPrefKey) {

        SharedPrefProxy proxy = new SharedPrefProxy(sharedPreferences, sharedPreKey, dataType);
        objectStoreApplication.setPersistenceProxy(sharedPreKey, proxy);

        StoreAccessor<T> storeAccessor = new StoreAccessor<>(sharedPreKey, objectStoreApplication);

        if (prevPrefName != null && prevPrefKey != null && !proxy.hasData()) {
            migratePrefData(storeAccessor, prevPrefName, prevPrefKey, dataType);
        }

        return storeAccessor;
    }

    @SuppressWarnings("unchecked")
    private <T> void migratePrefData(StoreAccessor<T> storeAccessor, String prevPrefName, String prevPrefKey, DataType dataType) {
        SharedPreferences sharedPrefForMigration = objectStoreApplication.getSharedPreferences(prevPrefName, Context.MODE_PRIVATE);
        SharedPrefProxy proxyForMigration = new SharedPrefProxy(sharedPrefForMigration, prevPrefKey, dataType);
        Object prevData = proxyForMigration.getData(null);
        storeAccessor.write((T)prevData);
    }
}
