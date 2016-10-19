package ufc.quixada.npi.contest.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class AvaliacaoTrabalho {
	
	@Enumerated(EnumType.STRING)
	private Avaliacao avaliacao;
	
	public enum Avaliacao {
		APROVADO("Aprovado"), RESSALVAS("Aprovado com ressalvas"), REPROVADO("Reprovado");

		private String tipo;
		
		Avaliacao(String tipo){
			this.tipo = tipo;
		}
		
		public String getTipo() {
			return this.tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}

	}

}
