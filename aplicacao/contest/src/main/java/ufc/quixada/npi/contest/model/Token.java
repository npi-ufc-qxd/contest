package ufc.quixada.npi.contest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Token {

	@Id
	@Column(name="token")
	private String codigoToken;

	@OneToOne
	private Pessoa pessoa;
	
	private String acao;
	
	

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getToken() {
		return codigoToken;
	}

	public void setToken(String token) {
		this.codigoToken = token;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigoToken == null) ? 0 : codigoToken.hashCode());
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
		Token other = (Token) obj;
		if (codigoToken == null) {
			if (other.codigoToken != null)
				return false;
		} else if (!codigoToken.equals(other.codigoToken))
			return false;
		return true;
	}

}