package ufc.quixada.npi.contest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.PessoaService;

@Controller
@RequestMapping("/")
public class MapearPapeisController {
	
	@Autowired
	private PessoaService pessoaService;
	
	@RequestMapping(value = {""}, method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		Pessoa p = getAutorLogado();
		
		switch (p.getPapelLdap()) {
		case ADMIN:
			
			return "redirect:/evento";
		case STA:
			
			return "redirect:/eventoOrganizador";
		case DISCENTE:
			
			return "redirect:/autor";
        case DOCENTE:
			
        	return "redirect:/eventoOrganizador";
		default:
			
			return "redirect:/login";
		}
	}
	
	public Pessoa getAutorLogado(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String cpf = auth.getName();
		Pessoa autorLogado = pessoaService.getByCpf(cpf);
		return autorLogado;
	}
	

	
}