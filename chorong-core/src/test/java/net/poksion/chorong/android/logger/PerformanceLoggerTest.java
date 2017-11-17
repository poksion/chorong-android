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
    public void checker_should_be_do_nothing_if_not_enabled() {
        PerformanceLogger logger = new PerformanceLogger(false, mockPrinter);

        PerformanceLogger.Checker checker = logger.start("dummy-check");
        assertThat(checker.end()).isZero();

        verify(mockPrinter, never()).printStarted(anyString(), anyString());
        verify(mockPrinter, never()).printEnded(anyString(), anyString(), anyLong());
    }

    @Test
    public void checker_should_be_do_nothing_if_null_name() {
        PerformanceLogger logger = new PerformanceLogger(false, mockPrinter);

        PerformanceLogger.Checker checker = logger.start(null);
        assertThat(checker.end()).isZero();

        verify(mockPrinter, never()).printStarted(anyString(), anyString());
        verify(mockPrinter, never()).printEnded(anyString(), anyString(), anyLong());
    }

    @Test
    public void print_should_be_called_when_enabled() {
        PerformanceLogger logger = new PerformanceLogger(true, mockPrinter);

        PerformanceLogger.Checker checker  = logger.start("dummy-check");
        checker.end();

        verify(mockPrinter, times(1)).printStarted(anyString(), anyString());
        verify(mockPrinter, times(1)).printEnded(anyString(), anyString(), anyLong());
    }
}
