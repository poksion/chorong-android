package net.poksion.chorong.android.samples.test.steps;

import cucumber.api.java.en.Given;
import net.poksion.chorong.android.samples.test.fixtures.AppFixture;

public class CommonSteps {

    @Given("^The app starts$")
    public void start_app() {
        AppFixture.startApp();
    }

}
