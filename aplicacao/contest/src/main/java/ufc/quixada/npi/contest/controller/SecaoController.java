package ufc.quixada.npi.contest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Secao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.SecaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;

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
	public String indexSecao(Model model) {
		List<Secao> secoes = secaoService.list();
		model.addAttribute("secoes", secoes);
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
	public String cadastrarSecao(Secao secao) {
		if (secao.getEvento() == null || secao.getResponsavel() == null) {
			return "redirect:/secao/cadastrarSecaoForm";
		}
		secaoService.addOrUpdate(secao);
		return "redirect:/secao/paginaSecao";
	}

	@RequestMapping(value = "/secaoTrabalhos/{id}", method = RequestMethod.GET)
	public String secaoTrabalhos(@PathVariable("id") Long idSecao, Model model) {
		Secao secao = secaoService.get(idSecao);
		List<ParticipacaoTrabalho> trabalhosSecao = new ArrayList<>();
		List<Trabalho> trabalhos = new ArrayList<>();

		for (Trabalho trab : secao.getTrabalhos()) {
			for (ParticipacaoTrabalho part : trab.getParticipacoes()) {
				if (part.getPapel().equals(Papel.AUTOR)) {
					trabalhosSecao.add(part);
				}
			}
		}

		for (Trabalho trabalho : trabalhoService.buscarTodosTrabalhos()) {
			if (trabalho.getSecao() == null) {
				trabalhos.add(trabalho);
			}
		}

		model.addAttribute("trabalhos", trabalhos);
		model.addAttribute("trabalhosSecao", trabalhosSecao);
		model.addAttribute("secao", secao);
		model.addAttribute("qtdTrabalhos", secao.getTrabalhos().size());
		return "secao/secaoTrabalhos";
	}

	@RequestMapping(value = "/excluirSecao/{id}")
	public String excluirSecao(@PathVariable("id") Long idSecao) {
		Secao secao = secaoService.get(idSecao);

		for (Trabalho trabalho : secao.getTrabalhos()) {
			trabalhoService.removerSecao(trabalho);
		}

		secaoService.delete(idSecao);

		return "redirect:/secao/paginaSecao";
	}

	@RequestMapping("/excluirTrabalho/{idSecao}/{idTrabalho}")
	public String excluirTrabalhoSecao(@PathVariable("idSecao") Long idSecao,
			@PathVariable("idTrabalho") Long idTrabalho) {
		Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
		trabalhoService.removerSecao(trabalho);
		return "redirect:/secao/secaoTrabalhos/" + idSecao;
	}

	@RequestMapping("/adicionarTrabalhoSecao")
	public String adicionarTrabalhoSecao(@RequestParam Long idSecao, @RequestParam List<Long> idTrabalhos) {
		for (int i = 0; i < idTrabalhos.size(); i++) {
			Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalhos.get(i));
			trabalho.setSecao(secaoService.get(idSecao));
			trabalhoService.adicionarTrabalho(trabalho);
		}
		return "redirect:/secao/secaoTrabalhos/" + idSecao;
	}
}
