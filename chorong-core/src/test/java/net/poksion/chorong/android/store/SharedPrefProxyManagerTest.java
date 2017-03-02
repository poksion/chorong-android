package net.poksion.chorong.android.store;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.content.SharedPreferences;
import net.poksion.chorong.android.store.persistence.SharedPrefProxyManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class SharedPrefProxyManagerTest {

    private static final String PREF_NAME = "app-data-container";
    private static final String PREF_KEY = "recent-visit-list";

    private SharedPrefProxyManager sharedPrefProxyManager;

    @Before
    public void setUp() {
        ObjectStoreApplication objectStoreApplication = new ObjectStoreApplication() {
            @Override
            public SharedPreferences getSharedPreferences(String name, int mode) {
                return getRobolectricSharedPreferences();
            }
        };
        sharedPrefProxyManager = new SharedPrefProxyManager(objectStoreApplication);
    }

    private SharedPreferences getRobolectricSharedPreferences() {
        return RuntimeEnvironment.application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Test
    public void persistence_data_should_be_applied_by_proxy() {
        SharedPreferences sp = getRobolectricSharedPreferences();
        sp.edit().putString(PREF_KEY, "aaa").apply();

        StoreAccessor<String> storeAccessor = sharedPrefProxyManager.installProxy(PREF_KEY, SharedPrefProxyManager.DataType.STRING);
        assertThat(storeAccessor.read()).isEqualTo("aaa");

        storeAccessor.write("bbb");
        assertThat(sp.getString(PREF_KEY, null)).isEqualTo("bbb");

        sp.edit().putString(PREF_KEY, null).apply();
    }
}
