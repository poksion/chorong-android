package net.poksion.chorong.android.samples.presenter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import net.poksion.chorong.android.samples.domain.SampleItem;
import net.poksion.chorong.android.samples.domain.SampleItemRepository;
import net.poksion.chorong.android.store.ObjectStore;
import net.poksion.chorong.android.store.ObjectStoreImpl;
import net.poksion.chorong.android.store.persistence.DatabaseProxyManager;
import net.poksion.chorong.android.task.TaskRunnerSync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class SampleForPersistencePresenterTest {

    private ArgumentCaptor<List<SampleItem>> captor;
    private SampleForPersistencePresenter.View view;

    private SampleItemRepository sampleItemRepository;
    private SampleForPersistencePresenter presenter;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        captor = ArgumentCaptor.forClass((Class) List.class);
        view = mock(SampleForPersistencePresenter.View.class);
        when(view.isFinishing()).thenReturn(false);

        final ObjectStore objectStore = new ObjectStoreImpl();
        final ObjectStore.Key storeKey = new ObjectStore.Key(SampleItemRepository.SAMPLE_DB_CACHE_STATIC_KEY);

        sampleItemRepository = new SampleItemRepository(mock(DatabaseProxyManager.class), objectStore) {
            List<SampleItem> stored = new ArrayList<>();

            @Override
            public void storeAll(List<SampleItem> items) {
                stored.addAll(items);
                objectStore.set(storeKey, items);
            }

            @Override
            public List<SampleItem> findAll() {
                return stored;
            }
        };

        presenter = new SampleForPersistencePresenter(new TaskRunnerSync<>(view), sampleItemRepository);
    }

    @Test
    public void view_should_show_items_stored_on_repository() {
        List<SampleItem> items = new ArrayList<>();
        SampleItem item = new SampleItem();
        item.id = "dummy-id";
        item.name = "dummy-name";
        item.date = "dummy-date";

        items.add(item);
        sampleItemRepository.storeAll(items);

        presenter.readDb();
        verify(view, times(1)).showItems(captor.capture());

        List<SampleItem> values = captor.getValue();
        assertThat(values.size()).isEqualTo(1);
        assertThat(values.get(0).id).isEqualTo("dummy-id");
    }

}
