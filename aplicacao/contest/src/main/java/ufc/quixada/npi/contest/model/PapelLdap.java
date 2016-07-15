package ufc.quixada.npi.contest.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.security.core.GrantedAuthority;

public class PapelLdap implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

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

	public void setNome(Tipo nome) {
		this.nome = nome;
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

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}

	}

	@Override
	public String getAuthority() {
		return this.nome.name();
	}

	@Override
	public String toString() {
		return "PapelLdap [nome=" + nome + "]";
	}
}