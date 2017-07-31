package net.poksion.chorong.android.task;

import java.lang.ref.WeakReference;

public abstract class SimpleTask<ResultT, ListenerT> implements Task<ListenerT> {

    protected abstract ResultT onWorkSimple();
    protected abstract void onResultSimple(ResultT result, ListenerT listener);

    @Override
    public final void onWork(ResultSender resultSender) {
        ResultT result = onWorkSimple();
        resultSender.sendResult(-1, result, true);
    }

    @Override
    public final void onResult(int resultKey, Object resultValue, WeakReference<ListenerT> resultListenerRef) {
        @SuppressWarnings("unchecked")
        ResultT result = (ResultT) resultValue;

        ListenerT listener = resultListenerRef.get();
        if (listener == null) {
            return;
        }

        onResultSimple(result, listener);
    }
}
