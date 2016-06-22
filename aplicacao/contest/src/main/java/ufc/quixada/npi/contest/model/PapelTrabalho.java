package ufc.quixada.npi.contest.model;

public enum PapelTrabalho {
	AUTOR("Autor"), COAUTOR("Coautor"), ORIENTADOR("Orientador"), REVISOR("Revisor");

	private String papel;

	PapelTrabalho(String papel) {
		this.setPapel(papel);
	}

	public String getPapel() {
		return papel;
	}

	private void setPapel(String papel) {
		this.papel = papel;
	}
}