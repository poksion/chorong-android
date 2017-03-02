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

    private final ObjectStore.Key storeKey = new ObjectStore.Key("dummy-key");

    private ObjectStoreApplication objectStoreApplication;

    private String observedValue;
    private StoreObserver<String> spyObserver;


    @Mock private ObjectStore.PersistenceProxy mockPersistenceProxy;

    @Before
    public void setUp() {
        objectStoreApplication = new ObjectStoreApplication() {};

        observedValue = null;
        spyObserver = new StoreObserver<String>() {
            @Override
            protected void onChanged(String s) {
                observedValue = s;
            }
        };
    }

    @Test
    public void object_store_can_be_set_get_and_observable() {

        objectStoreApplication.addWeakObserver(storeKey.staticKey, spyObserver, false);

        objectStoreApplication.set(storeKey, "dummy-data");
        assertThat(objectStoreApplication.get(storeKey)).isEqualTo("dummy-data");

        assertThat(observedValue).isEqualTo("dummy-data");
    }

    @Test
    public void persistence_data_can_deal_with_proxy() {

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

    @Test
    public void persistence_data_also_can_be_observable() {
        when(mockPersistenceProxy.getData(anyString())).thenReturn("dummy-data");
        objectStoreApplication.setPersistenceProxy(storeKey.staticKey, mockPersistenceProxy);

        objectStoreApplication.addWeakObserver(storeKey.staticKey, spyObserver, true);
        assertThat(observedValue).isEqualTo("dummy-data");

        assertThat(objectStoreApplication.get(storeKey)).isEqualTo("dummy-data");
    }

}
