package ufc.quixada.npi.contest.model;

public enum TipoSubmissao {
	PARCIAL("Parcial"), FINAL("Final");

	private String tipoSubmissao;

	TipoSubmissao(String tipoSubmissao) {
		this.setTipoSubmissao(tipoSubmissao);
	}

	public String getTipoSubmissao() {
		return tipoSubmissao;
	}

	public void setTipoSubmissao(String tipoSubmissao) {
		this.tipoSubmissao = tipoSubmissao;
	}
}