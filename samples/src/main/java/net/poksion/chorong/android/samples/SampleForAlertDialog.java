package net.poksion.chorong.android.samples;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreObserver;
import net.poksion.chorong.android.ui.dialog.AlertDialogActivity;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class SampleForAlertDialog extends ToolbarActivity {

    private final String ALERT_DIALOG_CLICKED_EVENT = "alert-dialog-click-event";

    @Assemble LinearLayout buttonContainer;
    @Assemble ObjectStore objectStore;

    // Store Observer
    // CAUTION
    // In Object Store, the observer reference is managed "weak".
    // Hence should manage the observer reference.
    @SuppressWarnings("FieldCanBeLocal")
    private StoreObserver<Boolean> alertDialogBtnObserver;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        ModuleFactory.assemble(this, new SampleAssembler(this, container));

        registerAlertDialogCallback();
        addAlertDialogOpener();
    }

    private void registerAlertDialogCallback() {
        alertDialogBtnObserver = new StoreObserver<Boolean>() {
            @Override
            protected void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(SampleForAlertDialog.this, "Yes clicked", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SampleForAlertDialog.this, "No clicked", Toast.LENGTH_LONG).show();
                }
            }
        };

        objectStore.addWeakObserver(ALERT_DIALOG_CLICKED_EVENT, alertDialogBtnObserver, false);
    }

    private void addAlertDialogOpener() {
        Button button = new Button(this);
        button.setText(R.string.button_open_alert_dialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogActivity.Builder dialogBuilder = new AlertDialogActivity.Builder(
                        SampleForAlertDialog.this,
                        "Sample Alert",
                        "Sample Alert Dialog Body");

                Intent i = dialogBuilder
                        .clickable(
                                "yes button",
                                "no button",
                                ALERT_DIALOG_CLICKED_EVENT)
                        .build();

                startActivity(i);
            }
        });

        buttonContainer.addView(button);
    }

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.SKY;
    }
}
