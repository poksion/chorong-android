package net.poksion.chorong.android.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import android.content.Context;
import android.content.SharedPreferences;
import net.poksion.chorong.android.store.ObjectStoreApplication;
import net.poksion.chorong.android.store.StoreAccessor;
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
    private static final String PREF_KEY_STR_PREV = "pref-key-str-prev";

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

        StoreAccessor<String> storeAccessor = sharedPrefProxyManager.installStringPrefProxy(PREF_KEY_STR);
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

        StoreAccessor<Long> longAccessor = sharedPrefProxyManager.installPrefProxy(PREF_KEY_LONG, Result.Primitive.LONG);
        StoreAccessor<Integer> intAccessor = sharedPrefProxyManager.installPrefProxy(PREF_KEY_INT, Result.Primitive.INT);
        StoreAccessor<Boolean> boolAccessor = sharedPrefProxyManager.installPrefProxy(PREF_KEY_BOOLEAN, Result.Primitive.BOOLEAN);

        assertThat(longAccessor.read()).isEqualTo(111L);
        assertThat(intAccessor.read()).isEqualTo(222);
        assertThat(boolAccessor.read()).isEqualTo(true);

        longAccessor.write(333L);
        intAccessor.write(444);
        boolAccessor.write(false);

        assertThat(longAccessor.read()).isEqualTo(333L);
        assertThat(intAccessor.read()).isEqualTo(444);
        assertThat(boolAccessor.read()).isEqualTo(false);
    }

    @Test
    public void data_migrated_if_target_data_is_null() {
        SharedPreferences sp = getRobolectricSharedPreferences();
        sp.edit().putString(PREF_KEY_STR, null).apply();
        sp.edit().putString(PREF_KEY_STR_PREV, "prev-data").apply();

        StoreAccessor<String> storeAccessor = sharedPrefProxyManager.installPrefProxy(
                PREF_KEY_STR,
                Result.Primitive.STRING,
                PREF_NAME,
                PREF_KEY_STR_PREV);

        assertThat(storeAccessor.read()).isEqualTo("prev-data");
    }
}
