package net.poksion.chorong.android.store;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectStoreAndAccessorTest {

    private final static String KEY_STR = "-dummy-key";
    private final static ObjectStore.Key KEY = new ObjectStore.Key(KEY_STR);
    private final static int DATA = 10;

    @Test
    public void store_accessor_should_be_equal_to_direct_accessing() {
        ObjectStore objectStore = new ObjectStoreImpl();
        StoreAccessor<Integer> dataAccessor = new StoreAccessor<>(KEY_STR, objectStore);

        objectStore.set(KEY, DATA);
        assertThat(objectStore.get(KEY)).isEqualTo(DATA);

        assertThat(dataAccessor.read()).isEqualTo(objectStore.get(KEY));
    }
}
