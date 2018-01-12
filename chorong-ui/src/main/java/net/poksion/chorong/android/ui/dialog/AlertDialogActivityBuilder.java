package net.poksion.chorong.android.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.poksion.chorong.android.route.Performer;
import net.poksion.chorong.android.route.Router;
import net.poksion.chorong.android.store.ObjectStore;

public class AlertDialogActivityBuilder {

    enum EndPoint {
        DIALOG,
        DIALOG_YES,
        DIALOG_NO,
        DIALOG_CLOSE
    }

    public static abstract class OnClickCallback implements Performer<EndPoint> {
        public void onNavigateTo(EndPoint to, Bundle bundle) {
            if (to == EndPoint.DIALOG_YES) {
                onClick(true);
            } else if (to == EndPoint.DIALOG_NO) {
                onClick(false);
            }
        }

        protected abstract void onClick(boolean yes);
    }

    public static final class EventRouter extends Router<EndPoint> {
        private boolean closeControlling = false;

        private EventRouter() {
            super("alert-dialog-activity");
        }

        public void closeDialog() {
            if (closeControlling) {
                navigateTo(EndPoint.DIALOG_CLOSE);
            }
        }
    }

    public static EventRouter makeEventRouter(ObjectStore objectStore, OnClickCallback clickCallback) {
        EventRouter eventRouter = new EventRouter();
        eventRouter.init(objectStore, EndPoint.DIALOG, clickCallback);

        return eventRouter;
    }

    private final Context context;
    private final String title;
    private final String body;

    private String yes;
    private String no;

    private boolean closeControlling = false;
    private EventRouter eventRouter;

    private boolean newTask = false;
    private boolean reordering = false;

    private boolean noDimming = false;

    public AlertDialogActivityBuilder(Context context, String title, String body) {
        this.context = context;

        this.title = title;
        this.body = body;
    }

    public AlertDialogActivityBuilder clickable(String yes, String no, EventRouter eventRouter) {
        this.yes = yes;
        this.no = no;
        this.eventRouter = eventRouter;
        return this;
    }

    public AlertDialogActivityBuilder closeControlling() {
        closeControlling = true;
        return this;
    }

    public AlertDialogActivityBuilder newTask() {
        newTask = true;
        return this;
    }

    public AlertDialogActivityBuilder reordering() {
        reordering = true;
        return this;
    }

    public AlertDialogActivityBuilder noDimming() {
        noDimming = true;
        return this;
    }

    public void startDialogActivity() {

        final Intent i;
        if (noDimming) {
            i = new Intent(context, AlertDialogNoDimmingActivity.class);
        } else {
            i = new Intent(context, AlertDialogActivity.class);
        }

        i.putExtra("title", title);
        i.putExtra("body", body);

        if (eventRouter != null) {
            i.putExtra("yes", yes);
            i.putExtra("no", no);
            i.putExtra("click-event-key", true);

            if (closeControlling) {
                eventRouter.closeControlling = true;
                i.putExtra("close-event-key", true);
            }
        }

        if (newTask) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (reordering) {
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }

        context.startActivity(i);
    }
}
