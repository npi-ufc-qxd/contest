package ufc.quixada.npi.contest;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@SpringApplicationConfiguration(classes = ContestApplication.class)
@WebAppConfiguration
@CucumberOptions (features = "classpath:features")
public class CucumberExecute {
}