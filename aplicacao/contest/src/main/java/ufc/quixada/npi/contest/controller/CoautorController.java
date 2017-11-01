package ufc.quixada.npi.contest.controller;




import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.Papel.Tipo;
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
	public String listarTrabalhos(RedirectAttributes redirAttr, @PathVariable String id, Model model) {
		redirAttr.addAttribute("coautor", "");
		return "forward:/autor/listarTrabalhos/" + id;
	}
	
	@RequestMapping(value = "/meusTrabalhos", method = RequestMethod.GET)
	public String listarMeusTrabalhosEmEventosAtivos(RedirectAttributes redirAttr) {
		redirAttr.addAttribute("coautor", "");
		return "forward:/autor/meusTrabalhos/";
	}
	
	@RequestMapping(value = "/meusTrabalhos/evento/{eventoId}", method = RequestMethod.GET)
	public String listarMeusTrabalhosEmEventosAtivos(RedirectAttributes redirAttr, @PathVariable Long eventoId) {
		redirAttr.addAttribute("coautor", "");
		return "forward:/autor/meusTrabalhos/evento/" + eventoId;
	}
}
