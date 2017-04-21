package net.poksion.chorong.android.samples.presenter;

import java.util.List;
import net.poksion.chorong.android.samples.domain.DbItemModel;
import net.poksion.chorong.android.samples.domain.DbManager;
import net.poksion.chorong.android.task.SimpleTask;
import net.poksion.chorong.android.task.TaskRunner;

public class SampleForPersistencePresenter {
    public interface View {
        boolean isFinishing();

        void loadItems(List<DbItemModel> itemList);
    }

    private final TaskRunner<View> taskRunner;
    private final DbManager dbManager;

    public SampleForPersistencePresenter(TaskRunner<View> taskRunner, DbManager dbManager) {
        this.taskRunner = taskRunner;
        this.dbManager = dbManager;
    }

    public void addItems(final List<DbItemModel> items) {
        taskRunner.runTask(new SimpleTask<List<DbItemModel>, View>() {
            @Override
            protected List<DbItemModel> onWorkSimple() {
                return dbManager.addItems(items);
            }

            @Override
            protected void onResultSimple(List<DbItemModel> dbItemModels, View view) {
                if (view.isFinishing()) {
                    return;
                }

                view.loadItems(dbItemModels);
            }
        });
    }
}
