package ufc.quixada.npi.contest.model;

public enum Papel {
	ORGANIZADOR("Organizador"), REVISOR("Revisor"), AUTOR("Autor"), COAUTOR("Coautor"), ORIENTADOR("Orientador");

	private String papel;

	Papel(String papel) {
		this.setPapel(papel);
	}

	public String getPapel() {
		return papel;
	}

	private void setPapel(String papel) {
		this.papel = papel;
	}
}