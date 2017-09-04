package net.poksion.chorong.android.module;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
public class ViewModuleAssemblerTest {

    @Assemble(R.id.main_frame) FrameLayout mainFrame;
    @Assemble(R.id.open_btn)   Button openBtn;
    @Assemble                  TestPresenter testPresenter;

    @Before
    public void setUp() {
        mainFrame = null;
        openBtn = null;
        testPresenter = null;
    }

    @Test
    public void view_module_assembler_should_assemble_view_from_activity() {
        ModuleFactory.assemble(ViewModuleAssemblerTest.class, this, new TestViewModuleAssembler(makeMockActivity("dummy-frame", "dummy-btn")));

        assertThat(mainFrame).isNotNull();
        assertThat(mainFrame.getTag()).isEqualTo("dummy-frame");

        assertThat(openBtn).isNotNull();
        assertThat(openBtn.getTag()).isEqualTo("dummy-btn");

        assertThat(testPresenter).isNotNull();
    }

    @Test
    public void view_module_assembler_should_assemble_view_from_view() {
        ModuleFactory.assemble(ViewModuleAssemblerTest.class, this, new TestViewModuleAssembler(makeMockView("dummy-frame", "dummy-btn")));

        assertThat(mainFrame).isNotNull();
        assertThat(mainFrame.getTag()).isEqualTo("dummy-frame");

        assertThat(openBtn).isNotNull();
        assertThat(openBtn.getTag()).isEqualTo("dummy-btn");

        assertThat(testPresenter).isNotNull();
    }

    private Activity makeMockActivity(String mockFrameTag, String mockBtnTag) {
        FrameLayout mockFrameLayout = mock(FrameLayout.class);
        when(mockFrameLayout.getTag()).thenReturn(mockFrameTag);

        Button mockBtn = mock(Button.class);
        when(mockBtn.getTag()).thenReturn(mockBtnTag);

        Activity mockActivity = mock(Activity.class);
        when(mockActivity.findViewById(R.id.main_frame)).thenReturn(mockFrameLayout);
        when(mockActivity.findViewById(R.id.open_btn)).thenReturn(mockBtn);

        return mockActivity;
    }

    private View makeMockView(String mockFrameTag, String mockBtnTag) {
        FrameLayout mockFrameLayout = mock(FrameLayout.class);
        when(mockFrameLayout.getTag()).thenReturn(mockFrameTag);

        Button mockBtn = mock(Button.class);
        when(mockBtn.getTag()).thenReturn(mockBtnTag);

        View mockView = mock(View.class);
        when(mockView.findViewById(R.id.main_frame)).thenReturn(mockFrameLayout);
        when(mockView.findViewById(R.id.open_btn)).thenReturn(mockBtn);

        return mockView;
    }

    private static class TestViewModuleAssembler extends ViewModuleAssembler {

        TestViewModuleAssembler(Activity activity) {
            super(null, activity);
        }

        TestViewModuleAssembler(View view) {
            super(view, null);
        }

        @Override
        protected void onInit(Factory factory) {
            factory.addProvider(new Provider() {
                @Override
                public boolean isMatchedField(Class<?> fieldClass) {
                    return fieldClass.equals(TestPresenter.class);
                }

                @Override
                public Object provide(int id) {
                    return new TestPresenter();
                }
            });

        }

        @Override
        public void setField(Field field, Object object, Object value) throws IllegalAccessException {
            field.set(object, value);
        }
    }

    private static class TestPresenter {

    }

    private static class R {
        static class id {
            final static int main_frame = 1;
            final static int open_btn = 2;
        }
    }
}
