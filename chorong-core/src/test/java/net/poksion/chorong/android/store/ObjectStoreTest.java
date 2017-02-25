package net.poksion.chorong.android.store;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectStoreTest {

    private final static String KEY_1 = "-key-1";
    private final static int data1 = 10;

    private final static String KEY_2 = "-key-2";
    private final static String data2 = "test-value-2";

    private final static String KEY_3 = "-key-3";
    private final static String data3 = "test-value-for-not-typed-key";

    @Test
    public void testEmptyDataContainer() {
        ObjectStore objectStore = new ObjectStoreImpl();
        StoreAccessor<Integer> data1Accessor = new StoreAccessor<>(KEY_1, objectStore);
        objectStore.set(new ObjectStore.Key(KEY_1), data1);
        assertThat(data1Accessor.read()).isEqualTo(data1);
    }
}
