package net.poksion.chorong.android.ui.main;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import net.poksion.chorong.android.ui.R;

public abstract class OneSubjectMainActivity extends AppCompatActivity {

    protected abstract void onCreateContent(ViewGroup rootView, Bundle savedInstanceState);

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_subject_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        ViewGroup root = (ViewGroup) findViewById(R.id.main_content);
        onCreateContent(root, savedInstanceState);

        // remove background for performance
        getWindow().setBackgroundDrawable(null);
    }

    protected void setTitle(String title) {
        actionBar.setTitle(title);
    }

    private static int MENU_INFO_ID = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuItem info = menu.add(Menu.NONE, MENU_INFO_ID, Menu.NONE, R.string.menu_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            info.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
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

        if (id == MENU_INFO_ID) {
            // TODO
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
