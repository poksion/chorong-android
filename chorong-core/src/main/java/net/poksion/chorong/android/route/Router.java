package net.poksion.chorong.android.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreObserver;

public class Router<N> {

    private static class PerformCmd<N> {
        private N from;
        private N to;
        private Bundle bundle;
    }

    private final ObjectStore.Key routingKey;

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

    public Router(String routingKey) {
        this.routingKey = new ObjectStore.Key(routingKey);
    }

    public void init(ObjectStore objectStore, N current) {
        this.objectStore = objectStore;
        this.current = current;

        objectStore.addWeakObserver(routingKey.staticKey, storeObserver, false);
    }

    public void setPerformer(Performer<N> performer) {
        this.performer = performer;
    }

    public void halt() {
        objectStore.removeWeakObserver(routingKey.staticKey, storeObserver);

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

        objectStore.set(routingKey, performCmd);
    }
}
