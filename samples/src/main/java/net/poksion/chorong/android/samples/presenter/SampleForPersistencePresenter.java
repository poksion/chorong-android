package net.poksion.chorong.android.samples.presenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.poksion.chorong.android.presenter.BaseView;
import net.poksion.chorong.android.samples.domain.SampleItem;
import net.poksion.chorong.android.samples.domain.DbObservingTask;
import net.poksion.chorong.android.samples.domain.SampleItemRepository;
import net.poksion.chorong.android.task.ObservingBuffer;
import net.poksion.chorong.android.task.SimpleTask;
import net.poksion.chorong.android.task.TaskRunner;

public class SampleForPersistencePresenter {

    public interface View extends BaseView {
        void showItems(List<SampleItem> itemList);
    }

    private final TaskRunner<View> taskRunner;
    private final SampleItemRepository sampleItemRepository;

    private final ObservingBuffer<SampleItem, View> observingBuffer = new ObservingBuffer<>(
            new ObservingBuffer.Callback<SampleItem, View>() {
                @Override
                public void completeOnMain(List<SampleItem> results, View listener) {
                    listener.showItems(results);
                }

                @Override
                public void completeOnSub(List<SampleItem> results, View listener) {
                    listener.showItems(results);
                }
            }
    );

    public SampleForPersistencePresenter(TaskRunner<View> taskRunner, SampleItemRepository sampleItemRepository) {
        this.taskRunner = taskRunner;
        this.sampleItemRepository = sampleItemRepository;

        taskRunner.registerObservingTask(new DbObservingTask<View>(sampleItemRepository) {
            @Override
            public void onChanged(List<SampleItem> sampleItems, View view) {
                observingBuffer.listenSub(sampleItems, view);
            }
        });
    }

    public void readDb() {
        taskRunner.runTask(new SimpleTask<List<SampleItem>, View>() {
            @Override
            protected List<SampleItem> onWorkSimple() {
                List<SampleItem> result = sampleItemRepository.findAll();
                if (result.isEmpty()) {
                    sampleItemRepository.storeAll(buildSampleDbItem());
                }

                return result;
            }

            @Override
            protected void onResultSimple(List<SampleItem> sampleItems, View view) {
                observingBuffer.completeMain(sampleItems, view);
            }
        });
    }

    public void reloadItem(final String id) {
        taskRunner.runTask(new SimpleTask<SampleItem, View>() {
            @Override
            protected SampleItem onWorkSimple() {
                return sampleItemRepository.find(id);
            }

            @Override
            protected void onResultSimple(SampleItem sampleItem, View view) {
                if (sampleItem != null && !view.isFinishing()) {
                    List<SampleItem> items = new ArrayList<>();
                    items.add(sampleItem);
                    view.showItems(items);
                }
            }
        });
    }

    private List<SampleItem> buildSampleDbItem() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        List<SampleItem> sampleItems = new ArrayList<>();

        for (int i = 0; i < 4; ++i) {
            SampleItem itemModel = new SampleItem();
            itemModel.id = "" + (i+1);
            itemModel.name = "test name " + i;
            itemModel.date = sdf.format(new Date());

            sampleItems.add(itemModel);
        }

        return sampleItems;
    }
}
