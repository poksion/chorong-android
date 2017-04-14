package net.poksion.chorong.android.ui.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreObserver;

public class Router<N> {

    private static final ObjectStore.Key PERFORM_CMD_KEY = new ObjectStore.Key("router-perform-cmd");

    private static class PerformCmd<N> {
        private N from;
        private N to;
        private Bundle bundle;
    }

    private ObjectStore objectStore;
    private N current;

    private Performer<N> performer;

    private StoreObserver<PerformCmd<N>> storeObserver = new StoreObserver<PerformCmd<N>>() {
        @Override
        protected void onChanged(PerformCmd<N> performCmd) {
            if (performer == null || !current.equals(performCmd.from)) {
                return;
            }

            performer.onNavigateTo(performCmd.to, performCmd.bundle);
        }
    };

    public void init(ObjectStore objectStore, N current) {
        this.objectStore = objectStore;
        this.current = current;

        objectStore.addWeakObserver(PERFORM_CMD_KEY.staticKey, storeObserver, false);
    }

    public void halt() {
        objectStore = null;
        performer = null;
    }

    public void navigateTo(N to) {
        navigateTo(to, null);
    }

    public void navigateTo(N to, @Nullable Bundle bundle) {
        if (objectStore == null) {
            return;
        }

        PerformCmd<N> performCmd = new PerformCmd<>();

        performCmd.from = current;
        performCmd.to = to;
        performCmd.bundle = bundle;

        objectStore.set(PERFORM_CMD_KEY, performCmd);
    }

    public void setPerformer(Performer<N> performer) {
        this.performer = performer;
    }
}
