package ufc.quixada.npi.contest.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class PapelLdap {

	@Enumerated(EnumType.STRING)
	private Tipo nome;

	public PapelLdap() {
		super();
	}

	public PapelLdap(Tipo nome) {
		super();
		this.nome = nome;
	}

	public String getNome() {
		return nome.name();
	}

	public enum Tipo {
		ADMIN("ADMIN-CONTEST"), DOCENTE("DOCENTE"), DISCENTE("DISCENTE"), STA("STA");

		private String tipo;

		Tipo(String tipo) {
			this.tipo = tipo;
		}

		public String getTipo() {
			return tipo;
		}
	}
}