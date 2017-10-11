package ufc.quixada.npi.contest.model;

public enum Avaliacao {
	APROVADO("Aprovado"), RESSALVAS("Aprovado com ressalvas"), REPROVADO("Reprovado"), MODERACAO("Moderacao");

	private String tipo;

	Avaliacao(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
