package net.poksion.chorong.android.logger;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PerformanceLogger {

    public interface Printer {
        void print(String tag, String message);
    }


    private final boolean enabled;
    private final Printer printer;

    private final Map<Long, List<String>> logStack = new ConcurrentHashMap<>();

    public PerformanceLogger(boolean enabled) {
        this(enabled, new Printer() {
            @Override
            public void print(String tag, String message) {
                Log.d(tag, message);
            }
        });
    }

    public PerformanceLogger(boolean enabled, Printer printer) {
        this.enabled = enabled;
        this.printer = printer;
    }

    public long start(String name) {
        if (!enabled) {
            return 0;
        }

        long current = System.currentTimeMillis();
        List<String> names = logStack.get(current);
        if (names == null) {
            names = new ArrayList<>();
            logStack.put(current, names);
        }
        names.add(name);

        printer.print("[performance]", name + " started");
        return current;
    }

    public long end(long startingId) {
        if (!enabled) {
            return 0;
        }

        long elapsed = System.currentTimeMillis() - startingId;

        List<String> names = logStack.get(startingId);
        int lastIdx = names.size()-1;
        String name = names.get(lastIdx);

        if (lastIdx == 0) {
            logStack.remove(startingId);
        } else {
            names.remove(lastIdx);
        }

        printer.print("[performance]", name + " ended (" + elapsed + "ms)");
        return elapsed;
    }

}
