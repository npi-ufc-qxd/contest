package ufc.quixada.npi.contest;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(Cucumber.class)
@SpringApplicationConfiguration(classes = ContestApplication.class)
@WebAppConfiguration
@CucumberOptions (features = "classpath:features", glue={"ufc.quixada.npi.contest"})
public class CucumberExecute {
}