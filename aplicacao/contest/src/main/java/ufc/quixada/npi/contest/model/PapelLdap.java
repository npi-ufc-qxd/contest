package ufc.quixada.npi.contest.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

@Entity
public class PapelLdap implements GrantedAuthority {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	private Tipo nome;

	public PapelLdap() {
		super();
	}

	public PapelLdap(Tipo nome) {
		super();
		this.nome = nome;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome.name();
	}

	public void setNome(Tipo nome) {
		this.nome = nome;
	}

	enum Tipo {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PapelLdap other = (PapelLdap) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PapelLdap [id=" + id + ", nome=" + nome + "]";
	}
}