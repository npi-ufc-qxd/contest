package ufc.quixada.npi.contest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;

@Controller
public class EventoGenericoController {

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private MessageService messageService;
	
	public String alterarEvento(@PathVariable String id, Model model, RedirectAttributes redirect, String viewSucesso, String viewFallha){
        try{
            Long idEvento = Long.valueOf(id);
            Evento evento = eventoService.buscarEventoPorId(idEvento);
            if (evento != null){
                ParticipacaoEvento participacao = participacaoEventoService.findByEventoId(evento.getId());
                model.addAttribute("evento", participacao.getEvento());
                model.addAttribute("idPessoa", participacao.getPessoa().getId());
                return viewSucesso;
            }else{
                redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
            }
        }catch(NumberFormatException e){
            redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
        }
        return viewFallha;
	}
}
