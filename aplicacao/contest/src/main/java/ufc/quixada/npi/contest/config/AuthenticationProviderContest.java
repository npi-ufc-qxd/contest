package ufc.quixada.npi.contest.config;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import br.ufc.quixada.npi.ldap.model.Usuario;
import br.ufc.quixada.npi.ldap.service.UsuarioService;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.repository.PessoaRepository;
import ufc.quixada.npi.contest.util.Constants;

@Component
public class AuthenticationProviderContest implements AuthenticationProvider {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private UsuarioService usuarioService;

	@Override
	@Transactional
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String cpf = authentication.getName();
		String password = (String) authentication.getCredentials();

		Pessoa pessoa = pessoaRepository.getByCpf(cpf);

		if (usuarioService.autentica(cpf, password)) {
			Usuario usuario = usuarioService.getByCpf(cpf);

			if (pessoa == null) {
				pessoa = new Pessoa();
				pessoa.setCpf(usuario.getCpf());
				pessoa.setEmail(usuario.getEmail());
				pessoa.setNome(usuario.getNome());
				pessoa.setPassword(usuario.getPassword());
				pessoa.setPapelLdap(usuario.getAuthorities().get(0).getNome());

				pessoaRepository.save(pessoa);
			} else if (pessoa.getAuthorities() == null || pessoa.getAuthorities().isEmpty()) {
				pessoa.setPapelLdap(usuario.getAuthorities().get(0).getNome());
			}

			return new UsernamePasswordAuthenticationToken(pessoa, password, pessoa.getAuthorities());
		} else {
			throw new BadCredentialsException(Constants.LOGIN_INVALIDO);
		}
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}