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
import ufc.quixada.npi.contest.util.Constants;


@Controller
@RequestMapping("/autor")
public class AutorController {

	private static final String PARTICAPAR_EVENTO_SUCESSO = "PARTICAPAR_EVENTO_SUCESSO";
	private static final String PARTICAPACAO_EVENTO_SUCESSO = "particapacaoEventoSucesso";
	private static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	private static final String EVENTO_INEXISTENTE_ERROR = "eventoInexistenteError";
	private static final String PARTICIPAR_EVENTO_INATIVO = "PARTICIPAR_EVENTO_INATIVO";
	private static final String ID_EVENTO_VAZIO_ERROR = "ID_EVENTO_VAZIO_ERROR";
	private static final String EVENTO_VAZIO_ERROR = "eventoVazioError";
	private static final String PARTICIPAR_EVENTO_INATIVO_ERROR = "participarEventoInativoError";

	@Autowired
	ParticipacaoEventoService participacaoEventoService;
	
	@Autowired
	EventoService eventoService;
	
	@Autowired
	PessoaService pessoaService;
	
	@Autowired
	MessageService messageService;
	
	@RequestMapping
	public String index(Model model){
		model.addAttribute("eventos", eventoService.buscarEventosAtivosEPublicos());
		return Constants.TEMPLATE_INDEX_AUTOR;
	}
		
	@RequestMapping(value="/participarEvento", method = RequestMethod.POST)
	public String participarEvento(@RequestParam String idEvento, Model model){
		if(idEvento == null || idEvento.isEmpty()){
			model.addAttribute(EVENTO_VAZIO_ERROR, messageService.getMessage(ID_EVENTO_VAZIO_ERROR));
			return "redirect:/autor";
		}
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Pessoa autorLogado = (Pessoa) auth.getPrincipal();
		
		Evento evento = eventoService.buscarEventoPorId(Long.valueOf(idEvento));
		
		if(evento != null){
			if(evento.getEstado() == EstadoEvento.ATIVO){
				ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
				participacaoEvento.setEvento(evento);
				participacaoEvento.setPessoa(autorLogado);
				participacaoEvento.setPapel(Papel.AUTOR);
				
				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento);
			}else{
				model.addAttribute(PARTICIPAR_EVENTO_INATIVO_ERROR, messageService.getMessage(PARTICIPAR_EVENTO_INATIVO));
			}
		}else{
			model.addAttribute(EVENTO_INEXISTENTE_ERROR, messageService.getMessage(EVENTO_NAO_EXISTE));
			return "redirect:/autor";
		}
		
		model.addAttribute(PARTICAPACAO_EVENTO_SUCESSO, messageService.getMessage(PARTICAPAR_EVENTO_SUCESSO));
		return "redirect:/autor";
	}
	
	@RequestMapping(value = "/meusTrabalhos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		
		return Constants.TEMPLATE_MEUS_TRABALHOS_AUTOR;
	}
}