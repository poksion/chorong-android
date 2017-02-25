package net.poksion.chorong.android.task;

import java.lang.ref.WeakReference;

/**
 * Task DOES JOB on separated WORK-SPACE and RESULT-SPACE
 *   - ResultSender is the communicator between work-space and result-space
 *   - It is possible to invoke listener on result-space
 * @param <T_Listener>
 */
public interface Task<T_Listener> {

    interface ResultSender {
        void sendResult(int resultId, Object resultValue, boolean lastResult);
    }

    void onWork(ResultSender resultSender);
    void onResult(int resultKey, Object resultValue, WeakReference<T_Listener> resultListenerRef);
}
