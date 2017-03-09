package net.poksion.chorong.android.task;

import java.lang.ref.WeakReference;

public abstract class SimpleTask<T_Result, T_Listener> implements Task<T_Listener> {

    protected abstract T_Result onWorkSimple();
    protected abstract void onResultSimple(T_Result result, T_Listener listener);

    @Override
    public final void onWork(ResultSender resultSender) {
        T_Result result = onWorkSimple();
        resultSender.sendResult(-1, result, true);
    }

    @Override
    public final void onResult(int resultKey, Object resultValue, WeakReference<T_Listener> resultListenerRef) {
        @SuppressWarnings("unchecked")
        T_Result result = (T_Result) resultValue;

        T_Listener listener = resultListenerRef.get();
        if (listener == null) {
            return;
        }

        onResultSimple(result, listener);
    }
}
