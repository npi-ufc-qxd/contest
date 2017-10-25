package ufc.quixada.npi.contest.controller;




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
		return Constants.TEMPLATE_INDEX_COAUTOR;
	}
	
	@RequestMapping(value = "/listarTrabalhos/{id}", method = RequestMethod.GET)
	public String listarTrabalhos(@PathVariable String id, Model model, RedirectAttributes redirect) {

		Long idEvento = Long.parseLong(id);
			if (eventoService.existeEvento(idEvento)) {
				Evento evento = eventoService.buscarEventoPorId(Long.parseLong(id));
				Pessoa pessoa = PessoaLogadaUtil.pessoaLogada();		
								
				List<Trabalho> listaTrabalho = trabalhoService.getTrabalhosDoCoautorNoEvento(pessoa, evento);
				model.addAttribute("evento", evento);
				model.addAttribute("listaTrabalhos", listaTrabalho);
				
				return Constants.TEMPLATE_INDEX_COAUTOR;
			}
			return "redirect:/autor/meusTrabalhos";
	}	
}
