package net.poksion.chorong.android.task;

import java.lang.ref.WeakReference;

public interface Task<T_Listener> {

    interface ResultSender {
        void sendResult(int resultId, Object resultValue, boolean lastResult);
    }

    void onWork(ResultSender resultSender);
    void onResult(int resultKey, Object resultValue, WeakReference<T_Listener> resultListenerRef);
}
