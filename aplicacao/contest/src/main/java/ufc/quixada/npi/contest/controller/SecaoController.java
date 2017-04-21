package ufc.quixada.npi.contest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ufc.quixada.npi.contest.model.Secao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.SecaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping("/secao")
public class SecaoController {
	@Autowired
	private SecaoService secaoService;
	@Autowired
	private TrabalhoService trabalhoService;
	
	@RequestMapping(value="/cadastrarSecaoForm")
	public String cadastrarSecaoForm(){
		return "";
	}
	
	@RequestMapping(value="/cadastrarSecao")
	public String cadastrarSecao(Secao secao){
		secaoService.addOrUpdate(secao);
		return Constants.TEMPLATE_MEUS_EVENTOS_ORG;
	}
	
	//FALTA A PAGINA ALTERAR SECAO
	@RequestMapping(value="/alterarSecao/{id}")
	public String alterarSecaoForm(@PathVariable("id") Long idSecao,Model model){
		Secao secao = secaoService.get(idSecao);
		model.addAttribute("secao", secao);
		return "";	
	}
	
	@RequestMapping(value="/alterarSecao")
	public String alterarSecao(Secao secao){
		secaoService.addOrUpdate(secao);
		return Constants.TEMPLATE_MEUS_EVENTOS_ORG;
	}
	
	//FALTA A PAGINA DE LISTAR TRABALHOS SEÇÃO
	@RequestMapping(value="/secao/{id}")
	public String listarTrabalhoSecao(@PathVariable("id") Long idSecao,Model model){
		Secao secao = secaoService.get(idSecao);
		List<Trabalho> trabalhos = secao.getTrabalhos();
		model.addAttribute("trabalhos", trabalhos);
		return"";
	}
	
	//FALTA A PAGINA DE LISTAR TRABALHOS SEÇÃO
	@RequestMapping(value="/excluirSecao/{id}")
	public String excluirSecao(@PathVariable("id") Long idSecao){
		secaoService.delete(idSecao);
		return "";
	}
	
	@RequestMapping("/secao/{idSecao}/{idTrabalho}")
	public String excluirTrabalhoSecao(@PathVariable("idSecao") Long idSecao,@PathVariable("idTrabalho") Long idTrabalho){
		Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
		secaoService.removerTrablahoSecao(idSecao,trabalho);
		return "redirect:/secao/"+idSecao;
	}
}
