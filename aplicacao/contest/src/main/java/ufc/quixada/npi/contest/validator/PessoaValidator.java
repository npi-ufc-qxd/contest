package ufc.quixada.npi.contest.validator;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.MessageService;

@Named
public class PessoaValidator implements Validator{
	
	private static final String NOME = "nome";
	private static final String NOME_NULL = "NOME_NULL";
	private static final String EMAIL_NULL = "EMAIL_NULL";
	private static final String EMAIL = "email";
	private static final String PESSOA_NULL = "PESSOA_NULL";
	private static final String PESSOA_INVALIDA = "PESSOA_INVALIDA";
	@Autowired
	private MessageService messageService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Pessoa.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Pessoa pessoa = (Pessoa) target;
		if(pessoa != null){
			if(pessoa.getEmail() == null){
				errors.rejectValue(EMAIL, EMAIL_NULL , messageService.getMessage(EMAIL_NULL));
			}
			if(pessoa.getNome() == null){
				errors.rejectValue(NOME, NOME_NULL , messageService.getMessage(NOME_NULL));
			}
		}else{
			errors.rejectValue(null, PESSOA_NULL , messageService.getMessage(PESSOA_INVALIDA));
		}
	}

}