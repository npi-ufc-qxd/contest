package ufc.quixada.npi.contest.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import ufc.quixada.npi.contest.service.EventoService;
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
	private MessageSource messages;

	@RequestMapping(value = "/ativos", method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstado(EstadoEvento.ATIVO);
		model.addAttribute("eventosAtivos", listaEventos);
		return Constants.TEMPLATE_LISTAR_ATIVOS;
	}

	@RequestMapping(value = "/inativos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstado(EstadoEvento.INATIVO);
		model.addAttribute("eventosInativos", listaEventos);
		return Constants.TEMPLATE_LISTAR_INATIVOS;
	}

	@RequestMapping(value = "/adicionar", method = RequestMethod.GET)
	public String adicionarEvento() {
		return Constants.TEMPLATE_ADICIONAR_OU_EDITAR;
	}

	@RequestMapping(value = "/adicionar", method = RequestMethod.POST)
	public String adicionarEvento(@RequestParam String organizador, @Valid Evento evento, BindingResult result,
			Model model) {

		if (result.hasErrors()) {
			model.addAttribute("error", "Nome do Evento não pode ser vazio");
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR;
		}

		Pessoa pessoa = null;
		try {
			pessoa = pessoaService.get(Long.valueOf(organizador));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		if (pessoa != null) {
			participacaoEventoService.adicionarOuEditarParticipacaoEvento(evento, pessoa, Papel.ORGANIZADOR);
		} else {
			model.addAttribute("error", "Essa pessoa não está cadastrada no sistema");
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR;
		}

		return "redirect:/paginas_administrador";
	}

	@RequestMapping(value = "/remover/{id}", method = RequestMethod.GET)
	public String removerEvento(@PathVariable String id, RedirectAttributes redirect) {
		try {
			Long idEvento = Long.valueOf(id);
			Evento evento = eventoService.buscarEventoPorId(idEvento);
			if (evento != null) {
				participacaoEventoService.removerParticipacaoEvento(evento);

				redirect.addFlashAttribute("sucesso",
						messages.getMessage("EVENTO_INATIVO_EXCLUIDO_SUCESSO", null, null));
			} else {
				redirect.addFlashAttribute("erro", messages.getMessage("EVENTO_INATIVO_EXCLUIDO_ERRO", null, null));
			}
		} catch (NumberFormatException e) {
			redirect.addFlashAttribute("erro", messages.getMessage("EVENTO_INATIVO_EXCLUIDO_ERRO", null, null));
		}

		return "redirect:/evento/inativos";
	}
}