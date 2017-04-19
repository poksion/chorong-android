package net.poksion.chorong.android.samples;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule public final ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void assemble_annotation_should_be_worked_so_not_null() {
        MainActivity mainActivity = mainActivityTestRule.getActivity();

        assertNotNull(mainActivity.buttonContainer);
    }

    @Test
    public void given_menu_info_should_be_displayed() {
        MainActivity mainActivity = mainActivityTestRule.getActivity();
        assertEquals(1, mainActivity.getMenuInfoList().size());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText(R.string.menu_info)).check(matches(isDisplayed()));
    }
}
