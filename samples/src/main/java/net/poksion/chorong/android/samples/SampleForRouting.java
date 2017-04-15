package net.poksion.chorong.android.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.ui.main.ToolbarActivity;
import net.poksion.chorong.android.ui.route.Router;

public class SampleForRouting extends ToolbarActivity {

    @Assemble(R.layout.navigation_header) Integer navHeaderResId;
    @Assemble(R.menu.navigation_menu) Integer navMenuResId;

    @Assemble Router<Integer> router;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        ModuleFactory.assemble(this, new SampleForRoutingAssembler(this, container));
    }

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.PURPLE;
    }

    @Override
    protected NavigationInfo getNavigationInfo() {
        return NavigationInfo.newMenuNavigation(navHeaderResId, navMenuResId);
    }

    @Override
    protected void onNavigationMenuSelected(int id) {
        router.navigateTo(id);
    }
}