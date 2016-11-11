package ufc.quixada.npi.contest.model;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "submissao")
public class Submissao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "data_submissao")
	@Temporal(TemporalType.DATE)
	private Date dataSubmissao;

	@Column(name = "tipo_submissao")
	@Enumerated(EnumType.STRING)
	private TipoSubmissao tipoSubmissao;

	@ManyToOne(cascade=CascadeType.PERSIST)
	private Trabalho trabalho;

	public Submissao(){
		this.setDataSubmissao(new Date());
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataSubmissao() {
		return dataSubmissao;
	}

	public void setDataSubmissao(Date dataSubmissao) {
		this.dataSubmissao = dataSubmissao;
	}

	public TipoSubmissao getTipoSubmissao() {
		return tipoSubmissao;
	}

	public void setTipoSubmissao(TipoSubmissao tipoSubmissao) {
		this.tipoSubmissao = tipoSubmissao;
	}

	public Trabalho getTrabalho() {
		return trabalho;
	}

	public void setTrabalho(Trabalho trabalho) {
		this.trabalho = trabalho;
		if(trabalho.getEvento().isPeriodoInicial()){
			this.setTipoSubmissao(TipoSubmissao.PARCIAL);
		}else {
			this.setTipoSubmissao(TipoSubmissao.FINAL);
		}
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
		Submissao other = (Submissao) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Submissao [id=" + id + ", dataSubmissao=" + dataSubmissao + ", tipoSubmissao=" + tipoSubmissao
				+ ", trabalho=" + trabalho + "]";
	}
}