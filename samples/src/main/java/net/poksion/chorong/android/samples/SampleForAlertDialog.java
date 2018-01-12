package net.poksion.chorong.android.samples;

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
import net.poksion.chorong.android.ui.dialog.AlertDialogActivityBuilder;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class SampleForAlertDialog extends ToolbarActivity {

    @Assemble LinearLayout buttonContainer;
    @Assemble ObjectStore objectStore;

    private AlertDialogActivityBuilder.EventRouter eventRouter;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        ModuleFactory.assemble(SampleForAlertDialog.class, this, new SampleAssembler<>(this, container));

        addAlertDialogOpener();
    }

    @Override
    protected void onDestroy() {
        eventRouter.halt();

        super.onDestroy();
    }

    private void addAlertDialogOpener() {
        eventRouter = AlertDialogActivityBuilder.makeEventRouter(objectStore, new AlertDialogActivityBuilder.OnClickCallback() {
            @Override
            protected void onClick(boolean yes) {
                if (isFinishing()) {
                    return;
                }

                if (yes) {
                    Toast.makeText(SampleForAlertDialog.this, "Yes clicked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SampleForAlertDialog.this, "No clicked", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button button = new Button(this);
        button.setText(R.string.button_open_alert_dialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialogActivityBuilder(
                        SampleForAlertDialog.this,
                        "Sample Alert",
                        "Sample Alert Dialog Body")
                        .clickable(
                                "yes button",
                                "no button",
                                eventRouter)
                        .noDimming()
                        .startDialogActivity();
            }
        });

        buttonContainer.addView(button);
    }

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.DARK;
    }
}
