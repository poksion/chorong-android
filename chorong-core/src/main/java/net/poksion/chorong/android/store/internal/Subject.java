package net.poksion.chorong.android.store.internal;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Subject {

    private Map<String, Object> container = new HashMap<>();
    private Map<String, List<WeakReference<Observer>>> observers = new HashMap<>();

    public synchronized Object get(String key) {
        return container.get(key);
    }

    public synchronized void set(String key, Object object) {
        container.put(key, object);

        List<WeakReference<Observer>> observers = this.observers.get(key);
        if (observers == null) {
            return;
        }

        for (Iterator<WeakReference<Observer>> itr = observers.iterator(); itr.hasNext(); ) {
            WeakReference<Observer> weakRefObserver = itr.next();
            Observer observer = weakRefObserver.get();
            if (observer != null) {
                observer.onObjectChanged(object);
            } else {
                itr.remove();
            }
        }
    }

    public synchronized void addWeakObserver(String key, Observer observer, boolean readExistValue) {
        List<WeakReference<Observer>> relatedObservers = observers.get(key);
        if(relatedObservers == null){
            relatedObservers = new LinkedList<>();
            observers.put(key, relatedObservers);
        }

        relatedObservers.add(new WeakReference<>(observer));

        if (readExistValue) {
            Object data = container.get(key);
            if (data != null) {
                observer.onObjectChanged(data);
            }
        }
    }

}
