package ufc.quixada.npi.contest.model;

public enum PapelEvento {
	ADMINISTRADOR("Administrador"), ORGANIZADOR("Organizador"), REVISOR("Revisor");

	private String papel;

	PapelEvento(String papel) {
		this.setPapel(papel);
	}

	public String getPapel() {
		return papel;
	}

	private void setPapel(String papel) {
		this.papel = papel;
	}
}