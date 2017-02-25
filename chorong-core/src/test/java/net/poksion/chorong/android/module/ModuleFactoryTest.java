package net.poksion.chorong.android.module;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class ModuleFactoryTest {

    private static final String KEY = "key";
    private static final String VALUE = "test-module-1";

    @Before
    public void resetModules() {
        ModuleFactory.reset();

        ModuleFactory.init(this, new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, Map<String, Object> moduleMap) {
                moduleMap.put(KEY, VALUE);

            }
        });
    }

    @Test
    public void testAcquiringModule() {
        String testModule1 = (String) ModuleFactory.get(KEY);
        assertThat(testModule1).isEqualTo("test-module-1");
    }
}
