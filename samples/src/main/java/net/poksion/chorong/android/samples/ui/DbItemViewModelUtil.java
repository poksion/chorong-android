package net.poksion.chorong.android.samples.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.poksion.chorong.android.samples.R;
import net.poksion.chorong.android.samples.databinding.DbRowBinding;
import net.poksion.chorong.android.samples.domain.DbItemModel;
import net.poksion.chorong.android.ui.card.ViewBinder;
import net.poksion.chorong.android.ui.card.ViewModel;
import net.poksion.chorong.android.ui.card.ViewUpdatableAdapter;

public class DbItemViewModelUtil {

    private ViewUpdatableAdapter.ItemViewInflater itemViewInflater = new ViewUpdatableAdapter.ItemViewInflater() {

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

    private ViewBinder<View, DbItemModel> viewBinder = new ViewBinder<View, DbItemModel>() {
        @Override
        public void onBind(View view, DbItemModel model) {
            DbRowBinding binding = (DbRowBinding) view.getTag();
            binding.setItem(model);
        }
    };


    public ViewModel<View, DbItemModel> makeViewModel(String id, String name, String date) {
        DbItemModel model = new DbItemModel();
        model.id = id;
        model.name = name;
        model.date = date;

        return makeViewModel(model);
    }

    public ViewModel<View, DbItemModel> makeViewModel(DbItemModel model) {
        return new ViewModel<>(R.layout.db_row, model);
    }

    public ViewBinder<View, DbItemModel> getViewBinder() {
        return viewBinder;
    }

    public ViewUpdatableAdapter.ItemViewInflater getItemViewInflater() {
        return itemViewInflater;
    }
}
