package ufc.quixada.npi.contest.controller;




import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;

@RequestMapping("/coautor")
@Controller
public class CoautorController {

	@Autowired
	private TrabalhoService trabalhoService;
	
	@Autowired
	private EventoService eventoService;
		
	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("listaTrabalhos", trabalhoService.getTrabalhosDoCoautor(PessoaLogadaUtil.pessoaLogada()));
		return Constants.TEMPLATE_INDEX_AUTOR;
	}
	
	@RequestMapping(value = "/listarTrabalhos/{id}", method = RequestMethod.GET)
	public String listarTrabalhos(@PathVariable String id, Model model, RedirectAttributes redirect) {

			Evento evento = eventoService.buscarEventoPorId(Long.parseLong(id));
			Pessoa pessoa = PessoaLogadaUtil.pessoaLogada();
			if (evento != null && evento.getAutores().contains(pessoa)) {

								
				List<Trabalho> listaTrabalho = trabalhoService.getTrabalhosDoCoautorNoEvento(pessoa, evento);
				model.addAttribute("evento", evento);
				model.addAttribute("listaTrabalhos", listaTrabalho);
				model.addAttribute("hoje");
				model.addAttribute("data_hoje");
				model.addAttribute("dataFinal");
				
				return Constants.TEMPLATE_LISTAR_TRABALHO_AUTOR;
			}
			return "redirect:/autor/meusTrabalhos";
	}
	
	@RequestMapping(value = "/meusTrabalhos", method = RequestMethod.GET)
	public String listarMeusTrabalhosEmEventosAtivos(Model model) {
		return listarMeusTrabalhosEmEventosAtivos(null, model);
	}
	
	@RequestMapping(value = "/meusTrabalhos/evento/{eventoId}", method = RequestMethod.GET)
	public String listarMeusTrabalhosEmEventosAtivos(@PathVariable Long eventoId, Model model) {
		Pessoa autorLogado = PessoaLogadaUtil.pessoaLogada();
		List<Evento> eventos = new ArrayList<>();
		if (eventoId != null) {
			eventos.add(eventoService.buscarEventoPorId(eventoId));
		} else {
			eventos = eventoService.getMeusEventosComoCoautor(autorLogado.getId());
		}
		
		if (eventos != null) {
			model.addAttribute("eventos", eventos);
			model.addAttribute("papel", Tipo.COAUTOR);
		}
		return Constants.TEMPLATE_MEUS_TRABALHOS_AUTOR;
	}
}
