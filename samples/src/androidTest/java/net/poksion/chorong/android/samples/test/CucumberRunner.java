package net.poksion.chorong.android.samples.test;

import cucumber.api.CucumberOptions;

@CucumberOptions(
        features = "features",
        glue = "net.poksion.chorong.android.samples.test.steps"
)
public class CucumberRunner {

}
