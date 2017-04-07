package net.poksion.chorong.android.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.ui.card.FlatCardGeneralContentView;
import net.poksion.chorong.android.ui.card.FlatCardLoadingView;
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

        flatCardRecyclerView.addItem(flatCardRecyclerView.makeTitleViewModel("First card title", null), titleViewModelBinder);
        flatCardRecyclerView.addItem(new ViewModel<FlatTitleView, String[]>(R.layout.flat_card_title, new String[] {"Second card title", "first sub title"}), titleViewModelBinder);
        flatCardRecyclerView.addItem(flatCardRecyclerView.makeGeneralContentViewModel("Card item : general content"), generalContentViewModelBinder);
        flatCardRecyclerView.addItem(flatCardRecyclerView.makeLoadingViewModel(), loadingViewModelBinder);

        flatCardRecyclerView.notifyDataSetChanged();
    }

    private final ViewModelBinder<FlatTitleView, String[]> titleViewModelBinder = new ViewModelBinder<FlatTitleView, String[]>() {
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

    private final ViewModelBinder<FlatCardGeneralContentView, String> generalContentViewModelBinder = new ViewModelBinder<FlatCardGeneralContentView, String>() {
        @Override
        public void onBind(FlatCardGeneralContentView view, String model) {
            view.setTextContent(model);
            view.hideBoxContent();
        }
    };

    private final ViewModelBinder<FlatCardLoadingView, FlatCardLoadingView.LoadingState> loadingViewModelBinder = new ViewModelBinder<FlatCardLoadingView, FlatCardLoadingView.LoadingState>() {
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
