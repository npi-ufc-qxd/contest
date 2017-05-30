package ufc.quixada.npi.contest.config;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import br.ufc.quixada.npi.ldap.model.Affiliation;
import br.ufc.quixada.npi.ldap.model.Usuario;
import br.ufc.quixada.npi.ldap.service.UsuarioService;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;

@Component
public class AuthenticationProviderContest implements AuthenticationProvider {

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private MessageService messageService;

	@Override
	@Transactional
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		Pessoa pessoa = new Pessoa();
		
		String cpf = authentication.getName();
		String password = authentication.getCredentials().toString();

		final Usuario usuario = usuarioService.getByCpf(cpf);
		pessoa = pessoaService.getByCpf(cpf);

		if (pessoa != null) { // Pessoa existe na Base Local entra no trecho abaixo
			if (pessoaService.autentica(pessoa, cpf, password)){
				return new UsernamePasswordAuthenticationToken(pessoa, pessoaService.encodePassword(password),
						pessoa.getAuthorities());
			}
				
		} else if (usuario != null && usuarioService.autentica(cpf, password)) { 
			
			pessoa = new Pessoa();
			pessoa.setCpf(usuario.getCpf());
			pessoa.setNome(usuario.getNome());
			pessoa.setPassword(pessoaService.encodePassword(password));
			pessoa.setPapel(Tipo.USER);
			pessoa.setEmail(usuario.getEmail());
			
			for (Affiliation affiliation : usuario.getAuthorities()) {
				if (affiliation.getNome().equals(Tipo.ADMIN.getNome())) {
					pessoa.setPapel(Tipo.ADMIN);
				}
			}

			pessoaService.addOrUpdate(pessoa);

			return new UsernamePasswordAuthenticationToken(pessoa, pessoaService.encodePassword(password),
					pessoa.getAuthorities());
		}

		throw new BadCredentialsException(messageService.getMessage("LOGIN_INVALIDO"));
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}