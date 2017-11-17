package net.poksion.chorong.android.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class PerformanceLogger {

    public interface Printer {
        void printStarted(String tag, String name);

        void printEnded(String tag, String name, long elapsed);
    }

    public abstract class Checker {
        final String name;
        final long started;

        private Checker(String name, long started) {
            this.name = name;
            this.started = started;
        }

        public abstract long end();
    }

    private final boolean enabled;
    private final Printer printer;

    private final Checker doNothingChecker = new Checker("", 0) {
        @Override
        public long end() {
            return 0;
        }
    };

    public PerformanceLogger(boolean enabled) {
        this(enabled, new Printer() {
            @Override
            public void printStarted(String tag, String name) {
                Log.d(tag, name + " started");
            }

            @Override
            public void printEnded(String tag, String name, long elapsed) {
                Log.d(tag, name + " ended (" + elapsed + "ms)");
            }
        });
    }

    public PerformanceLogger(boolean enabled, Printer printer) {
        this.enabled = enabled;
        this.printer = printer;
    }

    @NonNull
    public Checker start(@Nullable String name) {
        if (!enabled || name == null) {
            return doNothingChecker;
        }

        printer.printStarted("[performance]", name);

        long current = System.currentTimeMillis();
        return new Checker(name, current) {

            @Override
            public long end() {
                long elapsed = System.currentTimeMillis() - started;
                printer.printEnded("[performance]", name, elapsed);

                return elapsed;
            }
        };
    }
}
