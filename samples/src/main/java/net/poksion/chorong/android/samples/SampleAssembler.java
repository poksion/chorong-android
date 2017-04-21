package net.poksion.chorong.android.samples;

import android.app.Activity;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.lang.reflect.Field;
import net.poksion.chorong.android.module.ViewModuleAssembler;
import net.poksion.chorong.android.ui.card.FlatCardRecyclerView;

class SampleAssembler<T extends Activity> extends ViewModuleAssembler {

    final T activity;
    final ViewGroup.LayoutParams wrapHeightParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    private final ViewGroup container;

    SampleAssembler(T activity, ViewGroup container) {
        super(null, activity);

        this.activity = activity;
        this.container = container;

        container.setBackgroundColor(Color.WHITE);
    }

    @Override
    public Object findModule(Class<?> filedClass, int id) {

        if (filedClass.equals(LinearLayout.class)) {

            ScrollView scrollView = new ScrollView(activity);
            container.addView(scrollView, wrapHeightParams);

            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(linearLayout, wrapHeightParams);

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
