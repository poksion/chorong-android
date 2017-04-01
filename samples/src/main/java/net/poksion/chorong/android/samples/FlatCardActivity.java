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

        ViewModelBinder<FlatTitleView, String[]> titleViewModelBinder = makeTitleViewModelBinder();

        ViewModel<FlatTitleView, String[]> firstTitle = flatCardRecyclerView.makeTitleViewModel("First card title", null);
        flatCardRecyclerView.addItem(firstTitle, titleViewModelBinder);

        ViewModel<FlatTitleView, String[]> secondTitle = new ViewModel<>(R.layout.flat_card_title, new String[] {"Second card title", "first sub title"});
        flatCardRecyclerView.addItem(secondTitle, titleViewModelBinder);
        ViewModel<FlatCardGeneralContentView, String> secondContent = flatCardRecyclerView.makeGeneralContentViewModel("Card item : general content");
        flatCardRecyclerView.addItem(secondContent, makeGeneralContentViewModelBinder());

        ViewModel<FlatCardLoadingView, FlatCardLoadingView.LoadingState> thirdLoading = flatCardRecyclerView.makeLoadingViewModel();
        flatCardRecyclerView.addItem(thirdLoading, makeLoadingViewModelBinder());

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

    private ViewModelBinder<FlatCardGeneralContentView, String> makeGeneralContentViewModelBinder() {
        return new ViewModelBinder<FlatCardGeneralContentView, String>() {
            @Override
            public void onBind(FlatCardGeneralContentView view, String model) {
                view.setTextContent(model);
                view.hideBoxContent();
            }
        };
    }

    private ViewModelBinder<FlatCardLoadingView, FlatCardLoadingView.LoadingState> makeLoadingViewModelBinder() {
        return new ViewModelBinder<FlatCardLoadingView, FlatCardLoadingView.LoadingState>() {
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
