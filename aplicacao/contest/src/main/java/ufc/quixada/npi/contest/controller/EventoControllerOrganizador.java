package ufc.quixada.npi.contest.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.Email;
import ufc.quixada.npi.contest.model.Email.EmailBuilder;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EnviarEmailService;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping("/eventoOrganizador")
public class EventoControllerOrganizador extends EventoGenericoController{

	private static final String ERRO_ENVIO_EMAIL = "ERRO_ENVIO_EMAIL";
	private static final String EVENTO_INATIVO = "eventoInativo";
	private static final String EVENTO_ATIVO = "eventoAtivo";
	private static final String EXISTE_SUBMISSAO = "existeSubmissao";
	private static final String SUBMISSAO_REVISAO = "existeSubmissaoRevisao";
	private static final String EVENTOS_INATIVOS = "eventosInativos";
	private static final String EVENTOS_ATIVOS = "eventosAtivos";
	private static final String TITULO_EMAIL_ORGANIZADOR="TITULO_EMAIL_CONVITE_ORGANIZADOR";
	private static final String TEXTO_EMAIL_ORGANIZADOR="TEXTO_EMAIL_CONVITE_ORGANIZADOR";
	
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
		model.addAttribute("evento", eventoService.buscarEventoPorId(eventoId) );
		model.addAttribute("qtdTrilhas", trilhaService.buscarQtdTrilhasPorEvento(eventoId));
		return Constants.TEMPLATE_DETALHES_EVENTO_ORG;
	}

	@RequestMapping(value = {"/ativos",""}, method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstadoAndPapelOrganizador(EstadoEvento.ATIVO);
		model.addAttribute(EVENTOS_ATIVOS, listaEventos);
		return Constants.TEMPLATE_LISTAR_EVENTOS_ATIVOS_ORG;
	}

	@RequestMapping(value = "/inativos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstadoAndPapelOrganizador(EstadoEvento.INATIVO);
		model.addAttribute(EVENTOS_INATIVOS, listaEventos);
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

	@RequestMapping(value = "/detalhes-evento/{id}", method = RequestMethod.GET)
	public String detalhesEvento(@PathVariable String id, Model model, RedirectAttributes redirect) {
		try {
			Long eventoId = Long.valueOf(id);
			
			if (eventoService.existeEvento(eventoId)) {
				Evento evento = eventoService.buscarEventoPorId(eventoId);
				if (evento.getEstado().equals(EstadoEvento.ATIVO)) {
					model.addAttribute("evento", evento);
					model.addAttribute("qtdTrilhas", evento.getTrilhas().size());
					model.addAttribute("revisores", pessoaService.pessoasPorPapelNoEvento(Papel.REVISOR, eventoId).size());
					model.addAttribute("qtdTrabalhos", trabalhoService.getTrabalhosEvento(evento).size());
					return Constants.TEMPLATE_DETALHES_EVENTO_ORG;
				} else {
					model.addAttribute(EVENTO_INATIVO, messageService.getMessage("EVENTO_INATIVO"));
				}
			}else {
				model.addAttribute(EVENTO_INEXISTENTE, messageService.getMessage("EVENTO_NAO_EXISTE"));
			}
		} catch (NumberFormatException e) {
			model.addAttribute(EVENTO_INEXISTENTE, messageService.getMessage("EVENTO_NAO_EXISTE"));
		}
		return Constants.TEMPLATE_DETALHES_EVENTO_ORG;
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
		}
		return Constants.TEMPLATE_LISTAR_TRILHAS_ORG;
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
	
	@RequestMapping(value = "/convidar/{id}", method = RequestMethod.GET)
	public String convidarPessoasPorEmail(@PathVariable String id, Model model,  RedirectAttributes redirect) {
		Long eventoId = Long.parseLong(id);
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		
		if(evento.getEstado().equals(EstadoEvento.ATIVO)){
			model.addAttribute("nomeEvento", evento.getNome());			
			return Constants.TEMPLATE_CONVIDAR_PESSOAS_EMAIL_ORG;
			
		}else{
			redirect.addFlashAttribute("organizadorError", messageService.getMessage("PARTICIPAR_EVENTO_INATIVO_OU_FINALIZADO"));
			return "redirect:/eventoOrganizador/ativos";
		}
    }
	
	@RequestMapping(value = "/convidar", method = RequestMethod.POST)
	public String convidarPorEmail(@RequestParam("nome") String nome,@RequestParam("email") String email,@RequestParam("funcao") String funcao, @RequestParam String nomeEvento,
			BindingResult result, Model model, RedirectAttributes redirect) {
		if (!result.hasErrors()) {
			String assunto =  messageService.getMessage(TITULO_EMAIL_ORGANIZADOR) + nomeEvento;
			String corpo = nome + messageService.getMessage(TEXTO_EMAIL_ORGANIZADOR) + funcao;
			
			EmailBuilder builder = new EmailBuilder(nome, assunto, email, corpo, nomeEvento );
			Email mail = builder.build();
			EnviarEmailService serviceEmail = new EnviarEmailService(mail);
			if(!serviceEmail.enviarEmail()){
				model.addAttribute("organizadorError", messageService.getMessage(ERRO_ENVIO_EMAIL)); 
			}
		}else{
			model.addAttribute("organizadorError", messageService.getMessage(ERRO_ENVIO_EMAIL)); 
		}
		return "redirect:/eventoOrganizador/ativos";	
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
			model.addAttribute("evento", trilha.getEvento());
			return Constants.TEMPLATE_DETALHES_TRILHA_ORG;
		}else{
			redirect.addFlashAttribute("organizadorError", messageService.getMessage("EVENTO_NAO_ENCONTRADO"));
			return Constants.TEMPLATE_LISTAR_TRILHAS_ORG;
		}
	}
	
	@RequestMapping(value = "/trilha/editar", method = RequestMethod.POST)
	public String atualizarTrilha(@RequestParam(required = false) String eventoId, @Valid Trilha trilha, Model model, BindingResult result, RedirectAttributes redirect){
		long idEvento = Long.parseLong(eventoId);
		model.addAttribute("trilha", trilhaService.get(trilha.getId(), idEvento));
		if (trilha.getNome().isEmpty()) {
			redirect.addFlashAttribute("organizadorError", messageService.getMessage("TRILHA_NOME_VAZIO"));
		}else{
			if (eventoService.existeEvento(idEvento)) {
				if (trilhaService.exists(trilha.getNome(), idEvento)) {
					redirect.addFlashAttribute("organizadorError", messageService.getMessage("TRILHA_NOME_JA_EXISTE"));
				}else if (trilhaService.existeTrabalho(trilha.getId()) ) {
					redirect.addFlashAttribute("organizadorError", messageService.getMessage("TRILHA_POSSUI_TRABALHO"));
				}else{
					trilha.setEvento(eventoService.buscarEventoPorId(idEvento));
					trilhaService.adicionarOuAtualizarTrilha(trilha);
					model.addAttribute("trilha", trilhaService.get(trilha.getId(), idEvento));
				}
			}else{
				redirect.addFlashAttribute("organizadorError", messageService.getMessage("EVENTO_NAO_EXISTE"));
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
	
	public Pessoa getOrganizadorLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String cpf = auth.getName();
		return pessoaService.getByCpf(cpf);
	}

}