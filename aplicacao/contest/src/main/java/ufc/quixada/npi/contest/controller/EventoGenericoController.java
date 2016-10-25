package ufc.quixada.npi.contest.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.validator.EventoValidator;

@Controller
public class EventoGenericoController {

	private static final String EVENTO_INATIVO = "eventoInativo";
	protected static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	private static final String ID_PESSOA = "idPessoa";
	private static final String EVENTO = "evento";
	private static final String EXISTE_SUBMISSAO = "existeSubmissao";
	private static final String SUBMISSAO_REVISAO = "existeSubmissaoRevisao";

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private EventoValidator eventoValidator;
	
	@Autowired
	private RevisaoService revisaoService;
	
	@Autowired
	private SubmissaoService submissaoService;
	
	public String alterarEvento(@PathVariable String id, Model model, RedirectAttributes redirect, String viewSucesso, String viewFallha){
        try{
            Long idEvento = Long.valueOf(id);
            Evento evento = eventoService.buscarEventoPorId(idEvento);
            if (evento != null){
                ParticipacaoEvento participacao = participacaoEventoService.findByEventoId(evento.getId());
                model.addAttribute(EVENTO, participacao.getEvento());
                model.addAttribute(ID_PESSOA, participacao.getPessoa().getId());
                return viewSucesso;
            }else{
                redirect.addFlashAttribute("erro", messageService.getMessage(EVENTO_NAO_EXISTE));
            }
        }catch(NumberFormatException e){
            redirect.addFlashAttribute("erro", messageService.getMessage(EVENTO_NAO_EXISTE));
        }
        return viewFallha;
	}
	
	public String ativarOuEditarEvento(@Valid Evento evento, BindingResult result, Model model, RedirectAttributes redirect, 
			String viewSucesso, String viewFallha){
		eventoValidator.validate(evento, result);
		
		if (result.hasErrors()){
			boolean exiteSubmissao = submissaoService.existeTrabalhoNesseEvento(Long.valueOf(evento.getId()));
			boolean existeRevisao = revisaoService.existeTrabalhoNesseEvento(Long.valueOf(evento.getId()));
			
			if(exiteSubmissao){
				if(existeRevisao){
					model.addAttribute(SUBMISSAO_REVISAO, exiteSubmissao);
				}else{
					model.addAttribute(EXISTE_SUBMISSAO, exiteSubmissao);
				}
			}else{
				model.addAttribute(EVENTO_INATIVO,true);
			}
			return viewFallha;
		}
		
		if(eventoService.existeEvento(evento.getId())){
			
			evento.setEstado(EstadoEvento.ATIVO);

			if(!eventoService.adicionarOuAtualizarEvento(evento)){			
				return viewFallha;
			}
		}else{
			redirect.addFlashAttribute("erro", messageService.getMessage(EVENTO_NAO_EXISTE));
		}

		return viewSucesso;
	}
}