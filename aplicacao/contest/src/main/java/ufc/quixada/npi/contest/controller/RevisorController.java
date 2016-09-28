package ufc.quixada.npi.contest.controller;

import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
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
	private MessageService messageService;
	
	@Autowired
	private ParticipacaoEventoService participacaoEventoService;
	
	@Autowired
	private TrabalhoService trabalhoService;
	
	private static final String REVISOR_INDEX = "revisor/revisor_index";
	private static final String REVISOR_MEUS_EVENTOS = "revisor/revisor_meus_eventos";
	private static final String REVISOR_TRABALHOS_REVISAO = "revisor/revisor_trabalhos";
	private static final String REVISOR_AVALIAR_TRABALHO = "revisor/revisor_avaliar_trabalho";
	
	
	private static final String EVENTO_VAZIO_ERROR = "eventoVazioError";
	private static final String ID_EVENTO_VAZIO_ERROR = "ID_EVENTO_VAZIO_ERROR";
	private static final String PARTICAPACAO_EVENTO_SUCESSO = "particapacaoEventoSucesso";
	private static final String PARTICIPAR_EVENTO_INATIVO_ERROR = "participarEventoInativoError";
	private static final String PARTICAPAR_EVENTO_SUCESSO = "PARTICAPAR_EVENTO_SUCESSO";
	private static final String PARTICIPAR_EVENTO_INATIVO = "PARTICIPAR_EVENTO_INATIVO";
	private static final String EVENTO_INEXISTENTE_ERROR = "eventoInexistenteError";
	private static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	
	@RequestMapping
	public String index(Model model){
		Pessoa revisor = getRevisorLogado();
		model.addAttribute("eventos", eventoService.eventosParaParticipar(revisor.getId()));
		return REVISOR_INDEX;
	}
	
	@RequestMapping(value="/meusEventos", method = RequestMethod.GET)
	public String meusEventos(Model model){
		Pessoa revisor = getRevisorLogado();
		model.addAttribute("eventos", eventoService.buscarEventosParticapacaoRevisor(revisor.getId()));
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
	
	@RequestMapping(value = "/participarevento", method = RequestMethod.POST)
	public String professorParticipa(@RequestParam String idEvento, Model model, RedirectAttributes redirect) {
		if (!eventoService.existeEvento(Long.parseLong(idEvento))) {
			redirect.addFlashAttribute(EVENTO_VAZIO_ERROR, messageService.getMessage(ID_EVENTO_VAZIO_ERROR));
			return "redirect:/revisor";
		}
		
		Pessoa professorLogado = getRevisorLogado();
		
		Evento evento = eventoService.buscarEventoPorId(Long.parseLong(idEvento));
		
		if(evento != null){
			if(evento.getEstado() == EstadoEvento.ATIVO){
				ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
				participacaoEvento.setEvento(evento);
				participacaoEvento.setPessoa(professorLogado);
				participacaoEvento.setPapel(Papel.REVISOR);
				
				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento);
				redirect.addFlashAttribute(PARTICAPACAO_EVENTO_SUCESSO, messageService.getMessage(PARTICAPAR_EVENTO_SUCESSO));
			}else{
				redirect.addFlashAttribute(PARTICIPAR_EVENTO_INATIVO_ERROR, messageService.getMessage(PARTICIPAR_EVENTO_INATIVO));
			}
		}else{
			redirect.addFlashAttribute(EVENTO_INEXISTENTE_ERROR, messageService.getMessage(EVENTO_NAO_EXISTE));
			return "redirect:/revisor";
		}
		
		return "redirect:/revisor";
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