package net.poksion.chorong.android.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.Before;
import org.junit.Test;

public class ModuleFactoryTest {

    private static final String KEY = "key";
    private static final String MODULE = "test-module";

    @Before
    public void setUp() {
        ModuleFactory.reset();

        ModuleFactory.init(this, new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {
                singletonBinder.bind(KEY, MODULE);
            }
        });
    }

    @Test
    public void module_factory_should_return_request_module() {
        String testModule = (String) ModuleFactory.get(KEY);
        assertThat(testModule).isEqualTo("test-module");
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
