package net.poksion.chorong.android.samples.presenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.poksion.chorong.android.presenter.BaseView;
import net.poksion.chorong.android.samples.domain.DbItemModel;
import net.poksion.chorong.android.samples.domain.DbManager;
import net.poksion.chorong.android.samples.domain.DbObservingTask;
import net.poksion.chorong.android.task.SimpleWorkingTask;
import net.poksion.chorong.android.task.TaskRunner;

public class SampleForPersistencePresenter {

    public interface View extends BaseView {
        void showItems(List<DbItemModel> itemList);
    }

    private final TaskRunner<View> taskRunner;
    private final DbManager dbManager;

    public SampleForPersistencePresenter(TaskRunner<View> taskRunner, DbManager dbManager) {
        this.taskRunner = taskRunner;
        this.dbManager = dbManager;

        taskRunner.registerObservingTask(new DbObservingTask<View>(dbManager) {
            @Override
            public void onChanged(List<DbItemModel> dbItemModels, View view) {
                view.showItems(dbItemModels);
            }
        });
    }

    public void readDb() {
        taskRunner.runTask(new SimpleWorkingTask<View>() {
            @Override
            protected void onWork() {
                List<DbItemModel> result = dbManager.readItems(false);
                if (result == null || result.isEmpty()) {
                    dbManager.addItems(buildSampleDbItem());
                }
            }
        });
    }

    public void addItems(final List<DbItemModel> items) {
        taskRunner.runTask(new SimpleWorkingTask<View>() {
            @Override
            protected void onWork() {
                dbManager.addItems(items);
            }
        });
    }

    private List<DbItemModel> buildSampleDbItem() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        List<DbItemModel> sampleItems = new ArrayList<>();

        for (int i = 0; i < 4; ++i) {
            DbItemModel itemModel = new DbItemModel();
            itemModel.id = "" + (i+1);
            itemModel.name = "test name " + i;
            itemModel.date = sdf.format(new Date());

            sampleItems.add(itemModel);
        }

        return sampleItems;
    }
}
