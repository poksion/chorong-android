package net.poksion.chorong.android.samples;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.StoreObserver;
import net.poksion.chorong.android.ui.dialog.AlertDialogActivity;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class MainActivity extends ToolbarActivity {

    // Assembling member with ActivityAssembler
    // 1. LinearLayout is set directly in ActivityAssembler.
    // 2. Since ActivityAssembler extends ViewModuleAssembler,
    // automatically assemble
    //   2-1. ObjectStore (provided by App-ModuleFactory.Initializer) and
    //   2-2. FrameLayout (finding in ViewModuleAssembler with assemble id)

    @Assemble LinearLayout buttonContainer;
    @Assemble ObjectStore objectStore;
    @Assemble(R.id.main_content) FrameLayout contentView;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        ModuleFactory.assemble(this, new ActivityAssembler(this, container));

        // after assembling, it is possible to access contentView
        contentView.setBackgroundColor(Color.WHITE);

        addAlertDialogSample();
        addFlatCardSample();
    }

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.SKY;
    }

    @Override
    protected List<MenuInfo> getMenuInfoList() {
        List<MenuInfo> menuInfoList = new ArrayList<>();
        menuInfoList.add(new MenuInfo(R.string.menu_info));

        return menuInfoList;
    }

    @Override
    protected boolean onMenuSelected(int id) {
        if (id == R.string.menu_info) {
            Toast.makeText(this, "menu info clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return false;
    }

    @Override
    protected NavigationInfo getNavigationInfo() {
        return NavigationInfo.newMenuNavigation(R.layout.navigation_header, R.menu.navigation_menu);
    }

    @Override
    protected void onNavigationMenuSelected(int id) {
        if (id == R.id.nav_menu_1) {
            Toast.makeText(this, "Navigation menu1 clicked", Toast.LENGTH_LONG).show();
        }
    }

    // Store Observer
    // CAUTION
    // In Object Store, the observer reference is managed "weak".
    // Hence should manage the observer reference.
    private StoreObserver<Boolean> alertDialogBtnObserver = new StoreObserver<Boolean>() {
        @Override
        protected void onChanged(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(MainActivity.this, "Yes clicked", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "No clicked", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void addAlertDialogSample() {

        // button clicked event
        final String ALERT_DIALOG_CLICKED_EVENT = "alert-dialog-click-event";

        // observer for click event
        objectStore.addWeakObserver(ALERT_DIALOG_CLICKED_EVENT, alertDialogBtnObserver, false);

        Button button = new Button(this);
        button.setText(R.string.button_alert_dialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new AlertDialogActivity.Builder(MainActivity.this, "Sample Alert", "Sample Alert Dialog Body")
                        .clickable("yes button", "no button", ALERT_DIALOG_CLICKED_EVENT)
                        .build();

                startActivity(i);
            }
        });

        buttonContainer.addView(button);
    }

    private void addFlatCardSample() {
        Button button = new Button(this);
        button.setText(R.string.button_flat_card);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FlatCardActivity.class);
                startActivity(i);
            }
        });

        buttonContainer.addView(button);

    }
}
