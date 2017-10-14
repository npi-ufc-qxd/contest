package ufc.quixada.npi.contest;

import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import ufc.quixada.npi.contest.config.ContestApplication;

@ContextConfiguration(classes = ContestApplication.class, loader = SpringApplicationContextLoader.class)
@WebAppConfiguration
@IntegrationTest
public class ContestApplicationTests {

}