package ufc.quixada.npi.contest.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;

@RequestMapping("/coautor")
@Controller
public class CoautorController {

	@Autowired
	private TrabalhoService trabalhoService;
	
	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("listaTrabalhos", trabalhoService.getTrabalhosDoCoautor(PessoaLogadaUtil.pessoaLogada()));
		return Constants.TEMPLATE_INDEX_AUTOR;
	}
	
	@RequestMapping(value = "/listarTrabalhos/{id}", method = RequestMethod.GET)
	public String listarTrabalhos(@PathVariable String id, Model model) {
		return "forward:/autor/listarTrabalhos/" + id;
	}
	
	@RequestMapping(value = "/meusTrabalhos", method = RequestMethod.GET)
	public String listarMeusTrabalhosEmEventosAtivos() {
		return "forward:/autor/meusTrabalhos/?coautor=coautor";
	}
	
	@RequestMapping(value = "/meusTrabalhos/evento/{eventoId}", method = RequestMethod.GET)
	public String listarMeusTrabalhosEmEventosAtivos(@PathVariable Long eventoId) {
		return "forward:/autor/meusTrabalhos/evento/" + eventoId ;
	}
}
