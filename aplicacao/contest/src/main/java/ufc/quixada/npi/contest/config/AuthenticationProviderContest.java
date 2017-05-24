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
import ufc.quixada.npi.contest.model.PapelLdap.Tipo;
import ufc.quixada.npi.contest.model.PapelSistema.Papel;
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
		String cpf = authentication.getName();
		String password = authentication.getCredentials().toString();
		
		Usuario usuario = usuarioService.getByCpf(cpf);
		Pessoa pessoa = pessoaService.getByCpf(cpf);

				if (usuario != null || pessoa !=null) { // Pessoa existe
					if (pessoaService.autentica(pessoa, cpf, password))
						return new UsernamePasswordAuthenticationToken(pessoa, pessoaService.encodePassword(password),
								pessoa.getAuthorities());
				}else if (usuarioService.autentica(cpf, password)) { // Pessoa não existe, então tenta autenticar via LDAP
				
					if(pessoa == null){
						pessoa = new Pessoa();
						pessoa.setEmail(usuario.getEmail());
					}
					pessoa.setCpf(cpf);
					pessoa.setNome(usuario.getNome());
					pessoa.setPassword(pessoaService.encodePassword(password));
//					pessoa.setPapelLdap(usuario.getAuthorities().get(0).getNome());
					pessoa.setPapel(Papel.USER);
					
					for(Affiliation affiliation: usuario.getAuthorities()){
						if(affiliation.getNome().equals(Papel.ADMIN.getTipo())){
							pessoa.setPapel(Papel.ADMIN);
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