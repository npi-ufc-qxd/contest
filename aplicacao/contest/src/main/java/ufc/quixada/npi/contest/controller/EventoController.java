package ufc.quixada.npi.contest.controller;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

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

	private static final String TEMPLATE_ADICIONAR_OU_EDITAR = "evento/admin_cadastrar";
	
	@ModelAttribute("pessoas")
	public List<Pessoa> listarPessoas(){
		return pessoaService.list();
	}
	
	
	@RequestMapping(value = "/adicionar", method = RequestMethod.GET) 
	public String adicionarEvento(Model model) {
		model.addAttribute("evento", new Evento());
		return TEMPLATE_ADICIONAR_OU_EDITAR;
	}
  
	@RequestMapping(value = "/adicionar", method = RequestMethod.POST)
	public String adicionarEvento(@RequestParam(required = false) String organizador, @Valid Evento evento, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		
		if(organizador == null || organizador.isEmpty()){
			result.reject("organizadorError", messages.getMessage("ORGANIZADOR_VAZIO_ERROR", null, null));
		}  
		 
		if (result.hasErrors()) {
			return TEMPLATE_ADICIONAR_OU_EDITAR;
		}

		Pessoa pessoa = pessoaService.get(Long.valueOf(organizador));

		if (pessoa != null) {
			participacaoEventoService.adicionarOuEditarParticipacaoEvento(evento, pessoa, Papel.ORGANIZADOR);
		} else {
			result.reject("organizadorError", messages.getMessage("PESSOA_NAO_ENCONTRADA", null, null));
			return TEMPLATE_ADICIONAR_OU_EDITAR;
		} 

		return "redirect:/evento";
	}

	@RequestMapping(value = "/remover/{id}", method = RequestMethod.GET)
	public String removerEvento(@PathVariable Integer id, Model model) {
		if (id != null) {
			Evento evento = eventoService.buscarEventoPorId(id);
			participacaoEventoService.removerParticipacaoEvento(evento);
		} else {
			model.addAttribute("error", "Não foi possivel remover evento");
		}
		return "redirect:/evento";
	}
}