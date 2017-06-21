package net.poksion.chorong.android.samples;

import android.view.ViewGroup;
import java.lang.reflect.Field;
import net.poksion.chorong.android.module.Assemble;
import net.poksion.chorong.android.samples.domain.DbManager;
import net.poksion.chorong.android.samples.presenter.SampleForPersistencePresenter;
import net.poksion.chorong.android.samples.ui.DbItemViewModelUtil;
import net.poksion.chorong.android.task.TaskRunnerAsyncShared;

@SuppressWarnings("unused")
class SampleForPersistenceAssembler extends SampleAssembler<SampleForPersistence> {

    @Assemble private DbManager dbManager;

    SampleForPersistenceAssembler(SampleForPersistence activity, ViewGroup container) {
        super(activity, container);
    }

    @Override
    public Object findModule(Class<?> filedClass, int id) {
        if (filedClass.equals(SampleForPersistencePresenter.class)) {
            return new SampleForPersistencePresenter(
                    new TaskRunnerAsyncShared<SampleForPersistencePresenter.View>(activity),
                    dbManager);
        }

        if (filedClass.equals(DbItemViewModelUtil.class)) {
            return new DbItemViewModelUtil();
        }

        return super.findModule(filedClass, id);
    }

    @Override
    public void setField(Field filed, Object object, Object value) throws IllegalAccessException {
        filed.set(object, value);
    }
}
