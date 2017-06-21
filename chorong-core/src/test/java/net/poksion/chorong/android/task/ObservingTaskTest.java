package net.poksion.chorong.android.task;

import static org.assertj.core.api.Assertions.assertThat;

import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreImpl;
import net.poksion.chorong.android.store.StoreAccessor;
import org.junit.Before;
import org.junit.Test;

public class ObservingTaskTest {
    private ObjectStore.Key storeKey = new ObjectStore.Key("observing-task-test");
    private ObjectStore objectStore;
    private StoreAccessor<String> storeAccessor;

    private final Object dummyListener = new Object();
    private TaskRunner<Object> taskRunner;

    @Before
    public void setUp() {
        objectStore = new ObjectStoreImpl();
        storeAccessor = new StoreAccessor<>(storeKey, objectStore);

        taskRunner = new TaskRunnerSync<>(dummyListener);
    }

    @Test
    public void observing_task_should_know_related_store_changed() {
        taskRunner.registerObservingTask(new ObservingTask<String, Object>() {
            @Override
            public ObjectStore getStore() {
                return objectStore;
            }

            @Override
            public String getStoreKey() {
                return storeKey.staticKey;
            }

            @Override
            public boolean isAvailable(Object listener) {
                assertThat(listener).isEqualTo(dummyListener);
                return true;
            }

            @Override
            public void onChanged(String s, Object listener) {
                assertThat(listener).isEqualTo(dummyListener);

                String stored = storeAccessor.read();
                assertThat(stored).isEqualTo("observing-test");
                assertThat(s).isEqualTo(stored);
            }
        });

        storeAccessor.write("observing-test");
    }

}
