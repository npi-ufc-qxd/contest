package ufc.quixada.npi.contest.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ufc.quixada.npi.contest.model.Papel.Tipo;

@Entity
@Table(name = "participacao_evento")
public class ParticipacaoEvento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "papel")
	@Enumerated(EnumType.STRING)
	private Tipo papel;
	
	@ManyToOne(cascade=CascadeType.REFRESH)
	private Pessoa pessoa;
	
	@ManyToOne(cascade=CascadeType.ALL)
	private Evento evento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tipo getPapel() {
		return papel;
	}

	public void setPapel(Tipo papel) {
		this.papel = papel;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public ParticipacaoEvento(Tipo papel, Pessoa pessoa, Evento evento) {
		this.papel = papel;
		this.pessoa = pessoa;
		this.evento = evento;
	}
	
	public ParticipacaoEvento() {
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
		ParticipacaoEvento other = (ParticipacaoEvento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ParticipacaoEvento [id=" + id + ", papel=" + papel + ", pessoa=" + pessoa + ", evento=" + evento + "]";
	}
}