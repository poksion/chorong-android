package net.poksion.chorong.android.samples;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        addSampleOpener(R.string.button_sample_alert_dialog, SampleForAlertDialog.class);
        addSampleOpener(R.string.button_sample_flat_card, SampleForFlatCard.class);
        addSampleOpener(R.string.button_sample_routing, SampleForRouting.class);
    }

    private void addSampleOpener(@StringRes int buttonStrRes, final Class<?> sampleActivityClass) {
        Button button = new Button(this);
        button.setText(buttonStrRes);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, sampleActivityClass);
                startActivity(i);
            }
        });

        buttonContainer.addView(button);
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
    protected ThemeType getThemeType() {
        return ThemeType.SKY;
    }
}
