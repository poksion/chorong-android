package net.poksion.chorong.android.route;

import static org.assertj.core.api.Assertions.assertThat;

import android.os.Bundle;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreImpl;
import org.junit.Before;
import org.junit.Test;

public class RouterAndPerformerTest {

    private ObjectStore objectStore;

    @Before
    public void setUp() {
        objectStore = new ObjectStoreImpl();
    }

    @Test
    public void performer_should_receive_message_on_same_state_router() {

        final Integer from = 0;
        final Integer expectedTo = 1;

        final boolean[] called = new boolean[] { false };

        Performer<Integer> performer = new Performer<Integer>() {
            @Override
            public void onNavigateTo(Integer to, Bundle bundle) {
                assertThat(to).isEqualTo(expectedTo);
                called[0] = true;
            }
        };

        Router<Integer> routerA = new Router<>("test-router");
        routerA.init(from, objectStore, performer);

        Router<Integer> routerB = new Router<>("test-router");
        routerB.init(from, objectStore, null);

        routerB.navigateTo(expectedTo);

        assertThat(called[0]).isTrue();

        called[0] = false;
        routerA.halt();

        routerB.navigateTo(expectedTo);
        assertThat(called[0]).isFalse();
    }

}
