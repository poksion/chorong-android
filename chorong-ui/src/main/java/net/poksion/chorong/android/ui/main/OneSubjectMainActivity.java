package net.poksion.chorong.android.ui.main;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.List;
import net.poksion.chorong.android.ui.R;

public abstract class OneSubjectMainActivity extends AppCompatActivity {

    protected abstract void onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState);
    protected abstract boolean isUsingCustomTheme();

    public static class MenuInfo {
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

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUsingCustomTheme()) {
            setTheme(R.style.MyAppTheme_Dark);
        }

        setContentView(R.layout.activity_one_subject_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        ViewGroup container = (ViewGroup) findViewById(R.id.main_content);
        onCreateView(getLayoutInflater(), container, savedInstanceState);

        // remove background for performance
        getWindow().setBackgroundDrawable(null);
    }

    protected void setTitle(String title) {
        actionBar.setTitle(title);
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

        if (onMenuSelected(id)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
