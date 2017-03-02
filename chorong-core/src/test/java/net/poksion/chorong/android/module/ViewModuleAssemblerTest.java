package net.poksion.chorong.android.module;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import net.poksion.chorong.android.annotation.Assemble;

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

        ModuleFactory.reset();
        ModuleFactory.init(this, new TestModuleInitializer());
    }

    @Test
    public void view_module_assembler_should_assemble_view_from_activity() {
        ModuleFactory.assemble(this, new TestViewModuleAssembler(makeMockActivity("dummy-frame", "dummy-btn")));

        assertThat(mainFrame).isNotNull();
        assertThat(mainFrame.getTag()).isEqualTo("dummy-frame");

        assertThat(openBtn).isNotNull();
        assertThat(openBtn.getTag()).isEqualTo("dummy-btn");

        assertThat(testPresenter).isNotNull();
    }

    @Test
    public void view_module_assembler_should_assemble_view_from_view() {
        ModuleFactory.assemble(this, new TestViewModuleAssembler(makeMockView("dummy-frame", "dummy-btn")));

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
            super(activity);
        }

        TestViewModuleAssembler(View view) {
            super(view);
        }

        @Override
        public void setField(Field filed, Object object, Object value) throws IllegalAccessException {
            filed.set(object, value);
        }
    }

    private static class TestModuleInitializer implements ModuleFactory.Initializer {
        @Override
        public void onInit(Object host, ModuleFactory.SingletonBinder singletonBinder) {
            singletonBinder.bind(TestPresenter.class, new TestPresenter());
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
