package net.poksion.chorong.android.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.ui.card.FlatCardRecyclerView;
import net.poksion.chorong.android.ui.card.FlatTitleView;
import net.poksion.chorong.android.ui.card.ViewModel;
import net.poksion.chorong.android.ui.card.ViewModelBinder;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class FlatCardActivity extends ToolbarActivity {

    @Assemble FlatCardRecyclerView flatCardRecyclerView;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        ModuleFactory.assemble(this, new ActivityAssembler(this, container));

        ViewModelBinder<FlatTitleView, String[]> titleViewModelBinder = makeTitleViewModelBinder();

        ViewModel<FlatTitleView, String[]> firstTitle = flatCardRecyclerView.makeTitleViewModel("First card title", null);
        ViewModel<FlatTitleView, String[]> secondTitle = new ViewModel<>(R.layout.flat_card_title, new String[] {"Second card title", "first sub title"});

        flatCardRecyclerView.addItem(firstTitle, titleViewModelBinder);
        flatCardRecyclerView.addItem(secondTitle, titleViewModelBinder);

        flatCardRecyclerView.notifyDataSetChanged();
    }

    private ViewModelBinder<FlatTitleView, String[]> makeTitleViewModelBinder() {
        return new ViewModelBinder<FlatTitleView, String[]>() {
            @Override
            public void onBind(FlatTitleView view, String[] model) {
                if (model[1] == null) {
                    view.hideBlank();
                } else {
                    view.showBlank();
                }
                view.setTitle(model[0], model[1]);
            }
        };
    }

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.GREEN;
    }

    @Override
    protected NavigationInfo getNavigationInfo() {
        return NavigationInfo.newUpNavigation();
    }
}
