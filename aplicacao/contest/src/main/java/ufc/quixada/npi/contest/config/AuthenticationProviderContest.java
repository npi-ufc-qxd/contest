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
		if(usuario == null){
			throw new BadCredentialsException(messageService.getMessage("LOGIN_INVALIDO"));
		} else {
			Pessoa pessoa = pessoaService.getByCpf(usuario.getCpf());
			if(pessoa == null){
				Pessoa pessoaTemp = new Pessoa();
				pessoaTemp.setCpf(cpf);
				pessoaTemp.setNome(usuario.getNome());
				pessoaTemp.setEmail(usuario.getEmail());
				pessoaTemp.setPassword(pessoaService.encodePassword(password));
				pessoaTemp.setPapelLdap(usuario.getAuthorities().get(0).getNome()); //REVISAR
				pessoaService.addOrUpdate(pessoaTemp);
				return new UsernamePasswordAuthenticationToken(pessoaTemp, pessoaService.encodePassword(password),
						pessoaTemp.getAuthorities());
			} else{
				return new UsernamePasswordAuthenticationToken(pessoa, pessoaService.encodePassword(password),
						pessoa.getAuthorities());
			}
		}

		
		
		
//		Pessoa pessoa = pessoaService.getByCpf(cpf);
//
//		if (pessoa != null) { // Pessoa existe
//			if (!pessoaService.autentica(pessoa, cpf, password))
//				throw new BadCredentialsException(messageService.getMessage("LOGIN_INVALIDO"));
//		} else if (usuarioService.autentica(cpf, password)) { // Pessoa não existe, então tenta autenticar via LDAP
//			
//			Usuario usuario = usuarioService.getByCpf(cpf);
//			pessoa = pessoaService.getByEmail(usuario.getEmail());
//			
//			if(pessoa == null){
//				pessoa = new Pessoa();
//				pessoa.setEmail(usuario.getEmail());
//			}
//			pessoa.setCpf(cpf);
//			pessoa.setNome(usuario.getNome());
//			pessoa.setPassword(pessoaService.encodePassword(password));
//			pessoa.setPapelLdap(usuario.getAuthorities().get(0).getNome());
//			
//			for(Affiliation affiliation: usuario.getAuthorities()){
//				if(affiliation.getNome().equals(Tipo.ADMIN.getTipo())){
//					pessoa.setPapelLdap(Tipo.ADMIN.name());
//				}
//			}
//
//			pessoaService.addOrUpdate(pessoa);
//		} else {
//			throw new BadCredentialsException(messageService.getMessage("LOGIN_INVALIDO"));
//		}
//
//		return new UsernamePasswordAuthenticationToken(pessoa, pessoaService.encodePassword(password),
//				pessoa.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

}