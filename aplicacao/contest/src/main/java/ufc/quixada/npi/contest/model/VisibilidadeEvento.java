package ufc.quixada.npi.contest.model;

public enum VisibilidadeEvento {
	PUBLICO("Publico"), PRIVADO("Privado");

	private String visibilidade;

	VisibilidadeEvento(String visibilidade) {
		this.setVisibilidade(visibilidade);
	}

	public String getVisibilidade() {
		return visibilidade;
	}

	public void setVisibilidade(String visibilidade) {
		this.visibilidade = visibilidade;
	}
}