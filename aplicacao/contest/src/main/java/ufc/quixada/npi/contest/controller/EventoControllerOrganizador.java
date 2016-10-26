package ufc.quixada.npi.contest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import ufc.quixada.npi.contest.model.RevisaoJsonWrapper;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping("/eventoOrganizador")
public class EventoControllerOrganizador extends EventoGenericoController{

	private static final String EVENTO_QUE_PARTICIPO = "eventoQueParticipo";
	private static final String EVENTOS_QUE_ORGANIZO = "eventosQueOrganizo";
	private static final String EVENTO_INATIVO = "eventoInativo";
	private static final String EVENTO_ATIVO = "eventoAtivo";
	private static final String EXISTE_SUBMISSAO = "existeSubmissao";
	private static final String SUBMISSAO_REVISAO = "existeSubmissaoRevisao";
	private static final String EVENTOS_INATIVOS = "eventosInativos";
	private static final String EVENTOS_ATIVOS = "eventosAtivos";
	
	private static final String EVENTO_VAZIO_ERROR = "eventoVazioError";
	private static final String ID_EVENTO_VAZIO_ERROR = "ID_EVENTO_VAZIO_ERROR";
	private static final String PARTICAPACAO_EVENTO_SUCESSO = "particapacaoEventoSucesso";
	private static final String PARTICIPAR_EVENTO_INATIVO_ERROR = "participarEventoInativoError";
	private static final String PARTICAPAR_EVENTO_SUCESSO = "PARTICAPAR_EVENTO_SUCESSO";
	private static final String PARTICIPAR_EVENTO_INATIVO = "PARTICIPAR_EVENTO_INATIVO";
	private static final String EVENTO_INEXISTENTE_ERROR = "eventoInexistenteError";
	private static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	
	private static final String EVENTO_INEXISTENTE = "eventoInexistente";

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	@Autowired
	private EventoService eventoService;

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private TrilhaService trilhaService;
	
	@Autowired
	private TrabalhoService trabalhoService;
	
	@Autowired
	private ParticipacaoTrabalhoService participacaoTrabalhoService;
	
	@Autowired
	private RevisaoService revisaoService;
	
	@Autowired
	private SubmissaoService submissaoService;
	
	@ModelAttribute("pessoas")
	public List<Pessoa> listaPossiveisOrganizadores() {
		return pessoaService.getPossiveisOrganizadores();
	}
	
	@RequestMapping(value = "/evento/{id}", method = RequestMethod.GET)
	public String detalhesEvento(@PathVariable String id, Model model) {
		Long eventoId = Long.parseLong(id);
		Pessoa pessoa = getOrganizadorLogado();
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		List<Pessoa> pessoas = new ArrayList<Pessoa>(); 
		List<Trilha> trilhas = trilhaService.buscarTrilhas(Long.parseLong(id));
		boolean organizaEvento = false;
		
		for(ParticipacaoEvento pe : pessoa.getParticipacoesEvento()){
			if(pe.getEvento().getId() == evento.getId() && pe.getPapel() == Papel.ORGANIZADOR){
				organizaEvento = true;
				pessoas = pessoaService.getPossiveisOrganizadoresDoEvento(eventoId);
			}
		}
		
		model.addAttribute("trilhasEvento", trilhas);
		model.addAttribute("organizaEvento", organizaEvento);
		model.addAttribute("evento", evento);
		model.addAttribute("pessoas", pessoas);
		model.addAttribute("numeroTrilhas", trilhaService.buscarQtdTrilhasPorEvento(eventoId));
		model.addAttribute("numeroRevisores", participacaoEventoService.buscarQuantidadeRevisoresPorEvento(eventoId));
		
		int trabalhosSubmetidos = trabalhoService.buscarQuantidadeTrabalhosPorEvento(evento);
		int trabalhosNaoRevisados = trabalhoService.buscarQuantidadeTrabalhosNaoRevisadosPorEvento(evento);
		int trabalhosRevisados = trabalhosSubmetidos - trabalhosNaoRevisados;
		
		model.addAttribute("numeroTrabalhos", trabalhosSubmetidos);
		model.addAttribute("numeroTrabalhosNaoRevisados", trabalhosNaoRevisados);
		model.addAttribute("numeroTrabalhosRevisados", trabalhosRevisados);
		
		model.addAttribute("comentarios", trabalhoService.buscarQuantidadeTrabalhosRevisadosEComentadosPorEvento(evento));
		
		return Constants.TEMPLATE_DETALHES_EVENTO_ORG;
	}
	
	@RequestMapping(value = "/evento/{id}/revisores", method = RequestMethod.GET)
	public String gerenciarRevisor(@PathVariable String id, Model model) {
		Long eventoId = Long.parseLong(id);
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		List<Trabalho> trabalhos = trabalhoService.getTrabalhosEvento(evento);
		model.addAttribute("revisores", pessoaService.getRevisoresDoEventoQueNaoParticipaDoTrabalho(eventoId));
		model.addAttribute("evento", evento);
		model.addAttribute("trabalhos", trabalhos);
		return Constants.TEMPLATE_ATRIBUIR_REVISOR_ORG;
	}
	
	@RequestMapping(value = "/evento/trabalho/revisor",method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String atibuirRevisor(@RequestBody RevisaoJsonWrapper dadosRevisao) {
		
		Pessoa revisor = pessoaService.get(dadosRevisao.getRevisorId());
		Trabalho trabalho = trabalhoService.getTrabalhoById(dadosRevisao.getTrabalhoId());
		
		ParticipacaoTrabalho participacaoTrabalho = new ParticipacaoTrabalho();
		participacaoTrabalho.setPapel(Papel.REVISOR);
		participacaoTrabalho.setPessoa(revisor);
		participacaoTrabalho.setTrabalho(trabalho);

		participacaoTrabalhoService.adicionarOuEditar(participacaoTrabalho);
		
		return "{\"result\":\"ok\"}";
	}
	
	@RequestMapping(value = "/evento/trabalho/removerRevisor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String removerRevisor(@RequestBody RevisaoJsonWrapper dadosRevisao){
		ParticipacaoTrabalho participacao = participacaoTrabalhoService.getParticipacaoTrabalhoRevisor(dadosRevisao.getRevisorId(), dadosRevisao.getTrabalhoId());
		participacaoTrabalhoService.remover(participacao);
		return "{\"result\":\"ok\"}";
	}
	
	@RequestMapping(value={"/meusEventos",""}, method = RequestMethod.GET)
	public String meusEventos(Model model){
		Pessoa revisor = getOrganizadorLogado();
		model.addAttribute("eventos", eventoService.buscarMeusEventos(revisor.getId()));
		return Constants.TEMPLATE_MEUS_EVENTOS_ORG;
	}
	
	@RequestMapping(value = "/ativos", method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		Pessoa p = getOrganizadorLogado();
		List<Evento> eventos = eventoService.buscarEventoPorEstado(EstadoEvento.ATIVO);
		List<Evento> eventosQueReviso= eventoService.buscarEventosParticapacaoRevisor(p.getId());
		boolean existeEventos = true;
		
		for(Evento e : eventosQueReviso){
			eventos.remove(e);
		}
		if(eventos.isEmpty() && eventosQueReviso.isEmpty())
			existeEventos = false;
		
		model.addAttribute("existeEventos", existeEventos);
		model.addAttribute(EVENTOS_ATIVOS, eventos);
		model.addAttribute(EVENTO_QUE_PARTICIPO, eventosQueReviso);
		return Constants.TEMPLATE_LISTAR_EVENTOS_ATIVOS_ORG;
	}

	@RequestMapping(value = "/inativos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		Pessoa p = getOrganizadorLogado();
		
		List<Evento> eventos = eventoService.buscarEventoPorEstado(EstadoEvento.INATIVO);
		List<Evento> eventosQueOrganizo = eventoService.buscarEventosInativosQueOrganizo(p.getId());
		
		boolean existeEventos = true;
		
		for(Evento e : eventosQueOrganizo){
			eventos.remove(e);
		}
		
		if(eventos.isEmpty() && eventosQueOrganizo.isEmpty())
			existeEventos = false;
		
		model.addAttribute("existeEventos", existeEventos);
		model.addAttribute(EVENTOS_INATIVOS, eventos);
		model.addAttribute(EVENTOS_QUE_ORGANIZO, eventosQueOrganizo);
		return Constants.TEMPLATE_LISTAR_EVENTOS_INATIVOS_ORG;
	}
	
	@RequestMapping(value = "/editar/{id}", method = RequestMethod.GET)
	public String alterarEventoOrganizador(@PathVariable String id, Model model, RedirectAttributes redirect){
		boolean exiteSubmissao = submissaoService.existeTrabalhoNesseEvento(Long.valueOf(id));
		boolean existeRevisao = revisaoService.existeTrabalhoNesseEvento(Long.valueOf(id));
		
		if(exiteSubmissao){
			if(existeRevisao){
				model.addAttribute(SUBMISSAO_REVISAO, exiteSubmissao);
			}else{
				model.addAttribute(EXISTE_SUBMISSAO, exiteSubmissao);
			}
		}else{
			Evento evento = eventoService.buscarEventoPorId(Long.valueOf(id));
			boolean eventoAtivo = true;
			boolean eventoInativo = true;
			if(evento.getEstado().equals(EstadoEvento.ATIVO)){
				model.addAttribute(EVENTO_ATIVO,eventoAtivo);
			}else{
				model.addAttribute(EVENTO_INATIVO,eventoInativo);
			}
		}
		return alterarEvento(id, model, redirect, Constants.TEMPLATE_EDITAR_EVENTO_ORG, "redirect:/eventoOrganizador/inativos");
	}
	
	@RequestMapping(value = "/editar", method = RequestMethod.POST)
	public String editarEvento(@Valid Evento evento, BindingResult result, Model model, RedirectAttributes redirect){
		return ativarOuEditarEvento(evento, result, model, redirect, "redirect:/eventoOrganizador/ativos", Constants.TEMPLATE_EDITAR_EVENTO_ORG);
	}

	@RequestMapping(value = "/ativar/{id}", method = RequestMethod.GET)
	public String ativarEvento(@PathVariable String id, Model model, RedirectAttributes redirect){
		try{
			Long idEvento = Long.valueOf(id);
			Evento evento = eventoService.buscarEventoPorId(idEvento);
			model.addAttribute("evento", evento);
			return Constants.TEMPLATE_ATIVAR_EVENTO_ORG;
		}catch(NumberFormatException e){
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
		}
		return "redirect:/eventoOrganizador/inativos";
	}

	@RequestMapping(value = "/trilhas/{id}", method = RequestMethod.GET)
	public String listaTrilhas(@PathVariable String id, Model model, RedirectAttributes redirect) {
		try{
			Long eventoId = Long.valueOf(id);
			model.addAttribute("trilhas", trilhaService.buscarTrilhas(eventoId));
			model.addAttribute("trilha", new Trilha());
			model.addAttribute("evento", eventoService.buscarEventoPorId(eventoId));
			return Constants.TEMPLATE_LISTAR_TRILHAS_ORG;
		}catch(NumberFormatException e){
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
			return "redirect:/eventoOrganizador/evento" + id;
		}
	}
	
	@RequestMapping(value = "/trilha/{idTrilha}/{idEvento}", method = RequestMethod.GET)
	public String detalhesTrilha(@PathVariable String idTrilha,@PathVariable String idEvento, Model model, RedirectAttributes redirect) {
		try{
			Long trilhaId = Long.valueOf(idTrilha);
			Long eventoId = Long.valueOf(idEvento);
			Trilha trilha = trilhaService.get(trilhaId, eventoId);
			model.addAttribute("trilha", trilha);
			model.addAttribute("trabalhos", trabalhoService.getTrabalhosTrilha(trilha));
			return Constants.TEMPLATE_DETALHES_TRILHA_ORG;
		}catch(NumberFormatException e){
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
		}
		return Constants.TEMPLATE_LISTAR_TRILHAS_ORG;
	}

	@RequestMapping(value = "/ativar", method = RequestMethod.POST)
	public String ativarEvento(@Valid Evento evento, BindingResult result, Model model, RedirectAttributes redirect){
		return ativarOuEditarEvento(evento, result, model, redirect, "redirect:/eventoOrganizador/ativos", Constants.TEMPLATE_ATIVAR_EVENTO_ORG);
	}

	@RequestMapping(value = "/trilhas", method = RequestMethod.POST)
	public String cadastraTrilha(@RequestParam(required = false) String eventoId, @Valid Trilha trilha, Model model, RedirectAttributes redirect){
		long id = Long.parseLong(eventoId);
		if (trilhaService.exists(trilha.getNome(), id)) {
			redirect.addFlashAttribute("organizadorError", messageService.getMessage("TRILHA_NOME_JA_EXISTE"));
			return "redirect:/eventoOrganizador/trilhas/" + eventoId;
		}
		if (eventoService.existeEvento(id)) {
			trilha.setEvento(eventoService.buscarEventoPorId(id));
			trilhaService.adicionarOuAtualizarTrilha(trilha);
			redirect.addFlashAttribute("trilhaAdd", messageService.getMessage("TRILHA_ADD_COM_SUCESSO"));
			return "redirect:/eventoOrganizador/trilhas/" + eventoId;
		}else{
			redirect.addFlashAttribute("organizadorError", messageService.getMessage("EVENTO_NAO_ENCONTRADO"));
			return "redirect:/eventoOrganizador/trilhas/" + eventoId;
		}
	}
	
	@RequestMapping(value = "/trilha/editar", method = RequestMethod.POST)
	public String atualizarTrilha(@RequestParam(required = false) String eventoId, @Valid Trilha trilha, Model model, BindingResult result, RedirectAttributes redirect){
		long idEvento = Long.parseLong(eventoId);
		model.addAttribute("trilha", trilhaService.get(trilha.getId(), idEvento));
		if (trilha.getNome().isEmpty()) {
			model.addAttribute("organizadorError", messageService.getMessage("TRILHA_NOME_VAZIO"));
		}else{
			if (eventoService.existeEvento(idEvento)) {
				if (trilhaService.exists(trilha.getNome(), idEvento)) {
					model.addAttribute("organizadorError", messageService.getMessage("TRILHA_NOME_JA_EXISTE"));
				}else if (trilhaService.existeTrabalho(trilha.getId()) ) {
					model.addAttribute("organizadorError", messageService.getMessage("TRILHA_POSSUI_TRABALHO"));
				}else{
					trilha.setEvento(eventoService.buscarEventoPorId(idEvento));
					trilhaService.adicionarOuAtualizarTrilha(trilha);
					model.addAttribute("trilha", trilhaService.get(trilha.getId(), idEvento));
				}
			}else{
				model.addAttribute("organizadorError", messageService.getMessage("EVENTO_NAO_EXISTE"));
			}
		}
		return Constants.TEMPLATE_DETALHES_TRILHA_ORG;
	}
	
	@RequestMapping(value = "/trilha/excluir/{trilhaId}/{eventoId}", method = RequestMethod.GET)
	public String excluirTrilha(@PathVariable String trilhaId, @PathVariable String eventoId, Model model, RedirectAttributes redirect){
		Long idEvento = Long.valueOf(eventoId);
		Long idTrilha = Long.valueOf(trilhaId);
		if (eventoService.existeEvento(idEvento) && !trilhaService.existeTrabalho(idTrilha)) {
			Trilha trilha = trilhaService.get(idTrilha, idEvento);
			trilhaService.removerTrilha(trilha);
			return "redirect:/eventoOrganizador/trilhas/"+ eventoId;
		}
		redirect.addFlashAttribute("organizadorError", messageService.getMessage("EVENTO_VAZIO_OU_TEM_TRABALHO"));
		return "redirect:/eventoOrganizador/trilhas/"+ eventoId;
	}
	
	@RequestMapping(value = "/addOrganizadores", method = RequestMethod.POST)
	public String addOrganizadores(@RequestParam(required = false) String idOrganizadores, @RequestParam String idEvento, Model model){
		if(idOrganizadores != null){
			String ids[] = idOrganizadores.split(Pattern.quote(","));
			List<Pessoa> organizadores = new ArrayList<>();
			for(String id : ids){
				organizadores.add(pessoaService.get(Long.parseLong(id)));
			}
			Evento evento = eventoService.buscarEventoPorId(Long.parseLong(idEvento));
			ParticipacaoEvento participacao = new ParticipacaoEvento();
			
			for(Pessoa p : organizadores){
				participacao.setEvento(evento);
				participacao.setPessoa(p);
				participacao.setPapel(Papel.ORGANIZADOR);
				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacao);
			}
			return "redirect:/eventoOrganizador/evento/"+idEvento;
		}
		return "redirect:/eventoOrganizador/evento/"+idEvento;
	}
	
	@RequestMapping(value = "/participarevento", method = RequestMethod.POST)
	public String professorParticipa(@RequestParam String idEvento, Model model, RedirectAttributes redirect) {
		if (!eventoService.existeEvento(Long.parseLong(idEvento))) {
			redirect.addFlashAttribute(EVENTO_VAZIO_ERROR, messageService.getMessage(ID_EVENTO_VAZIO_ERROR));
			return "redirect:/eventoOrganizador";
		}
		
		Pessoa professorLogado = getOrganizadorLogado();
		
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
			return "redirect:/eventoOrganizador";
		}
		
		return "redirect:/eventoOrganizador";
	}
	
	public Pessoa getOrganizadorLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String cpf = auth.getName();
		return pessoaService.getByCpf(cpf);
	}
}