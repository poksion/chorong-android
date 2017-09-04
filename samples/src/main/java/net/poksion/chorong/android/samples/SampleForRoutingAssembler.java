package net.poksion.chorong.android.samples;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.reflect.Field;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.route.Performer;
import net.poksion.chorong.android.route.Router;

class SampleForRoutingAssembler extends SampleAssembler<SampleForRouting> {

    private static final String ROUTING_ID_KEY = "routing-id-key";

    @SuppressWarnings("unused")
    @Assemble private ObjectStore objectStore;

    private final int currentRoutingId;

    private final int navHeaderResId;
    private final int navMenuResId;

    private final Performer<Integer> listeningPerformer = new Performer<Integer>() {
        @Override
        public void onNavigateTo(Integer to, Bundle bundle) {
            Intent i = new Intent(activity, SampleForRouting.class);
            i.putExtra(ROUTING_ID_KEY, to);

            activity.startActivity(i);
        }
    };

    SampleForRoutingAssembler(SampleForRouting activity, ViewGroup container) {
        super(activity, container);

        Intent i = activity.getIntent();
        if (i == null) {
            currentRoutingId = 0;
        } else {
            currentRoutingId = i.getIntExtra(ROUTING_ID_KEY, 0);
        }

        switch(currentRoutingId) {
            case R.id.nav_menu_1:
                activity.setTitle("Routing on Nav Menu 1");
                navHeaderResId = 0;
                navMenuResId = 0;
                break;
            case R.id.nav_menu_2:
                activity.setTitle("Routing on Nav Menu 2");
                navHeaderResId = 0;
                navMenuResId = 0;
                break;
            default:
                activity.setTitle("Sample Routing Activity");
                navHeaderResId = R.layout.navigation_header;
                navMenuResId = R.menu.navigation_menu;
                break;
        }

        TextView textView = new TextView(activity);
        textView.setText(R.string.text_routing_sample);
        container.addView(textView, wrapHeightParams);
    }

    @Override
    protected void onInit(Factory factory) {
        super.onInit(factory);

        factory.addProvider(new Provider() {
            @Override
            public boolean isMatchedField(Class<?> fieldClass) {
                return fieldClass.equals(Integer.class);
            }

            @Override
            public Object provide(int id) {
                if (id == R.layout.navigation_header) {
                    return navHeaderResId;
                }
                if (id == R.menu.navigation_menu) {
                    return navMenuResId;
                }
                return null;
            }
        });

        factory.addProvider(new Provider() {
            @Override
            public boolean isMatchedField(Class<?> fieldClass) {
                return fieldClass.equals(Router.class);
            }

            @Override
            public Object provide(int id) {
                Router<Integer> router = new Router<>("sample-router");
                router.init(objectStore, currentRoutingId, listeningPerformer);

                return router;
            }
        });
    }

    @Override
    public void setField(Field field, Object object, Object value) throws IllegalAccessException {
        field.set(object, value);
    }
}
