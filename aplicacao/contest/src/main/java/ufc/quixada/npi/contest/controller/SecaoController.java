package ufc.quixada.npi.contest.controller;

import java.util.ArrayList;
import java.util.Collections;
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
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;

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

	@RequestMapping(value = "{eventoId}/paginaSecao")
	public String indexSecao(Model model, @PathVariable("eventoId") Long eventoId) {
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateParams(evento);
		if (validateResult.equals(Constants.NO_ERROR)) {
			List<Secao> secoes = secaoService.listByEvento(evento);
			model.addAttribute("secoes", secoes);
			model.addAttribute("evento", evento);
			return "secao/indexSecao";
		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "{eventoId}/cadastrarSecaoForm", method = RequestMethod.GET)
	public String cadastrarSecaoForm(Model model, @PathVariable("eventoId") Long eventoId) {

		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateParams(evento);
		if (validateResult.equals(Constants.NO_ERROR)) {
			List<Pessoa> pessoas = pessoaService.getTodosInEvento(evento);
			Collections.sort(pessoas);
			model.addAttribute("pessoas", pessoas);
			model.addAttribute("evento", evento);
			return "secao/cadastroSecao";
		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "{eventoId}/cadastrarSecao", method = RequestMethod.POST)
	public String cadastrarSecao(Secao secao, @PathVariable("eventoId") Long eventoId) {
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateParams(evento);
		if (validateResult.equals(Constants.NO_ERROR)) {

			if (secao.getEvento() == null || secao.getResponsavel() == null) {
				return "redirect:/secao/" + evento.getId() + "/cadastrarSecaoForm";
			}
			secaoService.addOrUpdate(secao);
			return "redirect:/secao/" + evento.getId() + "/paginaSecao";

		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "/secaoTrabalhos/{id}", method = RequestMethod.GET)
	public String secaoTrabalhos(@PathVariable("id") Long idSecao, Model model) {
		Secao secao = secaoService.get(idSecao);
		if(secao == null){
			return Constants.ERROR_404;
		}
		List<ParticipacaoTrabalho> trabalhosSecao = new ArrayList<>();
		List<Trabalho> trabalhos = new ArrayList<>();

		for (Trabalho trab : secao.getTrabalhos()) {
			for (ParticipacaoTrabalho part : trab.getParticipacoes()) {
				if (part.getPapel().equals(Papel.Tipo.AUTOR)) {
					trabalhosSecao.add(part);
				}
			}
		}

		for (Trabalho trabalho : trabalhoService.buscarTodosTrabalhos()) {
			if (trabalho.getSecao() == null && trabalho.getEvento() == secao.getEvento()) {
				trabalhos.add(trabalho);
			}
		}

		Collections.sort(trabalhos);

		model.addAttribute("trabalhos", trabalhos);
		model.addAttribute("trabalhosSecao", trabalhosSecao);
		model.addAttribute("secao", secao);
		model.addAttribute("qtdTrabalhos", secao.getTrabalhos().size());
		return "secao/secaoTrabalhos";
	}

	@RequestMapping(value = "/excluirSecao/{id}")
	public String excluirSecao(@PathVariable("id") Long idSecao) {
		Secao secao = secaoService.get(idSecao);
		
		if(secao == null){
			return Constants.ERROR_404;
		}

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
		
		if(trabalho == null){
			return Constants.ERROR_404;
		}
		
		trabalhoService.removerSecao(trabalho);
		return "redirect:/secao/secaoTrabalhos/" + idSecao;
	}

	@RequestMapping("/adicionarTrabalhoSecao")
	public String adicionarTrabalhoSecao(@RequestParam Long idSecao, @RequestParam List<Long> idTrabalhos) {
		Secao secao = secaoService.get(idSecao);
		
		if(secao == null){
			return Constants.ERROR_404;
		}
		
		for (int i = 0; i < idTrabalhos.size(); i++) {
			Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalhos.get(i));
			trabalho.setSecao(secao);
			trabalhoService.adicionarTrabalho(trabalho);
		}
		return "redirect:/secao/secaoTrabalhos/" + idSecao;
	}

	@RequestMapping("/listarParticipantes/{idSecao}")
	public String listarParticipantes(@PathVariable("idSecao") Long idSecao, Model model) {
		Secao secao = secaoService.get(idSecao);
		
		if(secao == null){
			return Constants.ERROR_404;
		}
		
		model.addAttribute("secao", secao);
		model.addAttribute("trabalhos", trabalhoService.buscarTodosTrabalhosDaSecao(idSecao));
		return "secao/listaParticipantes";

	}

	private String validateParams(Evento evento) {
		Pessoa pessoa = pessoaService.getByCpf(PessoaLogadaUtil.pessoaLogada().getCpf());
		if (evento == null) {
			return Constants.ERROR_404;
		}

		if (!evento.getOrganizadores().contains(pessoa)) {
			return Constants.ERROR_403;
		}
		return Constants.NO_ERROR;
	}
}
