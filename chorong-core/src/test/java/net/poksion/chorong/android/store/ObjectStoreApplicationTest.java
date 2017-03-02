package net.poksion.chorong.android.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ObjectStoreApplicationTest {

    private ObjectStoreApplication objectStoreApplication;

    // observer mostly make as the field (since managed as weak reference)
    @SuppressWarnings("FieldCanBeLocal")
    private StoreObserver<String> spyObserver;

    @Mock private ObjectStore.PersistenceProxy mockPersistenceProxy;

    @Before
    public void setUp() {
        objectStoreApplication = new ObjectStoreApplication() {};
    }

    @Test
    public void object_store_can_be_set_get_and_observable() {
        final String[] observed = { "" };
        spyObserver = new StoreObserver<String>() {
            @Override
            protected void onChanged(String s) {
                observed[0] = s;
            }
        };

        ObjectStore.Key storeKey = new ObjectStore.Key("dummy-key");
        objectStoreApplication.addWeakObserver(storeKey.staticKey, spyObserver, false);

        objectStoreApplication.set(storeKey, "dummy-data");

        assertThat(observed[0]).isEqualTo("dummy-data");
        assertThat(observed[0]).isEqualTo(objectStoreApplication.get(storeKey));
    }

    @Test
    public void persistence_data_can_deal_with_proxy() {
        ObjectStore.Key storeKey = new ObjectStore.Key("dummy-key");

        objectStoreApplication.setPersistenceProxy(storeKey.staticKey, mockPersistenceProxy);

        objectStoreApplication.set(storeKey, "dummy-data");

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockPersistenceProxy, times(1)).setData(anyString(), argumentCaptor.capture());

        String value = argumentCaptor.getValue();
        assertThat(value).isEqualTo("dummy-data");

        when(mockPersistenceProxy.getData(anyString())).thenReturn("dummy-data");
        Object stored = objectStoreApplication.get(storeKey);
        assertThat(stored).isEqualTo("dummy-data");
    }

}
