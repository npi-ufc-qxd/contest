package ufc.quixada.npi.contest.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import ufc.quixada.npi.contest.model.Papel.Tipo;

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
	@Column(name = "prazo_submissao_inicial")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date prazoSubmissaoInicial;

	@Temporal(TemporalType.DATE)
	@Column(name = "prazo_submissao_final")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date prazoSubmissaoFinal;

	@Temporal(TemporalType.DATE)
	@Column(name = "prazo_revisao_inicial")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date prazoRevisaoInicial;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "prazo_revisao_final")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date prazoRevisaoFinal;

	@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval=false)
	private List<ParticipacaoEvento> participacoes;
	
	@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval=false)
	@OrderBy("nome ASC")
	private List<Trilha> trilhas;
	
	@OneToMany(mappedBy="evento",cascade=CascadeType.ALL, orphanRemoval=false)
	private List<Secao> secoes;

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

	public Date getPrazoSubmissaoInicial() {
		return prazoSubmissaoInicial;
	}

	public void setPrazoSubmissaoInicial(Date prazoSubmissaoInicial) {
		this.prazoSubmissaoInicial = prazoSubmissaoInicial;
	}
	
	public Date getPrazoSubmissaoFinal() {
		return prazoSubmissaoFinal;
	}

	public void setPrazoSubmissaoFinal(Date prazoSubmissaoFinal) {
		this.prazoSubmissaoFinal = prazoSubmissaoFinal;
	}

	public Date getPrazoRevisaoInicial() {
		return prazoRevisaoInicial;
	}

	public void setPrazoRevisaoInicial(Date prazoRevisaoInicial) {
		this.prazoRevisaoInicial = prazoRevisaoInicial;
	}

	public Date getPrazoRevisaoFinal() {
		return prazoRevisaoFinal;
	}

	public void setPrazoRevisaoFinal(Date prazoRevisaoFinal) {
		this.prazoRevisaoFinal = prazoRevisaoFinal;
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
				+ ", estado=" + estado + ", prazoSubmissaoInicial=" + prazoSubmissaoInicial + ", prazoSubmissaoFinal="
				+ prazoSubmissaoFinal + ", prazoRevisaoInicial=" + prazoRevisaoInicial + ", prazoRevisaoFinal="
				+ prazoRevisaoFinal + ", participacoes=" + participacoes + "]";
	}

	public List<Trilha> getTrilhas() {
		return trilhas;
	}

	public void setTrilhas(List<Trilha> trilhas) {
		this.trilhas = trilhas;
	}

	
	public boolean isPeriodoInicial(){
		Date dataAtual = new Date();
		Calendar cal = Calendar.getInstance();
        cal.setTime(prazoRevisaoInicial);
        cal.add(Calendar.SECOND, -1);
		Date diaAntesDoInicioDaRevisao = cal.getTime();
		return (dataAtual.compareTo(diaAntesDoInicioDaRevisao) <= 0);
	}
	
	public boolean isPeriodoRevisao(){
		Date dataAtual = new Date();
		Calendar cal = Calendar.getInstance();
        cal.setTime(prazoRevisaoFinal);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.SECOND, -1);
		Date dataFinalRevisao = cal.getTime();		
		boolean comecaNoDiaOuAposInicioRevisao = (dataAtual.compareTo(prazoRevisaoInicial)>= 0);
		boolean terminaNoDiaOuAntesFinalRevisao = (dataAtual.compareTo(dataFinalRevisao)<= 0);
		return (comecaNoDiaOuAposInicioRevisao && terminaNoDiaOuAntesFinalRevisao);
	}
	
	public boolean isPeriodoFinal(){
		Date dataAtual = new Date();
		Calendar cal = Calendar.getInstance();
        cal.setTime(prazoRevisaoFinal);
        cal.add(Calendar.DAY_OF_MONTH, 1);
		Date diaAposRevisaoFinal = cal.getTime();
		boolean comecaAposRevisaoFinal = (dataAtual.compareTo(diaAposRevisaoFinal)>= 0);
		
		cal.setTime(prazoSubmissaoFinal);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date diaAposSubmissaoFinal = cal.getTime();
		boolean terminaNoDiaOuAntesSubissaoFinal = (dataAtual.compareTo(diaAposSubmissaoFinal)<= 0);
		return (comecaAposRevisaoFinal && terminaNoDiaOuAntesSubissaoFinal);
	}
	
	private List<Pessoa> getByPapel(Tipo ...papeis){
		List<Pessoa> pessoa = new ArrayList<Pessoa>();
		for (ParticipacaoEvento p : getParticipacoes()) {
			for(Tipo papel : papeis){
				if (p.getPapel() == papel){
					pessoa.add(p.getPessoa());
				}
			}
		}
		return pessoa;
		
	}
	
	public List<Pessoa> getOrganizadores(){
		return getByPapel(Tipo.ORGANIZADOR);
	}
	
	public List<String> getNomeOrganizadores(){
		
		List<Pessoa> pessoas = new ArrayList<>();
		List<String> nomePessoas = new ArrayList<>();
		pessoas = this.getOrganizadores();
		for(Pessoa p : pessoas){
			nomePessoas.add(p.getNome());
		}
		
		return nomePessoas;
	}
	
	
	public List<Pessoa> getRevisores(){
		return getByPapel(Tipo.REVISOR);
	}

	public List<Secao> getSecoes() {
		return secoes;
	}

	public void setSecoes(List<Secao> secoes) {
		this.secoes = secoes;
	}
	
}