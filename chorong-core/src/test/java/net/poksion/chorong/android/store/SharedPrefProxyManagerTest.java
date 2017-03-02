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

    private static final String PREF_NAME = "pref-name";

    private static final String PREF_KEY_STR = "pref-key-str";
    private static final String PREF_KEY_LONG = "pref-key-long";
    private static final String PREF_KEY_INT = "pref-key-int";
    private static final String PREF_KEY_BOOLEAN = "pref-key-boolean";

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
        sp.edit().putString(PREF_KEY_STR, "aaa").apply();

        StoreAccessor<String> storeAccessor = sharedPrefProxyManager.installProxy(PREF_KEY_STR, SharedPrefProxyManager.DataType.STRING);
        assertThat(storeAccessor.read()).isEqualTo("aaa");

        storeAccessor.write("bbb");
        assertThat(sp.getString(PREF_KEY_STR, null)).isEqualTo("bbb");
    }

    @Test
    public void shared_pref_can_be_stored_long_int_boolean() {
        SharedPreferences sp = getRobolectricSharedPreferences();
        sp.edit().putLong(PREF_KEY_LONG, 111L).apply();
        sp.edit().putInt(PREF_KEY_INT, 222).apply();
        sp.edit().putBoolean(PREF_KEY_BOOLEAN, true).apply();

        StoreAccessor<Long> longAccessor = sharedPrefProxyManager.installProxy(PREF_KEY_LONG, SharedPrefProxyManager.DataType.LONG);
        StoreAccessor<Integer> intAccessor = sharedPrefProxyManager.installProxy(PREF_KEY_INT, SharedPrefProxyManager.DataType.INT);
        StoreAccessor<Boolean> boolAccessor = sharedPrefProxyManager.installProxy(PREF_KEY_BOOLEAN, SharedPrefProxyManager.DataType.BOOLEAN);

        assertThat(longAccessor.read()).isEqualTo(111L);
        assertThat(intAccessor.read()).isEqualTo(222);
        assertThat(boolAccessor.read()).isEqualTo(true);

    }
}
