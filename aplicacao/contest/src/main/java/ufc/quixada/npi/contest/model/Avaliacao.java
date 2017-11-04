package ufc.quixada.npi.contest.model;

public enum Avaliacao {
	APROVADO("Aprovado"), RESSALVAS("Aprovado com ressalvas"),
	REPROVADO("Reprovado"), MODERACAO("Em moderação"), NAO_REVISADO("Não revisado");

	private String descricao;

	Avaliacao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
