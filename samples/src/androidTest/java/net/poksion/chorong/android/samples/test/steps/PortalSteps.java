package net.poksion.chorong.android.samples.test.steps;

import cucumber.api.java.en.Then;
import net.poksion.chorong.android.samples.test.fixtures.AppFixture;

public class PortalSteps {
    @Then("^The sample portal should be displayed$")
    public void sample_portal_should_be_displayed() {

        AppFixture.onThen();
    }
}
