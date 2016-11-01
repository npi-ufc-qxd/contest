package ufc.quixada.npi.contest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "trabalho")
public class Trabalho implements Comparable<Trabalho> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotEmpty
	@Column(name = "titulo")
	private String titulo;

	@ManyToOne
	private Evento evento;

	@ManyToOne
	private Trilha trilha;
	
	@OneToMany(mappedBy="trabalho", cascade=CascadeType.REMOVE)
	@OrderBy("data_submissao")
	private List<Submissao> submissoes;
	

	@OneToMany(mappedBy="trabalho", cascade=CascadeType.REMOVE)
	private List<Revisao> revisoes;

	@OneToMany(mappedBy = "trabalho", cascade=CascadeType.ALL)
	private List<ParticipacaoTrabalho> participacoes;

	@Column(name="path")
	private String path;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Submissao> getSubmissoes() {
		return submissoes;
	}
	
	public void setSubmissoes(List<Submissao> submissoes) {
		this.submissoes = submissoes;
	}
	
	public List<Revisao> getRevisoes() {
		return revisoes;
	}
	
	public void setRevisoes(List<Revisao> revisoes) {
		this.revisoes = revisoes;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public Trilha getTrilha() {
		return trilha;
	}

	public void setTrilha(Trilha trilha) {
		this.trilha = trilha;
	}

	public List<ParticipacaoTrabalho> getParticipacoes() {
		if(participacoes == null){
			return new ArrayList<ParticipacaoTrabalho>();
		}
		return participacoes;
	}

	public void setParticipacoes(List<ParticipacaoTrabalho> participacoes) {
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
		Trabalho other = (Trabalho) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	private List<Pessoa> getParticipacaoPapelTrabalho(Papel... papeis) {
		List<Pessoa> pessoa = new ArrayList<Pessoa>();
		for (ParticipacaoTrabalho p : getParticipacoes()) {
			for(Papel papel : papeis){
				if (p.getPapel() == papel){
					pessoa.add(p.getPessoa());
				}
			}
		}
		return pessoa;
	}
	
	public Pessoa getAutor() {
		return getParticipacaoPapelTrabalho(Papel.AUTOR).get(0);
	}
	
	public List<Pessoa> getAutoresDoTrabalho() {
		return getParticipacaoPapelTrabalho(Papel.AUTOR, Papel.COAUTOR);
	}
	
	public List<Pessoa> getRevisores(){
		return getParticipacaoPapelTrabalho(Papel.REVISOR);
	}
	
	@Override
	public String toString() {
		return "Trabalho [id=" + id + ", titulo=" + titulo + ", evento=" + evento + ", trilha=" + trilha
				+ ", participacoes=" + participacoes + "]";
	}

	@Override
	public int compareTo(Trabalho o) {
		if(this.getRevisores().size() < o.getRevisores().size()){
			return -1;
		}
		if(this.getRevisores().size() > o.getRevisores().size()){
			return 1;
		}
		return 0;
	}
	
	
}