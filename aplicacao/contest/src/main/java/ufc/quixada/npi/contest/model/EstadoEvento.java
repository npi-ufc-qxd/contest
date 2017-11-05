package ufc.quixada.npi.contest.model;

public enum EstadoEvento {
	ATIVO("Ativo"), INATIVO("Inativo"), FINALIZADO("Finalizado");

	private String estado;

	EstadoEvento(String estado) {
		this.estado = estado;
	}

	public String getEstado() {
		return estado;
	}
}