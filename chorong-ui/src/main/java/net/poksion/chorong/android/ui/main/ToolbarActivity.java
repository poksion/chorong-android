package net.poksion.chorong.android.ui.main;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import net.poksion.chorong.android.ui.R;

public abstract class ToolbarActivity extends AppCompatActivity {

    protected abstract void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState);

    protected enum ThemeType {
        DARK,
        GREEN,
        PURPLE,
        SKY,

        CUSTOM
    }

    protected abstract ThemeType getThemeType();

    protected static class NavigationInfo {
        private final int headerResId;
        private final int menuResId;

        private final int descOpenDrawer;
        private final int descCloseDrawer;

        private NavigationInfo(int headerResId, int menuResId, int descOpenDrawer, int descCloseDrawer) {
            this.headerResId = headerResId;
            this.menuResId = menuResId;

            this.descOpenDrawer = descOpenDrawer;
            this.descCloseDrawer = descCloseDrawer;
        }

        public static NavigationInfo newMenuNavigation(int headerResId, int menuResId) {
            return newMenuNavigation(headerResId, menuResId, R.string.desc_open_drawer, R.string.desc_close_drawer);
        }

        public static NavigationInfo newMenuNavigation(int headerResId, int menuResId, int descOpenDrawer, int descCloseDrawer) {
            return new NavigationInfo(headerResId, menuResId, descOpenDrawer, descCloseDrawer);
        }

        public static NavigationInfo newUpNavigation() {
            return newMenuNavigation(0, 0);
        }
    }

    protected NavigationInfo getNavigationInfo() {
        return null;
    }

    protected void onNavigationMenuSelected(int id) {
    }

    protected static class MenuInfo {
        private final int id;
        private int strResId;

        public MenuInfo(int id, int strRedId) {
            this.id = id;
            this.strResId = strRedId;
        }

        public MenuInfo(int strResIdAsId) {
            this.id = strResIdAsId;
            this.strResId = strResIdAsId;
        }
    }

    protected List<MenuInfo> getMenuInfoList() {
        return null;
    }

    protected boolean onMenuSelected(int id) {
        return false;
    }

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme();

        setContentView(R.layout.activity_toolbar);

        setToolbar();

        ViewGroup container = (ViewGroup) findViewById(R.id.main_content);
        onCreateContentView(getLayoutInflater(), container, savedInstanceState);

        setDrawer();

        // remove background for performance
        getWindow().setBackgroundDrawable(null);
    }

    private void setTheme() {
        ThemeType themeType = getThemeType();
        switch(themeType == null? ThemeType.CUSTOM : themeType) {
            case DARK:
                setTheme(R.style.MyAppTheme_Dark);
                break;
            case GREEN:
                setTheme(R.style.MyAppTheme_Green);
                break;
            case PURPLE:
                setTheme(R.style.MyAppTheme_Purple);
                break;
            case SKY:
                setTheme(R.style.MyAppTheme_Sky);
                break;
            case CUSTOM:
                // do nothing
                break;
        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
    }

    private void setDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);

        NavigationInfo navigationInfo = getNavigationInfo();
        if (navigationInfo == null) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            return;
        }

        if (navigationInfo.headerResId == 0) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            actionBar.setDisplayHomeAsUpEnabled(true);
            return;
        }

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                navigationInfo.descOpenDrawer,
                navigationInfo.descCloseDrawer) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                // disable animation
                super.onDrawerSlide(drawerView, 0);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // disable animation
                super.onDrawerSlide(drawerView, 0);
            }
        };

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation);
        navigationView.inflateHeaderView(navigationInfo.headerResId);
        navigationView.inflateMenu(navigationInfo.menuResId);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                onNavigationMenuSelected(item.getItemId());
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        List<MenuInfo> menuInfoList = getMenuInfoList();
        if (menuInfoList != null && !menuInfoList.isEmpty()) {
            int order = 0;
            for (MenuInfo menuInfo : menuInfoList) {
                order++;
                MenuItem info = menu.add(Menu.NONE, menuInfo.id, order, menuInfo.strResId);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    info.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
                }
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    // if option menu needs dynamic state, do something in this method
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return onMenuSelected(id) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected final void setTitle(String title) {
        actionBar.setTitle(title);
    }

}
