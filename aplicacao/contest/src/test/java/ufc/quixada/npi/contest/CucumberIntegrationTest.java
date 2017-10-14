package ufc.quixada.npi.contest;


import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/java/features", plugin = { "pretty", "html:target/cucumber" })
public class CucumberIntegrationTest {
}