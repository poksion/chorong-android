package net.poksion.chorong.android.samples;

import android.view.ViewGroup;
import java.lang.reflect.Field;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.samples.domain.SampleItemRepository;
import net.poksion.chorong.android.samples.presenter.SampleForPersistencePresenter;
import net.poksion.chorong.android.samples.ui.SampleItemViewModelUtil;
import net.poksion.chorong.android.task.TaskRunnerAsyncShared;

@SuppressWarnings("unused")
class SampleForPersistenceAssembler extends SampleAssembler<SampleForPersistence> {

    @Assemble private SampleItemRepository sampleItemRepository;

    SampleForPersistenceAssembler(SampleForPersistence activity, ViewGroup container) {
        super(activity, container);
    }

    @Override
    protected void onInit(Factory factory) {
        super.onInit(factory);

        factory.addProvider(new Provider() {
            @Override
            public boolean isMatchedField(Class<?> filedClass) {
                return filedClass.equals(SampleForPersistencePresenter.class);
            }

            @Override
            public Object provide(int id) {
                return new SampleForPersistencePresenter(
                        new TaskRunnerAsyncShared<SampleForPersistencePresenter.View>(activity),
                        sampleItemRepository);
            }
        });

        factory.addProvider(new Provider() {
            @Override
            public boolean isMatchedField(Class<?> filedClass) {
                return filedClass.equals(SampleItemViewModelUtil.class);
            }

            @Override
            public Object provide(int id) {
                return new SampleItemViewModelUtil();
            }
        });
    }

    @Override
    public void setField(Field filed, Object object, Object value) throws IllegalAccessException {
        filed.set(object, value);
    }
}
