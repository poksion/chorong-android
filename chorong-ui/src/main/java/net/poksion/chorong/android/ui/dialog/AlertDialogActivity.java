package net.poksion.chorong.android.ui.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import net.poksion.chorong.android.route.Performer;
import net.poksion.chorong.android.route.Router;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.ui.R;

public class AlertDialogActivity extends Activity {

    private enum EndPoint {
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

    public static class EventRouter extends Router<EndPoint> {
        private EventRouter(String routingKey) {
            super(routingKey);
        }

        public void closeDialog() {
            navigateTo(EndPoint.DIALOG_CLOSE);
        }
    }

    public static EventRouter makeEventRouter(ObjectStore objectStore, OnClickCallback clickCallback) {
        EventRouter eventRouter = new EventRouter("alert-dialog-activity");
        eventRouter.init(objectStore, EndPoint.DIALOG, clickCallback);

        return eventRouter;
    }

    public static class Builder {
        private final Intent i;

        public Builder(Context context, String title, String body) {
            i = new Intent(context, AlertDialogActivity.class);

            i.putExtra("title", title);
            i.putExtra("body", body);
        }

        public Builder clickable(String yes, String no, EventRouter eventRouter) {
            if (eventRouter == null) {
                throw new IllegalArgumentException("clickable needs event router");
            }

            i.putExtra("yes", yes);
            i.putExtra("no", no);
            i.putExtra("click-event-key", true);
            return this;
        }

        public Builder closeable() {
            i.putExtra("close-event-key", true);
            return this;
        }

        public Builder asNewTask() {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return this;
        }

        public Intent build() {
            return i;
        }

    }

    private EventRouter eventRouter;
    private boolean closeableFromOthers = false;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 11){
            setTheme(android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        Intent i = getIntent();
        builder.setTitle(i.getStringExtra("title"));
        builder.setMessage(i.getStringExtra("body"));

        updateFromIntent(i, getApplication());

        // yes button
        String yes = i.getStringExtra("yes");
        if(yes != null){
            builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateClickEvent(true);
                    finishDialog();
                }
            });
        }

        // no button
        String no = i.getStringExtra("no");
        if(no != null){
            builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateClickEvent(false);
                    finishDialog();
                }
            });
        }

        AlertDialog alert = builder.create();

        //noinspection ConstantConditions
        alert.getWindow().setWindowAnimations(R.style.AlertDialogNoAnimation);

        if (closeableFromOthers) {
            setFinishOnTouchOutside(false);
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
        } else {
            setFinishOnTouchOutside(true);
            alert.setCanceledOnTouchOutside(true);
            alert.setCancelable(true);
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finishDialog();
                }
            });
        }

        alert.show();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!closeableFromOthers) {
            finishDialog();
        }
    }

    @Override
    protected void onDestroy() {
        if (eventRouter != null) {
            eventRouter.halt();
        }

        super.onDestroy();
    }

    private void updateFromIntent(Intent i, Application application) {
        closeableFromOthers = i.getBooleanExtra("close-event-key", false);

        if (!(application instanceof ObjectStore)) {
            return;
        }

        OnClickCallback callbackListenable = null;
        if (closeableFromOthers) {
            callbackListenable = new OnClickCallback() {

                @Override
                public void onNavigateTo(EndPoint to, Bundle bundle) {
                    if (to == EndPoint.DIALOG_CLOSE) {
                        finishDialog();
                    }
                }

                @Override
                protected void onClick(boolean yes) {}
            };
        }

        ObjectStore objectStore = (ObjectStore) application;
        eventRouter = makeEventRouter(objectStore, callbackListenable);
    }

    private void updateClickEvent(boolean yes) {
        if (eventRouter == null) {
            return;
        }

        EndPoint endPoint = yes? EndPoint.DIALOG_YES : EndPoint.DIALOG_NO;
        eventRouter.navigateTo(endPoint);
    }

    private void finishDialog() {
        if (isFinishing()) {
            return;
        }

        setResult(RESULT_CANCELED);
        finish();
    }
}
