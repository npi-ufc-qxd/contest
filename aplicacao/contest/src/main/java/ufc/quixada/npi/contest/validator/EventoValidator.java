package ufc.quixada.npi.contest.validator;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.service.MessageService;


@Named
public class EventoValidator implements Validator{
	
	@Autowired
	private MessageService messageService;
	
	private static final String EVENTO_CAMPO_NULL="evento";
	private static final String PRAZO_REVISAO_INICIAL="prazoRevisaoInicial";
	private static final String PRAZO_REVISAO_FINAL="prazoRevisaoFinal";
	private static final String PRAZO_SUBMISSAO_INICIAL="prazoSubmissaoInicial";
	private static final String PRAZO_SUBMISSAO_FINAL="prazoSubmissaoInicial";
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Evento.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object object, Errors erros) {
		Evento  evento = (Evento) object;
		if(datasOuVisibilidadeNull(evento)){
			erros.rejectValue(null, EVENTO_CAMPO_NULL, messageService.getMessage("ERRO_CAMPOS_EVENTO_NULL"));
		}else{
			if (evento.getPrazoSubmissaoInicial().after(evento.getPrazoRevisaoInicial())
					|| evento.getPrazoSubmissaoInicial().after(evento.getPrazoRevisaoFinal())
					|| evento.getPrazoSubmissaoInicial().after(evento.getPrazoSubmissaoFinal())) {
					erros.rejectValue(PRAZO_SUBMISSAO_INICIAL, PRAZO_SUBMISSAO_INICIAL, messageService.getMessage("ERRO_DATA_SUBMISSAO_INICIAL"));
			}
			if(evento.getPrazoRevisaoInicial().after(evento.getPrazoRevisaoFinal())
				|| evento.getPrazoRevisaoInicial().after(evento.getPrazoSubmissaoFinal())
				|| evento.getPrazoRevisaoInicial().before(evento.getPrazoSubmissaoInicial())){
				erros.rejectValue(PRAZO_REVISAO_INICIAL, PRAZO_REVISAO_INICIAL, messageService.getMessage("ERRO_DATA_REVISAO_INICIAL"));
			}
			if(evento.getPrazoRevisaoFinal().after(evento.getPrazoSubmissaoFinal())
				|| evento.getPrazoRevisaoFinal().before(evento.getPrazoRevisaoInicial())
				|| evento.getPrazoRevisaoFinal().before(evento.getPrazoSubmissaoInicial())){
				erros.rejectValue(PRAZO_REVISAO_FINAL, PRAZO_REVISAO_FINAL, messageService.getMessage("ERRO_DATA_REVISAO_FINAL"));
			}
			if(evento.getPrazoSubmissaoFinal().before(evento.getPrazoRevisaoFinal())
				|| evento.getPrazoSubmissaoFinal().before(evento.getPrazoRevisaoInicial())
				|| evento.getPrazoSubmissaoFinal().before(evento.getPrazoSubmissaoInicial())){
				erros.rejectValue(PRAZO_SUBMISSAO_FINAL, PRAZO_SUBMISSAO_FINAL, messageService.getMessage("ERRO_DATA_SUBMISSAO_FINAL"));
			}
		}
	}
	public boolean datasOuVisibilidadeNull(Evento evento){
		if(evento == null){
			return true;
		}else if(evento.getPrazoRevisaoFinal() == null || evento.getPrazoRevisaoInicial() == null
			|| evento.getPrazoSubmissaoFinal() == null || evento.getPrazoSubmissaoInicial() == null
			|| evento.getVisibilidade() == null){
			return true;
		}
		return false;
	}
}