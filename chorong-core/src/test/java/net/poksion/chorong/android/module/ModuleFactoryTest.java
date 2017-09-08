package net.poksion.chorong.android.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Before;
import org.junit.Test;

public class ModuleFactoryTest {

    private static final String KEY = "key";
    private static final String MODULE = "test-module";

    private static class DummyModule {
        private static int CONSTRUCTOR_CALLING_CNT = 0;

        DummyModule() {
            CONSTRUCTOR_CALLING_CNT++;
        }

        int getConstructorCallingCnt() {
            return CONSTRUCTOR_CALLING_CNT;
        }
    }

    @Before
    public void setUp() {
        ModuleFactory.reset();

        ModuleFactory.init(this, new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {
                // for general binding
                singletonBinder.bind(KEY, MODULE);

                // for typed binding
                singletonBinder.bind(DummyModule.class, new DummyModule());
            }
        });
    }

    @Test(expected = IllegalAccessException.class)
    public void module_disallow_new_instance() throws IllegalAccessException, InstantiationException {
        Class<ModuleFactory> moduleFactoryClass = ModuleFactory.class;
        moduleFactoryClass.newInstance();
    }

    @Test
    public void module_factory_should_return_request_module() {
        String testModule = (String) ModuleFactory.get(KEY);
        assertThat(testModule).isEqualTo("test-module");
    }

    @Test
    public void requested_module_should_be_singleton() {
        DummyModule dummyModule = ModuleFactory.get(DummyModule.class);
        int currentCallingCnt = dummyModule.getConstructorCallingCnt();

        dummyModule = ModuleFactory.get(DummyModule.class);
        assertThat(dummyModule.getConstructorCallingCnt()).isEqualTo(currentCallingCnt);
    }

    @Test
    public void none_empty_module_factory_does_not_init_again() {
        ModuleFactory.Initializer dummyInitializer = new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {
                fail("already init on setUp");
            }
        };

        ModuleFactory.init(this, dummyInitializer);
    }
}
