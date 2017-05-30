package ufc.quixada.npi.contest.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.security.core.GrantedAuthority;

public class Papel implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	
	@Enumerated(EnumType.STRING)
	private Tipo papel;
	
	public Papel(Tipo papel){
		this.papel = papel;
	}

	public enum Tipo {
		ORGANIZADOR("ORGANIZADOR"), REVISOR("REVISOR"), AUTOR("AUTOR"), 
		COAUTOR("COAUTOR"), ORIENTADOR("ORIENTADOR"), USER("USER"), ADMIN("ADMIN");
		
		private String nome;

		Tipo(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}
	}

	@Override
	public String getAuthority() {
		return "ROLE_" + this.papel.name();
	}
	
	@Override
	public String toString() {
		return "Papel [nome=" + papel.getNome() + "]";
	}
}