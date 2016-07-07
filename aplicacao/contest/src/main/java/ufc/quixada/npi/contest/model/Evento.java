package ufc.quixada.npi.contest.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "evento")
public class Evento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotEmpty(message = "{NOME_EVENTO_VAZIO_ERROR}")
	@Column(name = "nome")
	private String nome;

	@Column(name = "descricao")
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(name = "visibilidade")
	private VisibilidadeEvento visibilidade;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado")
	private EstadoEvento estado;

	@Temporal(TemporalType.DATE)
	@Column(name = "prazo_submissao")
	private Date prazoDeSubmissao;

	@Temporal(TemporalType.DATE)
	@Column(name = "prazo_revisao")
	private Date prazoDeRevisao;

	@OneToMany(mappedBy = "evento")
	private List<ParticipacaoEvento> participacoes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public VisibilidadeEvento getVisibilidade() {
		return visibilidade;
	}

	public void setVisibilidade(VisibilidadeEvento visibilidade) {
		this.visibilidade = visibilidade;
	}

	public EstadoEvento getEstado() {
		return estado;
	}

	public void setEstado(EstadoEvento estado) {
		this.estado = estado;
	}

	public Date getPrazoDeSubmissao() {
		return prazoDeSubmissao;
	}

	public void setPrazoDeSubmissao(Date prazoDeSubmissao) {
		this.prazoDeSubmissao = prazoDeSubmissao;
	}

	public Date getPrazoDeRevisao() {
		return prazoDeRevisao;
	}

	public void setPrazoDeRevisao(Date prazoDeRevisao) {
		this.prazoDeRevisao = prazoDeRevisao;
	}

	public List<ParticipacaoEvento> getParticipacoes() {
		return participacoes;
	}

	public void setParticipacoes(List<ParticipacaoEvento> participacoes) {
		this.participacoes = participacoes;
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
		Evento other = (Evento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Evento [id=" + id + ", nome=" + nome + ", descricao=" + descricao + ", visibilidade=" + visibilidade
				+ ", estado=" + estado + ", prazoDeSubmissao=" + prazoDeSubmissao + ", prazoDeRevisao=" + prazoDeRevisao
				+ ", participacoes=" + participacoes + "]";
	}
}