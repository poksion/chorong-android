package net.poksion.chorong.android.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.samples.domain.DbItemModel;
import net.poksion.chorong.android.samples.presenter.SampleForPersistencePresenter;
import net.poksion.chorong.android.samples.ui.DbItemViewModelUtil;
import net.poksion.chorong.android.ui.card.FlatCardRecyclerView;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class SampleForPersistence extends ToolbarActivity implements SampleForPersistencePresenter.View {

    @Assemble FlatCardRecyclerView cardRecyclerView;
    @Assemble DbItemViewModelUtil dbItemViewModelUtil;
    @Assemble SampleForPersistencePresenter presenter;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        ModuleFactory.assemble(this, new SampleForPersistenceAssembler(this, container));

        initCardView();
        presenter.addItems(buildSampleDbItem());
    }

    private void initCardView() {
        cardRecyclerView.setCustomItemViewInflater(dbItemViewModelUtil.getItemViewInflater());

        cardRecyclerView.addItem(
                cardRecyclerView.makeTitleViewModel("Sample for DB", null),
                cardRecyclerView.makeTitleViewBinder());

        cardRecyclerView.addItem(
                dbItemViewModelUtil.makeViewModel("ID", "NAME", "DATE"),
                dbItemViewModelUtil.getViewBinder());

        cardRecyclerView.notifyDataSetChanged();
    }

    @Override
    public void showItems(List<DbItemModel> itemList) {
        for (DbItemModel model : itemList) {
            cardRecyclerView.addItem(
                    dbItemViewModelUtil.makeViewModel(model),
                    dbItemViewModelUtil.getViewBinder());
        }

        cardRecyclerView.notifyDataSetChanged();
    }

    private List<DbItemModel> buildSampleDbItem() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        List<DbItemModel> addingOrUpdatingItems = new ArrayList<>();

        for (int i = 0; i < 4; ++i) {
            DbItemModel itemModel = new DbItemModel();
            itemModel.id = "" + (i+1);
            itemModel.name = "test name " + i;
            itemModel.date = sdf.format(new Date());

            addingOrUpdatingItems.add(itemModel);
        }

        return addingOrUpdatingItems;
    }

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.SKY;
    }
}
