package ufc.quixada.npi.contest.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ufc.quixada.npi.contest.model.Pessoa;


public class PessoaLogadaUtil {
	
	private PessoaLogadaUtil(){
		throw new IllegalStateException("Utility class");  
	}
	
	public static Pessoa pessoaLogada(){
		Pessoa pessoa = (Pessoa) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return pessoa;
	}
	
	public static void refreshPessoaLogada(Pessoa pessoa){
		Authentication newAuth = new UsernamePasswordAuthenticationToken(pessoa, pessoa.getPassword(),pessoa.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(newAuth);
	}
}
