package ufc.quixada.npi.contest.model;

public enum VisibilidadeEvento {
	PUBLICO("Publico"), PRIVADO("Privado");

	private String visibilidade;

	VisibilidadeEvento(String visibilidade) {
		this.visibilidade = visibilidade;
	}

	public String getVisibilidade() {
		return visibilidade;
	}
}