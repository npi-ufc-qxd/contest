package ufc.quixada.npi.contest.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.security.core.GrantedAuthority;

public class PapelSistema implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	
	@Enumerated(EnumType.STRING)
	private Papel papel;
	
	public PapelSistema(Papel papel){
		this.papel = papel;
	}

	public enum Papel {
		ORGANIZADOR("ORGANIZADOR"), REVISOR("REVISOR"), AUTOR("AUTOR"), 
		COAUTOR("COAUTOR"), ORIENTADOR("ORIENTADOR"), USER("USER"), ADMIN("ADMIN");
		
		private String tipo;

		Papel(String tipo) {
			this.tipo = tipo;
		}

		public String getTipo() {
			return tipo;
		}

		public void setTipo(String tipo) {
			this.tipo = tipo;
		}
	}

	@Override
	public String getAuthority() {
		return "ROLE_" + this.papel.name();
	}
	
	@Override
	public String toString() {
		return "Papel [nome=" + papel.getTipo() + "]";
	}
}