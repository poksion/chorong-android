package net.poksion.chorong.android.module;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import org.junit.Test;

public class ViewModuleIndexedProviderTest {
    private static class DummyModule {
        String getName() {
            return "dummy-module";
        }
    }

    private static class DummyModuleGeneric<T> {
        String getName() {
            return "dummy-module-generic";
        }
    }

    private static class DummyModuleHolder {
        @Assemble DummyModule dummyModule;
    }

    private static class DummyModuleGenericHolder {
        @Assemble DummyModuleGeneric<Integer> dummyModuleGeneric;
    }

    @Test
    public void indexed_provider_should_providing_with_given_type() {
        DummyModuleHolder moduleHolder = new DummyModuleHolder();

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

        ModuleFactory.assemble(DummyModuleHolder.class, moduleHolder, viewModuleAssembler);

        assertThat(moduleHolder.dummyModule).isNotNull();
        assertThat(moduleHolder.dummyModule.getName()).isEqualTo("dummy-module");
    }

    @Test(expected=IllegalArgumentException.class)
    public void generic_class_cannot_be_index_class() {
        DummyModuleGenericHolder moduleHolder = new DummyModuleGenericHolder();

        ViewModuleAssembler viewModuleAssembler = new ViewModuleAssembler(null, null) {
            @Override
            protected void onInit(Factory factory) {
                // Generic : DummyModuleGeneric<Integer>
                factory.addIndexedProvider(new IndexedProvider<DummyModuleGeneric<Integer>>() {
                    @Override
                    protected DummyModuleGeneric<Integer> provide() {
                        return new DummyModuleGeneric<>();
                    }
                });
            }

            @Override
            public void setField(Field field, Object object, Object value) throws IllegalAccessException {
                field.set(object, value);
            }
        };

        ModuleFactory.assemble(DummyModuleGenericHolder.class, moduleHolder, viewModuleAssembler);
    }

    @Test
    public void raw_type_of_generic_should_be_accepted_as_index_class() {
        DummyModuleGenericHolder moduleHolder = new DummyModuleGenericHolder();

        ViewModuleAssembler viewModuleAssembler = new ViewModuleAssembler(null, null) {
            @Override
            protected void onInit(Factory factory) {
                // raw : DummyModuleGeneric
                factory.addIndexedProvider(new IndexedProvider<DummyModuleGeneric>() {
                    @Override
                    protected DummyModuleGeneric provide() {
                        return new DummyModuleGeneric<Integer>();
                    }
                });
            }

            @Override
            public void setField(Field field, Object object, Object value) throws IllegalAccessException {
                field.set(object, value);
            }
        };

        ModuleFactory.assemble(DummyModuleGenericHolder.class, moduleHolder, viewModuleAssembler);

        assertThat(moduleHolder.dummyModuleGeneric).isNotNull();
        assertThat(moduleHolder.dummyModuleGeneric.getName()).isEqualTo("dummy-module-generic");
    }
}
