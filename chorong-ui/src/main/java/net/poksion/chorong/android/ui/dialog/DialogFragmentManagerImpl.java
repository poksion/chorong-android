package net.poksion.chorong.android.ui.dialog;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.poksion.chorong.android.util.ReferenceCounter;

public class DialogFragmentManagerImpl<T extends Enum<T>> implements DialogFragmentManager<T> {

    private static final String STATEFUL_DIALOG_TAG = "stateful_dialog";

    private final class StatefulRequest {
        private final T dialogType;
        private final String id;
        private final String title;
        private final String message;

        private StatefulRequest(T dialogType, String id, String title, String message) {
            this.dialogType = dialogType;
            this.id = id;
            this.title = title;
            this.message = message;
        }
    }

    private final DelayRunner delayRunner;

    private final List<DialogFragmentHost<T>> visibleFragmentHosts = new LinkedList<>();

    private final Map<State, Set<State>> stateTransitionTable = new HashMap<>();
    private final List<StatefulRequest> statefulRequestQueue = new LinkedList<>();

    private State state = State.NORMAL;

    private final Map<T, ReferenceCounter> statelessGroupRefCounters = new HashMap<>();

    public DialogFragmentManagerImpl(final Looper looper, final long delayTime) {
        this(new DelayRunner() {
            private final Handler handler = new Handler(looper);

            @Override
            public void runDelayed(Runnable runnable) {
                handler.postDelayed(runnable, delayTime);
            }
        });
    }

    public DialogFragmentManagerImpl(DelayRunner delayRunner) {
        this.delayRunner = delayRunner;

        buildTransitTable();
    }

    @Override
    public void notifyOnResume(DialogFragmentHost<T> dialogFragmentHost) {
        visibleFragmentHosts.add(dialogFragmentHost);

        showStatelessDialogIfRemainingRefCounter();
        showStatefulDialogIfShowingState();
    }

    @Override
    public void notifyOnPause(DialogFragmentHost<T> dialogFragmentHost) {
        visibleFragmentHosts.remove(dialogFragmentHost);
    }

    @Override
    public void requestWaitingState(T dialogType, String id, String title, String message) {
        statefulRequestQueue.add(new StatefulRequest(dialogType, id, title, message));
        tryToWaiting();
    }

    @Override
    public void requestNormalState(String id) {
        Iterator<StatefulRequest> iterator = statefulRequestQueue.iterator();
        while(iterator.hasNext()) {
            StatefulRequest request = iterator.next();
            if(id.equals(request.id)) {
                iterator.remove();
            }
        }

        tryToNormal();
    }

    @Override
    public void requestShowingState(T dialogType, String title, String message) {
        statefulRequestQueue.add(new StatefulRequest(dialogType, "", title, message));
        tryToShowing();
    }

    @Override
    public void setStatelessDialog(T dialogType) {
        statelessGroupRefCounters.put(dialogType, new ReferenceCounter());
    }

    @Override
    public void showStatelessDialog(T dialogType, String id) {
        ReferenceCounter refCounter = statelessGroupRefCounters.get(dialogType);
        refCounter.grab(id);

        showDialog(dialogType.name(), dialogType, id, "");
    }

    @Override
    public void hideStatelessDialog(T dialogType, String id) {
        ReferenceCounter refCounter = statelessGroupRefCounters.get(dialogType);
        refCounter.release(id);

        if (refCounter.isEmpty()) {
            hideDialog(dialogType.name());
        }
    }

    private void buildTransitTable() {
        Set<State> fromNormal = new HashSet<>();
        fromNormal.add(State.WAITING);
        fromNormal.add(State.SHOWING);
        stateTransitionTable.put(State.NORMAL, fromNormal);

        Set<State> fromWaiting = new HashSet<>();
        fromWaiting.add(State.NORMAL);
        fromWaiting.add(State.SHOWING);
        stateTransitionTable.put(State.WAITING, fromWaiting);

        stateTransitionTable.put(State.SHOWING, new HashSet<State>());
    }

    private void tryToWaiting() {
        if(!stateTransitionTable.get(state).contains(State.WAITING)) {
            return;
        }

        state = State.WAITING;

        delayRunner.runDelayed(new Runnable() {
            @Override
            public void run() {
                tryToShowing();
            }
        });
    }

    private void tryToNormal() {
        if(!stateTransitionTable.get(state).contains(State.NORMAL)) {
            return;
        }

        if(statefulRequestQueue.isEmpty()) {
            state = State.NORMAL;
        }
    }

    private void tryToShowing() {
        if(!stateTransitionTable.get(state).contains(State.SHOWING)) {
            return;
        }

        if(!statefulRequestQueue.isEmpty()) {
            state = State.SHOWING;

            showStatefulDialogIfShowingState();
        }
    }

    private void showStatelessDialogIfRemainingRefCounter() {
        for (Map.Entry<T, ReferenceCounter> entry : statelessGroupRefCounters.entrySet()) {
            T dialogType = entry.getKey();
            String tag = entry.getKey().name();
            if (entry.getValue().isEmpty()) {
                hideDialog(tag);
            } else {
                Set<String> requested = new HashSet<>();
                for (String id : entry.getValue().flatten()) {
                    if (requested.contains(id)) {
                        continue;
                    }
                    requested.add(id);
                    showDialog(tag, dialogType, id, "");
                }
            }
        }
    }

    private void showStatefulDialogIfShowingState() {
        if(state != State.SHOWING || statefulRequestQueue.isEmpty()) {
            hideDialog(STATEFUL_DIALOG_TAG);
            return;
        }

        final StatefulRequest request = statefulRequestQueue.get(0);
        showDialog(STATEFUL_DIALOG_TAG, request.dialogType, request.title, request.message);
    }

    private void showDialog(String tag, T dialogType, String title, String message) {
        if (visibleFragmentHosts.isEmpty()) {
            return;
        }

        DialogFragmentHost<T> fragmentHost = visibleFragmentHosts.get(0);
        FragmentManager fragmentManager = fragmentHost.getSupportFragmentManager();
        DialogFragment prevDialog = (DialogFragment) fragmentManager.findFragmentByTag(tag);

        if (isFragmentAdded(prevDialog) && isDialogShowing(prevDialog.getDialog())) {
            return;
        }

        if(prevDialog != null) {
            prevDialog.dismissAllowingStateLoss();
        }

        fragmentHost.newDialogFragment(dialogType, title, message).show(fragmentManager, tag);
    }

    private void hideDialog(String tag) {
        if (visibleFragmentHosts.isEmpty()) {
            return;
        }

        DialogFragmentHost<T> fragmentHost = visibleFragmentHosts.get(0);
        FragmentManager fragmentManager = fragmentHost.getSupportFragmentManager();
        DialogFragment prevDialog = (DialogFragment) fragmentManager.findFragmentByTag(tag);
        if (prevDialog != null) {
            prevDialog.dismissAllowingStateLoss();
        }
    }

    private boolean isFragmentAdded(DialogFragment fragment) {
        return fragment != null && fragment.isAdded();
    }

    private boolean isDialogShowing(Dialog dialog) {
        return dialog != null && dialog.isShowing();
    }

}
