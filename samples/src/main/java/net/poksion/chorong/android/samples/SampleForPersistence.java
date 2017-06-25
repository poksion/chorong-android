package net.poksion.chorong.android.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.List;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.samples.domain.DbItemModel;
import net.poksion.chorong.android.samples.presenter.SampleForPersistencePresenter;
import net.poksion.chorong.android.samples.ui.DbItemClickHandler;
import net.poksion.chorong.android.samples.ui.DbItemViewModelUtil;
import net.poksion.chorong.android.ui.card.FlatCardRecyclerView;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class SampleForPersistence extends ToolbarActivity implements SampleForPersistencePresenter.View {

    @Assemble FlatCardRecyclerView cardRecyclerView;
    @Assemble DbItemViewModelUtil dbItemViewModelUtil;
    @Assemble SampleForPersistencePresenter presenter;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        ModuleFactory.assemble(SampleForPersistence.class, this, new SampleForPersistenceAssembler(this, container));

        initCardView();
        presenter.readDb();
    }

    private void initCardView() {
        cardRecyclerView.setCustomItemViewInflater(dbItemViewModelUtil.getItemViewInflater());

        cardRecyclerView.addItem(
                cardRecyclerView.makeTitleViewModel("Sample for DB", null),
                cardRecyclerView.makeTitleViewBinder());

        cardRecyclerView.addItem(
                dbItemViewModelUtil.makeViewModel("ID", "NAME", "DATE"),
                dbItemViewModelUtil.makeViewBinder(new DbItemClickHandler() {
                    @Override
                    public void onItemClick(String id) {
                        Toast.makeText(SampleForPersistence.this, "Header clicked", Toast.LENGTH_SHORT).show();
                    }
                }));

        cardRecyclerView.notifyDataSetChanged();
    }

    @Override
    public void showItems(List<DbItemModel> itemList) {
        for (DbItemModel model : itemList) {
            cardRecyclerView.addItem(
                    dbItemViewModelUtil.makeViewModel(model),
                    dbItemViewModelUtil.makeViewBinder(new DbItemClickHandler() {
                        @Override
                        public void onItemClick(String id) {
                            presenter.readItem(id);
                        }
                    }));
        }

        cardRecyclerView.notifyDataSetChanged();
    }

    @Override
    protected ThemeType getThemeType() {
        return ThemeType.SKY;
    }
}
