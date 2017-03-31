package net.poksion.chorong.android.samples;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.lang.reflect.Field;
import net.poksion.chorong.android.module.ViewModuleAssembler;
import net.poksion.chorong.android.ui.card.FlatCardRecyclerView;

class ActivityAssembler extends ViewModuleAssembler {

    private final Activity activity;
    private final ViewGroup container;

    ActivityAssembler(Activity activity, ViewGroup container) {
        super(null, activity);

        this.activity = activity;
        this.container = container;
    }

    @Override
    public Object findModule(Class<?> filedClass, int id) {

        if (filedClass.equals(LinearLayout.class)) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            ScrollView scrollView = new ScrollView(activity);
            container.addView(scrollView, params);

            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            scrollView.addView(linearLayout, params);

            return linearLayout;
        }

        if (filedClass.equals(FlatCardRecyclerView.class)) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            FlatCardRecyclerView flatCardRecyclerView = new FlatCardRecyclerView(activity);
            container.addView(flatCardRecyclerView, params);

            return flatCardRecyclerView;
        }

        return super.findModule(filedClass, id);
    }

    @Override
    public void setField(Field filed, Object object, Object value) throws IllegalAccessException {
        filed.set(object, value);
    }
}
