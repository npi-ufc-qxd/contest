package ufc.quixada.npi.contest.config;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import br.ufc.quixada.npi.ldap.model.Usuario;
import br.ufc.quixada.npi.ldap.service.UsuarioService;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.PessoaService;

@Component
public class AuthenticationProviderContest implements AuthenticationProvider {

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private MessageSource messages;

	@Override
	@Transactional
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String cpf = (String) authentication.getName();
		String password = (String) authentication.getCredentials();

		Pessoa pessoa = pessoaService.getByCpf(cpf);

		if (pessoa != null) { // Pessoa existe
			if(!pessoaService.autentica(pessoa, cpf, password))
				throw new BadCredentialsException(messages.getMessage("LOGIN_INVALIDO", null, null));
		}
		else if (usuarioService.autentica(cpf, password)) { // Pessoa não existe, então tenta autenticar via LDAP
			Usuario usuario = usuarioService.getByCpf(cpf);

			pessoa = new Pessoa();
			pessoa.setCpf(cpf);
			pessoa.setEmail(usuario.getEmail());
			pessoa.setNome(usuario.getNome());
			pessoa.setPassword(pessoaService.encodePassword(password));
			pessoa.setPapelLdap(usuario.getAuthorities().get(0).getNome());

			pessoaService.addOrUpdate(pessoa);
		}
		else {
			throw new BadCredentialsException(messages.getMessage("LOGIN_INVALIDO", null, null));
		}

		return new UsernamePasswordAuthenticationToken(pessoa, pessoaService.encodePassword(password), pessoa.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}