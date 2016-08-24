package ufc.quixada.npi.contest.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping("/eventoOrganizador")
public class EventoControllerOrganizador extends EventoGenericoController{

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private TrilhaService trilhaService;
	
	@ModelAttribute("pessoas")
	public List<Pessoa> listaPossiveisOrganizadores() {
		return pessoaService.getPossiveisOrganizadores();
	}

	@RequestMapping(value = "/ativos", method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstadoAndPapelOrganizador(EstadoEvento.ATIVO);
		model.addAttribute("eventosAtivos", listaEventos);
		return Constants.TEMPLATE_LISTAR_EVENTOS_ATIVOS_ORG;
	}

	@RequestMapping(value = "/inativos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstadoAndPapelOrganizador(EstadoEvento.INATIVO);
		model.addAttribute("eventosInativos", listaEventos);
		return Constants.TEMPLATE_LISTAR_EVENTOS_INATIVOS_ORG;
	}
	
	@RequestMapping(value = "/editar/{id}", method = RequestMethod.GET)
	public String alterarEventoOrganizador(@PathVariable String id, Model model, RedirectAttributes redirect){
		return alterarEvento(id, model, redirect, Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ORG, "redirect:/eventoOrganizador/inativos");
	}

	@RequestMapping(value = "/ativar/{id}", method = RequestMethod.GET)
	public String ativarEvento(@PathVariable String id, Model model, RedirectAttributes redirect){
		try{
			Long idEvento = Long.valueOf(id);
			Evento evento = eventoService.buscarEventoPorId(idEvento);
			model.addAttribute("evento", evento);
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ORG;
		}catch(NumberFormatException e){
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
		}
		return "redirect:/eventoOrganizador/inativos";
	}

	@RequestMapping(value = "/ativar", method = RequestMethod.POST)
	public String ativarEvento(@Valid Evento evento, BindingResult result, RedirectAttributes redirect){
		
		if (result.hasFieldErrors("nome")){
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ORG;
		}
		
		if(eventoService.existeEvento(evento.getId())){
			evento.setEstado(EstadoEvento.ATIVO);

			if(!eventoService.adicionarOuAtualizarEvento(evento)){			
				result.reject("ativarEventoErro", messageService.getMessage("ERRO_NAS_DATAS"));
				return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ORG;
			}
		}else{
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
		}

		return "redirect:/eventoOrganizador/ativos";
	}
	
	@RequestMapping(value = "/trilha", method = RequestMethod.POST)
	public String cadastraTrilha(@PathVariable String eventoId, @Valid Trilha trilha, BindingResult result){
		if (result.hasErrors()) {
			return Constants.TEMPLATE_LISTAR_TRILHAS_ORG;
		}
		long id = Long.parseLong(eventoId);
		if (eventoService.existeEvento(id)) {
			trilha.setEvento(eventoService.buscarEventoPorId(id));
			trilhaService.adicionarOuAtualizarTrilha(trilha);
			return Constants.TEMPLATE_DETALHES_TRILHA_ORG;
		}else{
			result.reject("organizadorError", messageService.getMessage("EVENTO_NAO_ENCONTRADO"));
			return Constants.TEMPLATE_LISTAR_TRILHAS_ORG;
		}
	}
	
}