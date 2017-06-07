package ufc.quixada.npi.contest.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.util.*;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Token;
import ufc.quixada.npi.contest.repository.TokenRepository;

@Service
public class TokenService {

	@Autowired
	private TokenRepository tokenRepository;

	public Token buscarPorUsuario(Pessoa pessoa) {
		return tokenRepository.findByUsuario(pessoa);
	}

	public Token buscar(String token) {
		return tokenRepository.findOne(token);
	}

	public boolean existe(String token) {
		return tokenRepository.exists(token);
	}

	public void salvar(Token token) {
		tokenRepository.save(token);
	}

	public void deletar(Token token) {
		tokenRepository.delete(token);
	}
	
	public Token novoToken(Pessoa pessoa, String acao) throws Exception{
		Token token = new Token();
		token.setPessoa(pessoa);
		
		switch(acao){
		case Constants.ACAO_COMPLETAR_CADASTRO:
			token.setAcao(Constants.ACAO_COMPLETAR_CADASTRO);
			break;
		case Constants.ACAO_RECUPERAR_SENHA:
			token.setAcao(Constants.ACAO_RECUPERAR_SENHA);
			break;
		default:
			throw new Exception("Acao não existente para geração do token.");
		}
		
		
		do {
			token.setToken(UUID.randomUUID().toString());
		} while (this.existe(token.getToken()));
		
		this.salvar(token);
		
		return token;
		
	}

}