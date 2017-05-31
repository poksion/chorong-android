package net.poksion.chorong.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferenceCounter {

    private final Map<String, Integer> counter = new HashMap<>();

    public int grab(String id) {
        int nextCnt = 1;

        Integer cnt = counter.get(id);
        if(cnt != null) {
            nextCnt = cnt + 1;
        }

        counter.put(id, nextCnt);

        return nextCnt;
    }

    public int release(String id) {
        int nextCnt = 0;

        Integer cnt = counter.get(id);
        if(cnt != null) {
            nextCnt = cnt - 1;
            if(nextCnt <= 0) {
                nextCnt = 0;
                counter.remove(id);
            } else {
                counter.put(id, nextCnt);
            }
        }

        return nextCnt;
    }

    public List<String> flatten() {
        List<String> list = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : counter.entrySet()) {
            int cnt = entry.getValue();
            for(int i = 0; i < cnt; ++i) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public void resetAll() {
        counter.clear();
    }

    public boolean isEmpty() {
        return counter.isEmpty();
    }

    public int getTotalCounts() {
        if (isEmpty()) {
            return 0;
        }

        return flatten().size();
    }
}
