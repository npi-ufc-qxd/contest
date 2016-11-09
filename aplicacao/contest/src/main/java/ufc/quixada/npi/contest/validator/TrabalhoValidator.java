package ufc.quixada.npi.contest.validator;

import java.util.List;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.MessageService;

@Named
public class TrabalhoValidator implements Validator{
	private static final String EVENTO_NULL = "EVENTO_NULL";
	private static final String EVENTO = "evento";
	private static final String CAMPOS_VAZIOS = "CAMPOS_VAZIOS";
	private static final String TRABALHO_NULL = "TRABALHO_NULL";
	private static final String TRABALHO_INVALIDO = "TRABALHO_INVALIDO";
	private static final String NOME_DO_TRABALHO_VAZIO = "NOME_DO_TRABALHO_VAZIO";
	private static final String TITULO_NULL = "TITULO_NULL";
	private static final String TITULO = "titulo";
	private static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	
	@Autowired
	private MessageService messageService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Trabalho.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Trabalho trabalho = (Trabalho) target;
		if(trabalho != null){
			if(trabalho.getTitulo().trim().isEmpty()){
				errors.rejectValue(TITULO, TITULO_NULL, messageService.getMessage(NOME_DO_TRABALHO_VAZIO));
			}
			if(trabalho.getEvento() == null){
				errors.rejectValue(EVENTO, EVENTO_NULL, messageService.getMessage(EVENTO_NAO_EXISTE));
			}
		}else{
			errors.rejectValue(null, TRABALHO_NULL , messageService.getMessage(TRABALHO_INVALIDO));
		}
		if(validadeParticipacaoTrabalho(trabalho, errors)){
			errors.rejectValue(null, CAMPOS_VAZIOS, messageService.getMessage(CAMPOS_VAZIOS));
		}
	}
	
	public boolean validadeParticipacaoTrabalho(Trabalho trabalho, Errors errors){
		if(trabalho.getParticipacoes() != null){
			List<ParticipacaoTrabalho> listaParticipacoes = trabalho.getParticipacoes();
			for(ParticipacaoTrabalho part : listaParticipacoes){
				if(part.getPessoa().getNome().isEmpty() || part.getPessoa().getEmail().isEmpty()){
					return true;
				}
			}
		}
		return false;
	}
}