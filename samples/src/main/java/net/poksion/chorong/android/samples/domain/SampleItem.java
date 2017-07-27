package net.poksion.chorong.android.samples.domain;

import net.poksion.chorong.android.task.ObservingBuffer;

public class SampleItem implements ObservingBuffer.Unique {
    public String id;
    public String name;
    public String date;

    @Override
    public String getId() {
        return id;
    }
}
