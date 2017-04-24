package net.poksion.chorong.android.samples.presenter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import net.poksion.chorong.android.samples.domain.DbItemModel;
import net.poksion.chorong.android.samples.domain.DbManager;
import net.poksion.chorong.android.task.TaskRunnerSync;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class SampleForPersistencePresenterTest {

    private ArgumentCaptor<List<DbItemModel>> captor;
    private SampleForPersistencePresenter.View view;

    private SampleForPersistencePresenter presenter;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        captor = ArgumentCaptor.forClass((Class) List.class);
        view = mock(SampleForPersistencePresenter.View.class);

        DbManager dbManager = mock(DbManager.class);
        when(dbManager.addItems(anyListOf(DbItemModel.class))).thenAnswer(new Answer<List<DbItemModel>>() {
            @Override
            public List<DbItemModel> answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (List<DbItemModel>) args[0];
            }
        });

        presenter = new SampleForPersistencePresenter(new TaskRunnerSync<>(view), dbManager);
    }

    @Test
    public void test_add_item() {
        List<DbItemModel> items = new ArrayList<>();
        DbItemModel item = new DbItemModel();
        item.id = "dummy-id";
        item.name = "dummy-name";
        item.date = "dummy-date";

        items.add(item);

        presenter.addItems(items);
        verify(view, times(1)).showItems(captor.capture());

        List<DbItemModel> values = captor.getValue();
        assertThat(values.size()).isEqualTo(1);
        assertThat(values.get(0).id).isEqualTo("dummy-id");
    }

}
