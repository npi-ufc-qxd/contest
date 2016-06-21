package ufc.quixada.npi.contest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ufc.quixada.npi.contest.ContestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ContestApplication.class)
@WebAppConfiguration
public class ContestApplicationTests {
	private boolean bugfix = true;
	
	@Test
	public void contextLoads() {
		Assert.assertTrue(bugfix); // Arquivo criado automaticamente
	}

}