package net.poksion.chorong.android.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.ui.card.FlatCardGeneralContentView;
import net.poksion.chorong.android.ui.card.FlatCardLoadingView;
import net.poksion.chorong.android.ui.card.FlatCardRecyclerView;
import net.poksion.chorong.android.ui.card.FlatCardTitleView;
import net.poksion.chorong.android.ui.card.ViewBinder;
import net.poksion.chorong.android.ui.card.ViewModel;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class SampleForFlatCard extends ToolbarActivity {

    @Assemble FlatCardRecyclerView flatCardRecyclerView;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {

        ModuleFactory.assemble(SampleForFlatCard.class, this, new SampleAssembler<>(this, container));

        flatCardRecyclerView.addItem(flatCardRecyclerView.makeTitleViewModel("First card title", null), titleViewBinder);
        flatCardRecyclerView.addItem(new ViewModel<FlatCardTitleView, String[]>(R.layout.flat_card_title, new String[] {"Second card title", "first sub title"}), titleViewBinder);
        flatCardRecyclerView.addItem(flatCardRecyclerView.makeGeneralContentViewModel("Card item : general content"), generalContentViewBinder);
        flatCardRecyclerView.addItem(flatCardRecyclerView.makeLoadingViewModel(), loadingViewBinder);

        flatCardRecyclerView.notifyDataSetChanged();
    }

    private final ViewBinder<FlatCardTitleView, String[]> titleViewBinder = new ViewBinder<FlatCardTitleView, String[]>() {
        @Override
        public void onBind(FlatCardTitleView view, String[] model) {
            if (model[1] == null) {
                view.hideBlank();
            } else {
                view.showBlank();
            }
            view.setTitle(model[0], model[1]);
        }
    };

    private final ViewBinder<FlatCardGeneralContentView, String> generalContentViewBinder = new ViewBinder<FlatCardGeneralContentView, String>() {
        @Override
        public void onBind(FlatCardGeneralContentView view, String model) {
            view.setTextContent(model);
            view.hideBoxContent();
        }
    };

    private final ViewBinder<FlatCardLoadingView, FlatCardLoadingView.LoadingState> loadingViewBinder = new ViewBinder<FlatCardLoadingView, FlatCardLoadingView.LoadingState>() {
        @Override
        public void onBind(FlatCardLoadingView view, FlatCardLoadingView.LoadingState model) {
            switch(model) {
                case START:
                    view.startLoading();
                    break;
                case STOP:
                    view.stopLoading();
                    break;
                case FAIL:
                    view.failLoading("Fail loading", null);
                    break;
            }
        }
    };

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.GREEN;
    }

    @Override
    protected NavigationInfo getNavigationInfo() {
        return NavigationInfo.newUpNavigation();
    }
}
