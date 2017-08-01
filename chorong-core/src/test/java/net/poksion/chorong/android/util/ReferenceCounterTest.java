package net.poksion.chorong.android.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Before;
import org.junit.Test;

public class ReferenceCounterTest {
    private ReferenceCounter referenceCounter;

    @Before
    public void setUp() {
        referenceCounter = new ReferenceCounter();
    }

    @Test
    public void grab_should_increase_counter() {
        int grabbed = referenceCounter.grab("dummy-1");
        assertThat(grabbed).isEqualTo(1);

        grabbed = referenceCounter.grab("dummy-1");
        assertThat(grabbed).isEqualTo(2);

        int newGrabbed = referenceCounter.grab("dummy-2");
        assertThat(newGrabbed).isEqualTo(1);
    }

    @Test
    public void release_should_decrease_counter() {
        int grabbed = referenceCounter.grab("dummy-1");
        assertThat(grabbed).isEqualTo(1);

        grabbed = referenceCounter.release("dummy-1");
        assertThat(grabbed).isEqualTo(0);
    }

    @Test
    public void released_count_should_be_at_least_zero() {
        int grabbed = referenceCounter.grab("dummy-1");
        assertThat(grabbed).isEqualTo(1);

        grabbed = referenceCounter.release("dummy-1");
        assertThat(grabbed).isEqualTo(0);

        grabbed = referenceCounter.release("dummy-1");
        assertThat(grabbed).isEqualTo(0);
    }

    @Test
    public void reset_makes_all_empty() {
        referenceCounter.grab("dummy-1");
        referenceCounter.grab("dummy-2");

        referenceCounter.resetAll();
        assertThat(referenceCounter.isEmpty()).isTrue();
    }

    @Test
    public void one_and_two_grabs_make_total_count_3() {
        referenceCounter.grab("dummy-1");
        referenceCounter.grab("dummy-2");
        referenceCounter.grab("dummy-2");

        assertThat(referenceCounter.getTotalCounts()).isEqualTo(3);

        int dummy1Cnt = 0;
        int dummy2Cnt = 0;
        for (String grabbed : referenceCounter.flatten()) {
            switch(grabbed) {
                case "dummy-1":
                    dummy1Cnt++;
                    break;
                case "dummy-2":
                    dummy2Cnt++;
                    break;
                default:
                    fail("should be dummy-1 or dummy-2");
                    break;
            }
        }
        assertThat(dummy1Cnt).isEqualTo(1);
        assertThat(dummy2Cnt).isEqualTo(2);
    }

}
