package net.poksion.chorong.android.logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PerformanceLoggerTest {
    private PerformanceLogger.Printer mockPrinter;

    @Before
    public void setUp() {
        mockPrinter = mock(PerformanceLogger.Printer.class);
    }

    @Test
    public void starting_id_should_be_zero_if_not_enabled() {
        PerformanceLogger logger = new PerformanceLogger(false, mockPrinter);

        long startingId = logger.start("dummy-check");
        assertThat(startingId).isZero();

        long endingElapsed = logger.end(startingId);
        assertThat(endingElapsed).isZero();

        verify(mockPrinter, never()).printStarted(anyString(), anyString());
    }

    @Test
    public void print_should_be_called_when_enabled() {
        PerformanceLogger logger = new PerformanceLogger(true, mockPrinter);

        long startingId = logger.start("dummy-check");
        logger.end(startingId);

        verify(mockPrinter, times(1)).printStarted(anyString(), anyString());
        verify(mockPrinter, times(1)).printEnded(anyString(), anyString(), anyLong());
    }

    @Test
    public void staring_time_can_be_duplicate() {
        PerformanceLogger logger = new PerformanceLogger(true);

        long staringTime = 10;
        logger.pushName(staringTime, "dummy-1");
        logger.pushName(staringTime, "dummy-2");

        String name = logger.popName(staringTime);
        assertThat(name).isEqualTo("dummy-2");

        name = logger.popName(staringTime);
        assertThat(name).isEqualTo("dummy-1");
    }

}
