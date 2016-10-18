package ufc.quixada.npi.contest.controller;

import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;

@Controller
@RequestMapping("/revisor")
public class RevisorController {
	
	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private TrabalhoService trabalhoService;
	
	private static final String REVISOR_INDEX = "revisor/revisor_index";
	private static final String REVISOR_MEUS_EVENTOS = "revisor/revisor_meus_eventos";
	private static final String REVISOR_TRABALHOS_REVISAO = "revisor/revisor_trabalhos";
	private static final String REVISOR_AVALIAR_TRABALHO = "revisor/revisor_avaliar_trabalho";
	
	@RequestMapping
	public String index(Model model){
		Pessoa revisor = getRevisorLogado();
		model.addAttribute("eventos", eventoService.eventosParaParticipar(revisor.getId()));
		return REVISOR_INDEX;
	}
	
	@RequestMapping(value="/meusEventos", method = RequestMethod.GET)
	public String meusEventos(Model model){
		Pessoa revisor = getRevisorLogado();
		//ver isso aqui
		model.addAttribute("eventos", eventoService.buscarEventosParticapacaoAutor(revisor.getId()));
		return REVISOR_MEUS_EVENTOS;
	}
	
	@RequestMapping(value="/{idEvento}/trabalhosRevisao")
	public String trabalhosRevisao(Model model, @PathVariable("idEvento") Long idEvento){
		Evento evento = eventoService.buscarEventoPorId(idEvento);
		if(evento == null)
			return "";
		
		Pessoa revisor = getRevisorLogado();
		model.addAttribute("trabalhos", trabalhoService.getTrabalhosParaRevisar(revisor.getId(), idEvento));
		model.addAttribute("evento", evento);
		return REVISOR_TRABALHOS_REVISAO;
	}
	
	@RequestMapping(value="/{idEvento}/{idTrabalho}/revisar", method=RequestMethod.GET)
	public String revisarTrabalho(Model model, @PathVariable("idTrabalho") Long idTrabalho,
			@PathVariable("idEvento") Long idEvento){
		Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
		Evento evento = eventoService.buscarEventoPorId(idEvento);
		if(trabalho == null || evento == null)
			return "";
		
		model.addAttribute("nomeEvento", evento.getNome());
		model.addAttribute("trabalho", trabalho);
		model.addAttribute("autores", getAutoresDoTrabalho(trabalho));
		model.addAttribute("coAutores", getCoAutoresDoTrabalho(trabalho));
		return REVISOR_AVALIAR_TRABALHO;
	}
	
	public Pessoa getRevisorLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String cpf = auth.getName();
		Pessoa autorLogado = pessoaService.getByCpf(cpf);
		return autorLogado;
	}
	
	public List<Pessoa> getAutoresDoTrabalho(Trabalho trabalho){
		List<Pessoa> autores = new Vector<Pessoa>();
		for(ParticipacaoTrabalho p : trabalho.getParticipacoes()){
			if(p.getPapel() == Papel.AUTOR)
				autores.add(p.getPessoa());
		}
		
		return autores;
	}
	
	public List<Pessoa> getCoAutoresDoTrabalho(Trabalho trabalho){
		List<Pessoa> coAutores = new Vector<Pessoa>();
		for(ParticipacaoTrabalho p : trabalho.getParticipacoes()){
			if(p.getPapel() == Papel.COAUTOR)
				coAutores.add(p.getPessoa());
		}
		
		return coAutores;
	}
	
}