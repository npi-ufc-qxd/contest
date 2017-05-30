package ufc.quixada.npi.contest.model;

import java.util.ArrayList;
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
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import ufc.quixada.npi.contest.model.Papel.Tipo;

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
	@OrderBy("papel")
	private List<ParticipacaoTrabalho> participacoes;

	@Column(name="path")
	private String path;
	
	@Transient
	private String coautoresInString;
	
	@ManyToOne
	private Secao secao;
	
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
	
	public void setAutores(Pessoa autor, List<Pessoa> coautores){
		ParticipacaoTrabalho participacaoAutor = new ParticipacaoTrabalho();
		participacaoAutor.setPapel(Tipo.AUTOR);
		participacaoAutor.setTrabalho(this);
		participacaoAutor.setPessoa(autor);
		
		participacoes = new ArrayList<ParticipacaoTrabalho>();
		for (Pessoa pessoa : coautores) {
			ParticipacaoTrabalho participacaoCoautor = new ParticipacaoTrabalho();
			participacaoCoautor.setPapel(Tipo.COAUTOR);
			participacaoCoautor.setTrabalho(this);
			participacaoCoautor.setPessoa(pessoa);
			participacoes.add(participacaoCoautor);
		}
		participacoes.add(participacaoAutor);
	}
	
	public void setCoautores(List<Pessoa> coautores) {
		
		for (Pessoa pessoa : coautores) {
			ParticipacaoTrabalho participacaoCoautor = new ParticipacaoTrabalho();
			participacaoCoautor.setPapel(Tipo.COAUTOR);
			participacaoCoautor.setTrabalho(this);
			participacaoCoautor.setPessoa(pessoa);
			participacoes.add(participacaoCoautor);
		}
	}
	
	private List<Pessoa> getParticipacaoPapelTrabalho(Tipo... papeis) {
		List<Pessoa> pessoa = new ArrayList<Pessoa>();
		for (ParticipacaoTrabalho p : getParticipacoes()) {
			for(Tipo papel : papeis){
				if (p.getPapel() == papel){
					pessoa.add(p.getPessoa());
				}
			}
		}
		return pessoa;
	}
	
	public String getCoautoresInString(){
		List<Pessoa> lista = this.getCoAutoresDoTrabalho();
		if(lista!=null){
			StringBuilder nomes = new StringBuilder();
			for (Pessoa p : lista) {
				nomes.append(p.getNome().toUpperCase());
				if(lista.indexOf(p)!=(lista.size()-1)){
					nomes.append(", ");
				}
			}
			return nomes.toString();
		}
		return "";
	}

	public Pessoa getAutor() {
		return getParticipacaoPapelTrabalho(Tipo.AUTOR).get(0);
	}
	
	public List<Pessoa> getAutoresDoTrabalho() {
		return getParticipacaoPapelTrabalho(Tipo.AUTOR, Tipo.COAUTOR);
	}
	
	public List<Pessoa> getCoAutoresDoTrabalho() {
		return getParticipacaoPapelTrabalho(Tipo.COAUTOR);
	}
	
	public List<Pessoa> getRevisores(){
		return getParticipacaoPapelTrabalho(Tipo.REVISOR);
	}
	
	public boolean isRevisado(){
		return (revisoes != null && revisoes.size() > 0) ? true : false;
	}
	
	public boolean isIndicadoMelhoresTrabalhos(){
		if(this.isRevisado()){
			for(Revisao revisao : revisoes){
				if(revisao.getConteudo().contains("indicacao")) return true;
			}
		}
		
		return false;
	}
	
	public Avaliacao getStatus(){
		if(this.isRevisado()){
			for(Revisao revisao : revisoes){
				return revisao.getAvaliacao();
			}
		}
		return null;
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

	public Secao getSecao() {
		return secao;
	}

	public void setSecao(Secao secao) {
		this.secao = secao;
	}

	
	
	
}