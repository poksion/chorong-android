package net.poksion.chorong.android.module;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import org.junit.Before;
import org.junit.Test;

public class ViewModuleIndexedProviderTest {
    private static class DummyModule {
        String getName() {
            return "dummy-module";
        }
    }

    @Assemble DummyModule dummyModule;

    @Before
    public void setUp() {
        dummyModule = null;
    }

    @Test
    public void indexed_provider_should_providing_with_given_type() {
        ViewModuleAssembler viewModuleAssembler = new ViewModuleAssembler(null, null) {
            @Override
            protected void onInit(Factory factory) {
                factory.addIndexedProvider(new IndexedProvider<DummyModule>() {
                    @Override
                    protected DummyModule provide() {
                        return new DummyModule();
                    }
                });
            }

            @Override
            public void setField(Field field, Object object, Object value) throws IllegalAccessException {
                field.set(object, value);
            }
        };

        ModuleFactory.assemble(ViewModuleIndexedProviderTest.class, this, viewModuleAssembler);
        assertThat(dummyModule).isNotNull();
        assertThat(dummyModule.getName()).isEqualTo("dummy-module");
    }
}
