package ufc.quixada.npi.contest.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ufc.quixada.npi.contest.model.Pessoa;

public class PessoaLogada {
	public Pessoa getLogado(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (Pessoa) auth.getPrincipal();
	}

}
