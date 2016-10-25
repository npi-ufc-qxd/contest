package ufc.quixada.npi.contest.model;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.print.attribute.standard.DateTimeAtCompleted;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

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

	@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
	private List<ParticipacaoEvento> participacoes;
	
	@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
	private List<Trilha> trilhas;

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
		Date diaAntesDoInicioDaRevisao = alterarDataEmDias(prazoRevisaoInicial, -1);
		return (dataAtual.compareTo(diaAntesDoInicioDaRevisao) < 0);
	}
	
	public boolean isPeriodoRevisao(){
		Date dataAtual = new Date();
		boolean comecaNoDiaOuAposInicioRevisao = (dataAtual.compareTo(prazoRevisaoInicial)>= 0);
		boolean terminaNoDiaOuAntesFinalRevisao = (dataAtual.compareTo(prazoRevisaoFinal)<= 0);
		return (comecaNoDiaOuAposInicioRevisao && terminaNoDiaOuAntesFinalRevisao);
	}
	
	public boolean isPeriodoFinal(){
		Date dataAtual = new Date();
		Date diaAposRevisaoFinal = alterarDataEmDias(prazoRevisaoFinal, 1);
		boolean comecaAposRevisaoFinal = (dataAtual.compareTo(diaAposRevisaoFinal)>= 0);
		boolean terminaNoDiaOuAntesSubissaoFinal = (dataAtual.compareTo(prazoSubmissaoFinal)<= 0);
		return (comecaAposRevisaoFinal && terminaNoDiaOuAntesSubissaoFinal);
	}
	
	//Se o valor de dias for negativo a quantidade sera subtraida
	private Date alterarDataEmDias(Date date, int dias)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, dias);
        return cal.getTime();
    }
}