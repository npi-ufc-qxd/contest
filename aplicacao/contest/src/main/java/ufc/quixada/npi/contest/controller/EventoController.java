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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping("/evento")
public class EventoController {

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private MessageService messageService;

	@ModelAttribute("pessoas")
	public List<Pessoa> listarPessoas() {
		return pessoaService.list();
	}

	@RequestMapping(value = {"/ativos", ""}, method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstado(EstadoEvento.ATIVO);
		model.addAttribute("eventosAtivos", listaEventos);
		return Constants.TEMPLATE_LISTAR_EVENTOS_ATIVOS;
	}

	@RequestMapping(value = "/inativos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstado(EstadoEvento.INATIVO);
		model.addAttribute("eventosInativos", listaEventos);
		return Constants.TEMPLATE_LISTAR_EVENTOS_INATIVOS;
	}

	@RequestMapping(value = "/adicionar", method = RequestMethod.GET)
	public String adicionarEvento(Model model) {
		model.addAttribute("evento", new Evento());
		return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO;
	}

	@RequestMapping(value = "/adicionar", method = RequestMethod.POST)
	public String adicionarEvento(@RequestParam(required = false) String organizador, @Valid Evento evento,
			BindingResult result, Model model) {

		if (organizador == null || organizador.isEmpty()) {
			result.reject("organizadorError", messageService.getMessage("ORGANIZADOR_VAZIO_ERROR"));
		}

		if (result.hasErrors()) {
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO;
		}

		evento.setEstado(EstadoEvento.INATIVO);
		evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		Pessoa pessoa = pessoaService.get(Long.valueOf(organizador));

		return "redirect:/evento";
	}
	
	@RequestMapping(value = "/alterar", method = RequestMethod.GET)
	public String alterarEvento(@RequestParam Long id, Model model){
		if (eventoService.existeEvento(id)){
			model.addAttribute("evento", eventoService.buscarEventoPorId(id));
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO;
		}
		return "redirect:/evento";
	}
	
	@RequestMapping(value = "/alterar", method = RequestMethod.POST)
	public String alterarEvento(@RequestParam String organizador, @Valid Evento evento, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO;
		}

		Pessoa pessoa = pessoaService.get(Long.valueOf(organizador));
		if (pessoa != null) {
			participacaoEventoService.adicionarOuEditarParticipacaoEvento(evento, pessoa, Papel.ORGANIZADOR);
		} else {
			model.addAttribute("error", "Essa pessoa não está cadastrada no sistema");
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO;
		}

		return "redirect:/evento";
	}

	@RequestMapping(value = "/remover/{id}", method = RequestMethod.GET)
	public String removerEvento(@PathVariable String id, RedirectAttributes redirect) {
		try {
			Long idEvento = Long.valueOf(id);
			Evento evento = eventoService.buscarEventoPorId(idEvento);
			if (evento != null) {
				participacaoEventoService.removerParticipacaoEvento(evento);

				redirect.addFlashAttribute("sucesso", messageService.getMessage("EVENTO_INATIVO_EXCLUIDO_SUCESSO"));
			} else {
				redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_INATIVO_EXCLUIDO_ERRO"));
			}
		} catch (NumberFormatException e) {
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_INATIVO_EXCLUIDO_ERRO"));
		}

		return "redirect:/evento/inativos";
	}
}