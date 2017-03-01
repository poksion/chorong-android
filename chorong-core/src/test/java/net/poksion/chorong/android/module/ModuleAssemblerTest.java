package net.poksion.chorong.android.module;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import net.poksion.chorong.android.annotation.Assemble;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("WeakerAccess")
public class ModuleAssemblerTest {

    @Assemble DummyModule assembledDummyModule;
    @Assemble(1) String idAssembled1;
    @Assemble(2) String idAssembled2;

    private static class DummyModule {
        final String value;

        DummyModule(String value) {
            this.value = value;
        }
    }

    @Before
    public void setUp() {
        assembledDummyModule = null;
        idAssembled1 = null;
        idAssembled2 = null;

        ModuleFactory.reset();
    }

    @Test
    public void assembler_should_set_with_assemble_annotation() {

        ModuleFactory.init(this, new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {
                singletonBinder.bind(DummyModule.class, new DummyModule("dummy-module"));
            }
        });

        ModuleFactory.assemble(this, new Assembler() {
            @Override
            public Object findModule(Class<?> filedClass, int id) {
                if (id > 0) {
                    if (id == 1) {
                        return "id-assembled-1";
                    }

                    if (id == 2) {
                        return "id-assembled-2";
                    }

                    return null;
                }

                return ModuleFactory.get(filedClass.getName());
            }

            @Override
            public void setField(Field filed, Object object, Object value) throws IllegalAccessException {
                filed.set(object, value);
            }
        });

        assertThat(assembledDummyModule.value).isEqualTo("dummy-module");
        assertThat(idAssembled1).isEqualTo("id-assembled-1");
        assertThat(idAssembled2).isEqualTo("id-assembled-2");
    }

}