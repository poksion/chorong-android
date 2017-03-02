package net.poksion.chorong.android.store;

import net.poksion.chorong.android.store.internal.Subject;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SubjectAndStoreObserverTest {

    private final String key1 = "test-key-1";
    private final String key2 = "test-key-2";

    private final String value1 = "test-value-1";
    private final String value2 = "test-value-2";

    @Test
    public void subject_should_store_values() {
        Subject container = new Subject();

        container.set(key1, value1);
        container.set(key2, value2);

        assertThat(container.get(key1)).isEqualTo(value1);
        assertThat(container.get(key2)).isEqualTo(value2);
    }

    @Test
    public void observer_should_read_existed_value_if_requested() {
        Subject subscriber = new Subject();
        subscriber.set(key1, value1);

        final boolean[] valueChecked = new boolean[1];
        valueChecked[0] = false;
        StoreObserver<String> readIfExistObserver = new StoreObserver<String>() {
            @Override
            protected void onChanged(String s) {
                assertThat(s).isEqualTo(value1);
                valueChecked[0] = true;
            }
        };
        subscriber.addWeakObserver(key1, readIfExistObserver, true);

        assertThat(valueChecked[0]).isTrue();

        valueChecked[0] = false;
        StoreObserver<String> notReadExistObserver = new StoreObserver<String>() {
            @Override
            protected void onChanged(String s) {
                fail("not here");
            }
        };
        subscriber.addWeakObserver(key1, notReadExistObserver, false);

        assertThat(valueChecked[0]).isFalse();
    }

    @Test
    public void observer_should_be_notified_on_data_changed() {
        Subject observableContainer = new Subject();
        observableContainer.set(key1, value1);

        final boolean[] valueChecked = new boolean[1];
        valueChecked[0] = false;
        StoreObserver<String> observer = new StoreObserver<String>() {
            @Override
            protected void onChanged(String s) {
                assertThat(s).isEqualTo(value2);
                valueChecked[0] = true;
            }
        };

        observableContainer.addWeakObserver(key1, observer, false);
        assertThat(valueChecked[0]).isFalse();

        observableContainer.set(key1, value2);
        assertThat(valueChecked[0]).isTrue();
        valueChecked[0] = false;

        // change key2
        // since there is not observer for key2,
        // key1 related observer is not notified
        observableContainer.set(key2, value2);
        assertThat(valueChecked[0]).isFalse();

        // remove observer
        // not receive message (weak key1 observer removed)
        //noinspection UnusedAssignment
        observer = null;
        System.gc();

        observableContainer.set(key1, value1);
        assertThat(valueChecked[0]).isFalse();
    }
}
