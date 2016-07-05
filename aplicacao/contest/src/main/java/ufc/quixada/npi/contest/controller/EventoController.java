package ufc.quixada.npi.contest.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.repository.EventoRepository;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

@Controller
@RequestMapping("/evento")
public class EventoController {

	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	private static final String TEMPLATE_ADICIONAR_OU_EDITAR = "evento/add_ou_edit";

	@RequestMapping(value = "/adicionar", method = RequestMethod.GET)
	public String adicionarEvento() {
		return TEMPLATE_ADICIONAR_OU_EDITAR;
	}

	@RequestMapping(value = "/adicionar", method = RequestMethod.POST)
	public String adicionarEvento(@RequestParam String organizador, @Valid Evento evento, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			return TEMPLATE_ADICIONAR_OU_EDITAR;
		}

		Pessoa pessoa = pessoaService.get(Long.valueOf(organizador));
		if (pessoa != null) {
			participacaoEventoService.adicionarOuEditarParticipacaoEvento(evento, pessoa, Papel.ORGANIZADOR);
		} else {
			model.addAttribute("error", "Essa pessoa não está cadastrada no sistema");
			return TEMPLATE_ADICIONAR_OU_EDITAR;
		}

		return "redirect:/evento";
	}
	
	@RequestMapping(value = "/alterar", method = RequestMethod.GET)
	public String alterarEvento(@RequestParam Long id, Model model){
		if (eventoService.existeEvento(id)){
			model.addAttribute("evento", eventoService.buscarEventoPorId(id));
			return TEMPLATE_ADICIONAR_OU_EDITAR;
		}
		return "redirect:/evento";
	}
	
	@RequestMapping(value = "/alterar", method = RequestMethod.POST)
	public String alterarEvento(@RequestParam String organizador, @Valid Evento evento, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			return TEMPLATE_ADICIONAR_OU_EDITAR;
		}

		Pessoa pessoa = pessoaService.get(Long.valueOf(organizador));
		if (pessoa != null) {
			participacaoEventoService.adicionarOuEditarParticipacaoEvento(evento, pessoa, Papel.ORGANIZADOR);
		} else {
			model.addAttribute("error", "Essa pessoa não está cadastrada no sistema");
			return TEMPLATE_ADICIONAR_OU_EDITAR;
		}

		return "redirect:/evento";
	}


	
}