package net.poksion.chorong.android.samples;

import android.content.Intent;
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
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class MainActivity extends ToolbarActivity {

    @Assemble LinearLayout buttonContainer;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        ModuleFactory.assemble(this, new SampleAssembler(this, container));

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

    private void addAlertDialogSample() {
        Button button = new Button(this);
        button.setText(R.string.button_alert_dialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SampleForAlertDialog.class);
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
                Intent i = new Intent(MainActivity.this, SampleForFlatCard.class);
                startActivity(i);
            }
        });

        buttonContainer.addView(button);

    }
}
