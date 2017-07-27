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
    public Object findModule(Class<?> filedClass, int id) {
        if (filedClass.equals(SampleForPersistencePresenter.class)) {
            return new SampleForPersistencePresenter(
                    new TaskRunnerAsyncShared<SampleForPersistencePresenter.View>(activity),
                    sampleItemRepository);
        }

        if (filedClass.equals(SampleItemViewModelUtil.class)) {
            return new SampleItemViewModelUtil();
        }

        return super.findModule(filedClass, id);
    }

    @Override
    public void setField(Field filed, Object object, Object value) throws IllegalAccessException {
        filed.set(object, value);
    }
}
