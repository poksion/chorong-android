package net.poksion.chorong.android.samples;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.lang.reflect.Field;
import net.poksion.chorong.android.module.ViewModuleAssembler;

class MainActivityAssembler extends ViewModuleAssembler {

    private final MainActivity mainActivity;
    private final ViewGroup container;

    MainActivityAssembler(MainActivity mainActivity, ViewGroup container) {
        super(null, mainActivity);

        this.mainActivity = mainActivity;
        this.container = container;
    }

    @Override
    public Object findModule(Class<?> filedClass, int id) {

        if (filedClass.isAssignableFrom(LinearLayout.class)) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            ScrollView scrollView = new ScrollView(mainActivity);
            container.addView(scrollView, params);

            LinearLayout linearLayout = new LinearLayout(mainActivity);
            scrollView.addView(linearLayout, params);

            return linearLayout;
        }

        return super.findModule(filedClass, id);
    }

    @Override
    public void setField(Field filed, Object object, Object value) throws IllegalAccessException {
        filed.set(object, value);
    }
}
