package ufc.quixada.npi.contest.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ufc.quixada.npi.contest.model.PapelLdap.Tipo;

@Entity
@Table(name = "pessoa")
public class Pessoa implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome")
	@NotEmpty
	private String nome;

	@Column(name = "cpf")
	@NotEmpty
	private String cpf;

	@Column(name = "password")
	private String password;

	@Column(name = "email")
	@NotEmpty
	private String email;

	@OneToMany(mappedBy = "pessoa")
	private List<ParticipacaoEvento> participacoesEvento;

	@OneToMany(mappedBy = "pessoa")
	private List<ParticipacaoTrabalho> participacoesTrabalho;

	@OneToOne(cascade = CascadeType.ALL)
	private PapelLdap papelLdap;

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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<ParticipacaoEvento> getParticipacoesEvento() {
		return participacoesEvento;
	}

	public void setParticipacoesEvento(List<ParticipacaoEvento> participacoesEvento) {
		this.participacoesEvento = participacoesEvento;
	}

	public List<ParticipacaoTrabalho> getParticipacoesTrabalho() {
		return participacoesTrabalho;
	}

	public void setParticipacoesTrabalho(List<ParticipacaoTrabalho> participacoesTrabalho) {
		this.participacoesTrabalho = participacoesTrabalho;
	}

	public PapelLdap getPapelLdap() {
		return papelLdap;
	}

	public void setPapelLdap(String papelLdap) throws IllegalArgumentException {
		try {
			PapelLdap papel = new PapelLdap(Tipo.valueOf(papelLdap));
			this.papelLdap = papel;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Papel proveniente do LDAP não condiz com os papéis mapeados pelo sistema");
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
		Pessoa other = (Pessoa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Pessoa [id=" + id + ", nome=" + nome + ", cpf=" + cpf + ", password=" + password + ", email=" + email
				+ ", participacoesEvento=" + participacoesEvento + ", participacoesTrabalho=" + participacoesTrabalho
				+ ", papelLdap=" + papelLdap + "]";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(this.papelLdap);
	}

	@Override
	public String getUsername() {
		return this.cpf;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}