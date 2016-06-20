package ufc.quixada.npi.contest;

import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;

public class CucumberSteps {

	@Dado("^teste um$")
    public void testeUm() throws Throwable {
	// caso de teste
    }
	
	@Quando("^teste dois$")
    public void testeDois() throws Throwable {
	// caso de teste
    }
	
	@E("^teste tres$")
    public void testeTres() throws Throwable {
	// caso de teste	
    }
	
	@Então("^teste quatro$")
    public void testeQuatro() throws Throwable {
	// caso de teste	
    }
	
}