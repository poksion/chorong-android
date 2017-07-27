package net.poksion.chorong.android.samples.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.poksion.chorong.android.samples.R;
import net.poksion.chorong.android.samples.databinding.DbRowBinding;
import net.poksion.chorong.android.samples.domain.SampleItem;
import net.poksion.chorong.android.ui.card.ViewBinder;
import net.poksion.chorong.android.ui.card.ViewModel;
import net.poksion.chorong.android.ui.card.ItemAdapter;

public class SampleItemViewModelUtil {

    private ItemAdapter.ViewInflater itemViewInflater = new ItemAdapter.ViewInflater() {

        @Override
        public View inflate(LayoutInflater layoutInflater, @LayoutRes int resId, ViewGroup parent) {
            if (resId == R.layout.db_row) {
                DbRowBinding binding = DataBindingUtil.inflate(layoutInflater, resId, parent, false);
                View root = binding.getRoot();
                root.setTag(binding);
                return root;
            }
            return super.inflate(layoutInflater, resId, parent);
        }
    };

    public ViewModel<View, SampleItem> makeViewModel(String id, String name, String date) {
        SampleItem model = new SampleItem();
        model.id = id;
        model.name = name;
        model.date = date;

        return makeViewModel(model);
    }

    public ViewModel<View, SampleItem> makeViewModel(SampleItem model) {
        return new ViewModel<>(R.layout.db_row, model);
    }

    public ViewBinder<View, SampleItem> makeViewBinder(final SampleItemClickHandler clickHandler) {
        return new ViewBinder<View, SampleItem>() {
            @Override
            public void onBind(View view, SampleItem model) {
                DbRowBinding binding = (DbRowBinding) view.getTag();
                binding.setItem(model);
                binding.setItemClickHandler(clickHandler);
            }
        };
    }

    public ItemAdapter.ViewInflater getItemViewInflater() {
        return itemViewInflater;
    }
}
