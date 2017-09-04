package net.poksion.chorong.android.module;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Field;
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

    private static class TestModuleAssembler extends ModuleAssembler {

        @Assemble DummyModule dummyModule;

        @Override
        public Object findModule(Class<?> fieldClass, int id) {
            if (id > 0) {
                if (id == 1) {
                    return "id-assembled-1";
                }

                if (id == 2) {
                    return "id-assembled-2";
                }

                return null;
            }

            return super.findModule(fieldClass, id);
        }

        @Override
        public void setField(Field field, Object object, Object value) throws IllegalAccessException {
            field.set(object, value);
        }
    }

    @Before
    public void setUp() {
        assembledDummyModule = null;
        idAssembled1 = null;
        idAssembled2 = null;

        ModuleFactory.reset();
        ModuleFactory.init(this, new ModuleFactory.Initializer() {
            @Override
            public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {
                singletonBinder.bind(DummyModule.class, new DummyModule("dummy-module"));
            }
        });
    }

    @Test
    public void assembler_should_set_with_assemble_annotation() {
        ModuleFactory.assemble(ModuleAssemblerTest.class, this, new TestModuleAssembler());

        assertThat(assembledDummyModule.value).isEqualTo("dummy-module");

        assertThat(idAssembled1).isEqualTo("id-assembled-1");
        assertThat(idAssembled2).isEqualTo("id-assembled-2");
    }

    @Test
    public void assembler_can_have_assembling_field_itself() {
        TestModuleAssembler assembler = new TestModuleAssembler();
        ModuleFactory.assemble(ModuleAssemblerTest.class, this, assembler);

        assertThat(assembler.dummyModule).isNotNull();
        assertThat(assembler.dummyModule.value).isEqualTo("dummy-module");
    }

    @Test
    public void check_error_if_does_not_have_access_right() {
        PrivateMemberClass testClassForNonAccessibleField = new PrivateMemberClass();
        boolean caught = false;
        try {
            ModuleFactory.assemble(PrivateMemberClass.class, testClassForNonAccessibleField, new TestModuleAssembler());
            fail("impossible since non-accessible on PrivateMemberClass fields");
        } catch(Exception e) {
            caught = true;
        }

        assertThat(caught).isTrue();
    }

}

@SuppressWarnings("unused")
class PrivateMemberClass {
    private @Assemble(1) String idAssembled1;
    private @Assemble(2) String idAssembled2;
}