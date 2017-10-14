package ufc.quixada.npi.contest.util;

import br.ufc.quixada.npi.ldap.model.Usuario;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.Pessoa;

public class ContestUtil {
	
	private ContestUtil() {
		throw new IllegalStateException("Utility class");
	}
	
	public static Pessoa convertUsuarioToPessoa(String encondedPassword, final Usuario usuario) {
		Pessoa pessoa;
		pessoa = new Pessoa();
		pessoa.setCpf(usuario.getCpf());
		pessoa.setNome(usuario.getNome());
		pessoa.setPassword(encondedPassword);
		pessoa.setPapel(Tipo.USER);
		pessoa.setEmail(usuario.getEmail());
		return pessoa;
	}

}
