package ufc.quixada.npi.contest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Secao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.SecaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping(value = "/secao")
public class SecaoController {
	@Autowired
	private SecaoService secaoService;
	@Autowired
	private TrabalhoService trabalhoService;
	@Autowired
	private EventoService eventoService;
	@Autowired
	private PessoaService pessoaService;

	@RequestMapping(value = "/paginaSecao")
	public String indexSecao() {
		return "secao/indexSecao";
	}

	@RequestMapping(value = "/cadastrarSecaoForm", method = RequestMethod.GET)
	public String cadastrarSecaoForm(Model model) {
		List<Pessoa> pessoas = pessoaService.getPossiveisOrganizadores();
		List<Evento> eventos = eventoService.buscarEventos();
		model.addAttribute("pessoas", pessoas);
		model.addAttribute("eventos", eventos);
		return "secao/cadastroSecao";
	}

	@RequestMapping(value = "/cadastrarSecao")
	public String cadastrarSecao(@RequestParam String nomeSecao, @RequestParam String descricao,
			@RequestParam Long idResponsavel, @RequestParam Long idEvento) {
		Secao secao = new Secao();
		Pessoa responsavel = pessoaService.get(idResponsavel);
		Evento evento = eventoService.buscarEventoPorId(idEvento);

		secao.setNome(nomeSecao);
		secao.setDescricao(descricao);
		secao.setResponsavel(responsavel);
		secao.setEvento(evento);

		secaoService.addOrUpdate(secao);
		return Constants.TEMPLATE_MEUS_EVENTOS_ORG;
	}

	@RequestMapping(value = "/secaoTrabalhos", method = RequestMethod.GET)
	public String secaoTrabalhos() {
		return "secao/secaoTrabalhos";
	}

	// FALTA A PAGINA ALTERAR SECAO
	@RequestMapping(value = "/alterarSecao/{id}")
	public String alterarSecaoForm(@PathVariable("id") Long idSecao, Model model) {
		Secao secao = secaoService.get(idSecao);
		model.addAttribute("secao", secao);
		return "";
	}

	@RequestMapping(value = "/alterarSecao")
	public String alterarSecao(Secao secao) {
		secaoService.addOrUpdate(secao);
		return Constants.TEMPLATE_MEUS_EVENTOS_ORG;
	}

	// FALTA A PAGINA DE LISTAR TRABALHOS SEÇÃO
	@RequestMapping(value = "/secao/{id}")
	public String listarTrabalhoSecao(@PathVariable("id") Long idSecao, Model model) {
		Secao secao = secaoService.get(idSecao);
		List<Trabalho> trabalhos = secao.getTrabalhos();
		model.addAttribute("trabalhos", trabalhos);
		return "";
	}

	// FALTA A PAGINA DE LISTAR TRABALHOS SEÇÃO
	@RequestMapping(value = "/excluirSecao/{id}")
	public String excluirSecao(@PathVariable("id") Long idSecao) {
		secaoService.delete(idSecao);
		return "";
	}

	@RequestMapping("/excluirTrabalho/{idSecao}/{idTrabalho}")
	public String excluirTrabalhoSecao(@PathVariable("idSecao") Long idSecao,
			@PathVariable("idTrabalho") Long idTrabalho) {
		Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
		secaoService.removerTrablahoSecao(idSecao, trabalho);
		return "redirect:/secao/" + idSecao;
	}

	@RequestMapping("/adicionarTrabalho/{idSecao}/{idTrabalho}")
	public String adicionarTrabalhoSecao(@PathVariable("idSecao") Long idSecao,
			@PathVariable("idTrabalho") Long idTrabalho) {
		Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
		secaoService.adicionarTrabalhoSecao(idSecao, trabalho);
		return "redirect:/secao/" + idSecao;
	}
}
