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
	private SecaoService sessaoService;
	@Autowired
	private TrabalhoService trabalhoService;
	@Autowired
	private EventoService eventoService;
	@Autowired
	private PessoaService pessoaService;

	@RequestMapping(value = "{eventoId}")
	public String indexSecao(Model model, @PathVariable("eventoId") Long eventoId) {
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateEventParams(evento);
		if (validateResult.equals(Constants.NO_ERROR)) {
			List<Secao> sessoes = sessaoService.listByEvento(evento);
			model.addAttribute("sessoes", sessoes);
			model.addAttribute("evento", evento);
			return "secao/sessao_listar_sessoes";
		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "{eventoId}/adicionar", method = RequestMethod.GET)
	public String adicionarSessaoForm(Model model, @PathVariable("eventoId") Long eventoId) {

		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateEventParams(evento);
		if (validateResult.equals(Constants.NO_ERROR)) {
			List<Pessoa> pessoas = pessoaService.getTodosInEvento(evento);
			Collections.sort(pessoas);
			model.addAttribute("pessoas", pessoas);
			model.addAttribute("evento", evento);
			return "secao/sessao_cadastrar";
		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "{eventoId}/adicionar", method = RequestMethod.POST)
	public String adicionarSecao(Secao sessao, @PathVariable("eventoId") Long eventoId) {
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String validateResult = validateEventParams(evento);
		
		if (sessao.getEvento() == null || sessao.getResponsavel() == null) {
			return "redirect:/secao/" + evento.getId() + "/adicionar";
		}
		
		if (validateResult.equals(Constants.NO_ERROR)) {			
			sessaoService.addOrUpdate(sessao);
			return "redirect:/secao/" + evento.getId();

		} else {
			return validateResult;
		}

	}

	@RequestMapping(value = "/secaoTrabalhos/{id}", method = RequestMethod.GET)
	public String secaoTrabalhos(@PathVariable("id") Long idSecao, Model model) {
		Secao secao = sessaoService.get(idSecao);
		if(secao == null){
			return Constants.ERROR_404;
		}
		String resultValidate = validateEventParams(secao.getEvento());
		if(!resultValidate.equals(Constants.NO_ERROR)){
			return resultValidate;
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

		for (Trabalho trabalho : trabalhoService.getTrabalhosEvento(secao.getEvento())) {
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
		Secao secao = sessaoService.get(idSecao);
		
		if(secao == null){
			return Constants.ERROR_404;
		}
		
		Long eventoId = secao.getEvento().getId();

		for (Trabalho trabalho : secao.getTrabalhos()) {
			trabalhoService.removerSecao(trabalho);
		}

		sessaoService.delete(idSecao);

		return "redirect:/secao/" + eventoId;
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
		Secao secao = sessaoService.get(idSecao);
		
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
		Secao secao = sessaoService.get(idSecao);
		
		if(secao == null){
			return Constants.ERROR_404;
		}
		
		model.addAttribute("secao", secao);
		model.addAttribute("trabalhos", trabalhoService.buscarTodosTrabalhosDaSecao(idSecao));
		return "secao/listaParticipantes";

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
	
	@RequestMapping(value = "/presenca", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String atibuirPresenca(@RequestBody PresencaJsonWrapper dadosPresenca) {

		Trabalho trabalho = trabalhoService.getTrabalhoById(dadosPresenca.getTrabalhoId());

		if (trabalho != null) {
			trabalho.setStatusApresentacao(!trabalho.getStatusApresentacao());
			trabalhoService.adicionarTrabalho(trabalho);
		}
		return "{\"result\":\"ok\"}";
	}
	
}
