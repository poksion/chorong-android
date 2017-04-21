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
import net.poksion.chorong.android.ui.dialog.AlertDialogActivity;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class SampleForAlertDialog extends ToolbarActivity {

    @Assemble LinearLayout buttonContainer;
    @Assemble ObjectStore objectStore;

    private AlertDialogActivity.EventRouter eventRouter;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        ModuleFactory.assemble(this, new SampleAssembler<>(this, container));

        addAlertDialogOpener();
    }

    @Override
    protected void onDestroy() {
        eventRouter.halt();

        super.onDestroy();
    }

    private void addAlertDialogOpener() {
        eventRouter = AlertDialogActivity.makeEventRouter(objectStore, new AlertDialogActivity.OnClickCallback() {
            @Override
            protected void onClick(boolean yes) {
                if (isFinishing()) {
                    return;
                }

                if (yes) {
                    Toast.makeText(SampleForAlertDialog.this, "Yes clicked", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SampleForAlertDialog.this, "No clicked", Toast.LENGTH_LONG).show();
                }

            }
        });

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
                                eventRouter)
                        .build();

                startActivity(i);
            }
        });

        buttonContainer.addView(button);
    }

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.DARK;
    }
}
