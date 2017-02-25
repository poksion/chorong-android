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

import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreAccessor;
import net.poksion.chorong.android.store.StoreObserver;
import net.poksion.chorong.android.ui.R;

public class AlertDialogActivity extends Activity {

    public static class Builder {
        private final Intent i;

        public Builder(Context context, String title, String body) {
            i = new Intent(context, AlertDialogActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            i.putExtra("title", title);
            i.putExtra("body", body);
        }

        public Builder clickable(String yes, String no, String clickEventKey) {
            i.putExtra("yes", yes);
            i.putExtra("no", no);
            i.putExtra("click-event-key", clickEventKey);
            return this;
        }

        public Builder closeable(String closeEventKey) {
            i.putExtra("close-event-key", closeEventKey);
            return this;
        }

        public Intent build() {
            return i;
        }

    }

    private StoreAccessor<Boolean> clickEventStoreAccessor;
    private StoreAccessor<Boolean> closeEventStoreAccessor;

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

        updateStoreAccessor(i, getApplication());

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

        // close event
        updateCloseEvent();

        AlertDialog alert = builder.create();

        //noinspection ConstantConditions
        alert.getWindow().setWindowAnimations(R.style.AlertDialogNoAnimation);

        if (closeEventStoreAccessor == null) {
            setFinishOnTouchOutside(true);
            alert.setCanceledOnTouchOutside(true);
            alert.setCancelable(true);
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finishDialog();
                }
            });
        } else {
            setFinishOnTouchOutside(false);
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
        }

        alert.show();
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (closeEventStoreAccessor == null) {
            finishDialog();
        }
    }

    private void updateStoreAccessor(Intent i, Application application) {
        if (!(application instanceof ObjectStore)) {
            return;
        }

        ObjectStore objectStore = (ObjectStore) application;

        String clickEventKey = i.getStringExtra("click-event-key");
        if (clickEventKey != null) {
            clickEventStoreAccessor = new StoreAccessor<>(clickEventKey, objectStore);
        }

        String closeEventKey = i.getStringExtra("close-event-key");
        if (closeEventKey != null) {
            closeEventStoreAccessor = new StoreAccessor<>(closeEventKey, objectStore);
        }
    }

    private void updateClickEvent(boolean yes) {
        if (clickEventStoreAccessor == null) {
            return;
        }

        clickEventStoreAccessor.write(yes);
    }

    private StoreObserver<Boolean> closeEventObserver = new StoreObserver<Boolean>() {
        @Override
        protected void onChanged(Boolean aBoolean) {
            if (aBoolean == null || !aBoolean) {
                return;
            }

            finishDialog();
        }
    };

    private void updateCloseEvent() {
        if (closeEventStoreAccessor == null) {
            return;
        }

        closeEventStoreAccessor.addWeakObserver(closeEventObserver, false);
    }

    private void finishDialog() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
