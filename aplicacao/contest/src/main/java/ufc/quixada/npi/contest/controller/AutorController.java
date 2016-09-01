package ufc.quixada.npi.contest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;


@Controller
@RequestMapping("/autor")
public class AutorController {

	@Autowired
	ParticipacaoEventoService participacaoEventoService;
	
	@Autowired
	EventoService eventoService;
	
	@Autowired
	PessoaService pessoaService;
	
	@Autowired
	MessageService messageService;
	
	private static final String TEMPLATE_INDEX_AUTOR = "autor/autor_index"; 
	
	@RequestMapping
	public String index(Model model){
		model.addAttribute("eventos", eventoService.buscarEventosAtivosEPublicos());
		return TEMPLATE_INDEX_AUTOR;
	}
		
	@RequestMapping(value="/participarevento", method = RequestMethod.POST)
	public String participarEvento(
			@RequestParam String idEvento,
			Model model){
		
		if(idEvento == null || idEvento.isEmpty()){
			model.addAttribute("eventoVazioError", messageService.getMessage("ID_EVENTO_VAZIO_ERROR"));
			return "redirect:/autor";
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Pessoa autorLogado = (Pessoa) auth.getPrincipal();
		
		Evento evento = eventoService.buscarEventoPorId(Long.valueOf(idEvento));
		
		if(evento!=null){
			if(evento.getEstado() == EstadoEvento.ATIVO){
				ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
				participacaoEvento.setEvento(evento);
				participacaoEvento.setPessoa(autorLogado);
				participacaoEvento.setPapel(Papel.AUTOR);
				
				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento);
			}else{
				model.addAttribute("participarEventoInativoError", messageService.getMessage("PARTICIPAR_EVENTO_INATIVO"));
			}
		}else{
			model.addAttribute("eventoInexistenteError", messageService.getMessage("EVENTO_NAO_EXISTE"));
			return "redirect:/autor";
		}
		
		model.addAttribute("particapacaoEventoSucesso", messageService.getMessage("PARTICAPAR_EVENTO_SUCESSO"));
		return "redirect:/autor";
	}
}