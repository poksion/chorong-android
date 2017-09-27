package net.poksion.chorong.android.store.persistence;

import net.poksion.chorong.android.store.ObjectStore;

public abstract class ProxyManager {
    private final ObjectStore objectStore;

    protected ProxyManager(ObjectStore objectStore) {
        this.objectStore = objectStore;
    }

    public ObjectStore getRelatedObjectStore() {
        return objectStore;
    }

}
