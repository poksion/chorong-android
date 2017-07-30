package net.poksion.chorong.android.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.List;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.module.ModuleFactory;
import net.poksion.chorong.android.samples.domain.SampleItem;
import net.poksion.chorong.android.samples.presenter.SampleForPersistencePresenter;
import net.poksion.chorong.android.samples.ui.SampleItemClickHandler;
import net.poksion.chorong.android.samples.ui.SampleItemViewModelUtil;
import net.poksion.chorong.android.ui.card.FlatCardRecyclerView;
import net.poksion.chorong.android.ui.main.ToolbarActivity;

public class SampleForPersistence extends ToolbarActivity implements SampleForPersistencePresenter.View {

    @Assemble FlatCardRecyclerView cardRecyclerView;
    @Assemble SampleItemViewModelUtil sampleItemViewModelUtil;
    @Assemble SampleForPersistencePresenter presenter;

    @Override
    protected void onCreateContentView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        ModuleFactory.assemble(SampleForPersistence.class, this, new SampleForPersistenceAssembler(this, container));

        initCardView();
        presenter.readDb();
    }

    private void initCardView() {
        cardRecyclerView.setCustomItemViewInflater(sampleItemViewModelUtil.getItemViewInflater());

        cardRecyclerView.addItem(
                cardRecyclerView.makeTitleViewModel("Sample for DB", null),
                cardRecyclerView.makeTitleViewBinder());

        cardRecyclerView.addItem(
                sampleItemViewModelUtil.makeViewModel("ID", "NAME", "DATE"),
                sampleItemViewModelUtil.makeViewBinder(new SampleItemClickHandler() {
                    @Override
                    public void onItemClick(String id) {
                        Toast.makeText(SampleForPersistence.this, "Header clicked", Toast.LENGTH_SHORT).show();
                    }
                }));

        cardRecyclerView.notifyDataSetChanged();
    }

    @Override
    public void showItems(List<SampleItem> itemList) {
        for (SampleItem model : itemList) {
            cardRecyclerView.addItem(
                    sampleItemViewModelUtil.makeViewModel(model),
                    sampleItemViewModelUtil.makeViewBinder(new SampleItemClickHandler() {
                        @Override
                        public void onItemClick(String id) {
                            presenter.reloadItem(id);
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
