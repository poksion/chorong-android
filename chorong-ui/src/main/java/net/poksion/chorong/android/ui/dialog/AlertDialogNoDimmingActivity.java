package net.poksion.chorong.android.ui.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import net.poksion.chorong.android.store.ObjectStore;

import static net.poksion.chorong.android.ui.dialog.AlertDialogActivityBuilder.makeEventRouter;

public class AlertDialogNoDimmingActivity extends Activity {

    private AlertDialogActivityBuilder.EventRouter eventRouter;
    private boolean closeControllingFromOthers = false;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
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

        if (closeControllingFromOthers) {
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

        if (!closeControllingFromOthers) {
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
        closeControllingFromOthers = i.getBooleanExtra("close-event-key", false);

        if (!(application instanceof ObjectStore)) {
            return;
        }

        AlertDialogActivityBuilder.OnClickCallback callbackListenable = null;
        if (closeControllingFromOthers) {
            callbackListenable = new AlertDialogActivityBuilder.OnClickCallback() {

                @Override
                public void onNavigateTo(AlertDialogActivityBuilder.EndPoint to, Bundle bundle) {
                    if (to == AlertDialogActivityBuilder.EndPoint.DIALOG_CLOSE) {
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

        AlertDialogActivityBuilder.EndPoint endPoint = yes? AlertDialogActivityBuilder.EndPoint.DIALOG_YES : AlertDialogActivityBuilder.EndPoint.DIALOG_NO;
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
