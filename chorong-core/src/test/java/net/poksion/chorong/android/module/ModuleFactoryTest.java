package net.poksion.chorong.android.module;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
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
            public void onInit(Object host, Map<String, Object> moduleMap) {
                moduleMap.put(KEY, MODULE);
            }
        });
    }

    @Test
    public void module_factory_should_return_request_module() {
        String testModule = (String) ModuleFactory.get(KEY);
        assertThat(testModule).isEqualTo("test-module");
    }
}
