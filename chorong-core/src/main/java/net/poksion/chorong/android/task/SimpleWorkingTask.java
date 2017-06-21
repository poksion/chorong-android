package net.poksion.chorong.android.task;

public abstract class SimpleWorkingTask<T_Listener> extends SimpleTask<Object, T_Listener> {

    protected abstract void onWork();

    @Override
    protected Object onWorkSimple() {
        onWork();
        return null;
    }

    @Override
    protected void onResultSimple(Object result, T_Listener listener) {

    }
}
