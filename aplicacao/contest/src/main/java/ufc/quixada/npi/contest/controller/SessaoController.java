package ufc.quixada.npi.contest.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.PresencaJsonWrapper;
import ufc.quixada.npi.contest.model.Sessao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.SessaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;

@Controller
public class SessaoController {
	@Autowired
	private SessaoService sessaoService;
	@Autowired
	private TrabalhoService trabalhoService;
	@Autowired
	private EventoService eventoService;
	@Autowired
	private PessoaService pessoaService;

	@RequestMapping(value = "/evento/{eventoId}/sessao/")
	public String indexSessao(Model model, @PathVariable("eventoId") Long eventoId) {
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateEventParams(evento);
		if (validateResult.equals(Constants.NO_ERROR)) {
			List<Sessao> sessoes = sessaoService.listByEvento(evento);
			model.addAttribute("sessoes", sessoes);
			model.addAttribute("evento", evento);
			return "sessao/sessao_listar_sessoes";
		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "/evento/{eventoId}/sessao/adicionar", method = RequestMethod.GET)
	public String adicionarSessaoForm(Model model, @PathVariable("eventoId") Long eventoId) {

		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateEventParams(evento);
		if (validateResult.equals(Constants.NO_ERROR)) {
			List<Pessoa> pessoas = pessoaService.getTodosInEvento(evento);
			Collections.sort(pessoas);
			model.addAttribute("pessoas", pessoas);
			model.addAttribute("evento", evento);
			return "sessao/sessao_cadastrar";
		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "evento/{eventoId}/sessao/adicionar", method = RequestMethod.POST)
	public String adicionarSessao(Sessao sessao, @PathVariable("eventoId") Long eventoId) {
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateEventParams(evento);
		
		if (sessao.getEvento() == null || sessao.getResponsavel() == null) {
			return "redirect:/sessao/" + evento.getId() + "/adicionar";
		}
		
		if (validateResult.equals(Constants.NO_ERROR)) {			
			sessaoService.addOrUpdate(sessao);
			return "redirect:/evento/" + evento.getId() + "/sessao/";

		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "/sessao/ver/{id}", method = RequestMethod.GET)
	public String verTrabalhos(@PathVariable("id") Long idSessao, Model model) {
		Sessao sessao = sessaoService.get(idSessao);
		if(sessao == null){
			return Constants.ERROR_404;
		}
		String resultValidate = validateEventParams(sessao.getEvento());
		if(!resultValidate.equals(Constants.NO_ERROR)){
			return resultValidate;
		}
		
		List<ParticipacaoTrabalho> trabalhosSessao = new ArrayList<>();
		List<Trabalho> trabalhos = new ArrayList<>();

		for (Trabalho trab : sessao.getTrabalhos()) {
			for (ParticipacaoTrabalho part : trab.getParticipacoes()) {
				if (part.getPapel().equals(Papel.Tipo.AUTOR)) {
					trabalhosSessao.add(part);
				}
			}
		}

		for (Trabalho trabalho : trabalhoService.getTrabalhosEvento(sessao.getEvento())) {
			if (trabalho.getSessao() == null) {
				trabalhos.add(trabalho);
			}
		}

		model.addAttribute("trabalhos", trabalhos);
		model.addAttribute("trabalhosSessao", trabalhosSessao);
		model.addAttribute("sessao", sessao);
		model.addAttribute("qtdTrabalhos", sessao.getTrabalhos().size());
		return "sessao/sessao_ver_trabalhos";
	}

	@RequestMapping(value = "/sessao/excluir/{id}")
	public String excluirSessao(@PathVariable("id") Long idSessao) {
		Sessao sessao = sessaoService.get(idSessao);
		
		if(sessao == null){
			return Constants.ERROR_404;
		}
		
		Long eventoId = sessao.getEvento().getId();

		for (Trabalho trabalho : sessao.getTrabalhos()) {
			trabalhoService.removerSessao(trabalho);
		}

		sessaoService.delete(idSessao);

		return "redirect:/evento/" + eventoId + "/sessao/";
	}

	@RequestMapping("/sessao/{idSessao}/excluirTrabalho/{idTrabalho}")
	public String excluirTrabalhoSessao(@PathVariable("idSessao") Long idSessao,
			@PathVariable("idTrabalho") Long idTrabalho) {
		Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
		
		if(trabalho == null){
			return Constants.ERROR_404;
		}
		
		trabalhoService.removerSessao(trabalho);
		return "redirect:/sessao/ver/" + idSessao;
	}

	@RequestMapping("/sessao/adicionar/trabalho")
	public String adicionarTrabalhoNaSessao(@RequestParam Long idSessao, @RequestParam List<Long> idTrabalhos) {
		Sessao sessao = sessaoService.get(idSessao);
		
		if(sessao == null){
			return Constants.ERROR_404;
		}
		
		for (int i = 0; i < idTrabalhos.size(); i++) {
			Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalhos.get(i));
			trabalho.setSessao(sessao);
			trabalhoService.adicionarTrabalho(trabalho);
		}
		return "redirect:/sessao/ver/" + idSessao;
	}

	@RequestMapping("/sessao/listarParticipantes/{idSessao}")
	public String listarParticipantes(@PathVariable("idSessao") Long idSessao, Model model) {
		Sessao sessao = sessaoService.get(idSessao);
		
		if(sessao == null){
			return Constants.ERROR_404;
		}
		
		model.addAttribute("sessao", sessao);
		model.addAttribute("trabalhos", trabalhoService.buscarTodosTrabalhosDaSessao(idSessao));
		return "sessao/sessao_listar_participantes";

	}

	private String validateEventParams(Evento evento) {
		Pessoa pessoa = pessoaService.getByCpf(PessoaLogadaUtil.pessoaLogada().getCpf());
		if (evento == null) {
			return Constants.ERROR_404;
		}

		if (!evento.getOrganizadores().contains(pessoa)) {
			return Constants.ERROR_403;
		}
		return Constants.NO_ERROR;
	}
	
	@RequestMapping(value = "/sessao/presenca", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String atibuirPresenca(@RequestBody PresencaJsonWrapper dadosPresenca) {

		Trabalho trabalho = trabalhoService.getTrabalhoById(dadosPresenca.getTrabalhoId());

		if (trabalho != null) {
			trabalho.setStatusApresentacao(!trabalho.getStatusApresentacao());
			trabalhoService.adicionarTrabalho(trabalho);
		}
		return "{\"result\":\"ok\"}";
	}
	
}
