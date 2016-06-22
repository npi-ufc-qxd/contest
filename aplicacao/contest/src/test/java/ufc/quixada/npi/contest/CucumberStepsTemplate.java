package ufc.quixada.npi.contest;

import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;

public class CucumberStepsTemplate {

	@Dado("^Escreva aqui a definição de Dado$")
    public void casoTesteDado() throws Throwable {
	// caso de teste
    }
	
	@Quando("^Escreva aqui a definição de Quando$")
    public void casoTesteQuando() throws Throwable {
	// caso de teste
    }
	
	@E("^Escreva aqui a definição de E$")
    public void casoTesteE() throws Throwable {
	// caso de teste	
    }
	
	@Então("^Escreva aqui a definição de Então$")
    public void casoTesteEntao() throws Throwable {
	// caso de teste	
    }
	
}