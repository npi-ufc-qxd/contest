package ufc.quixada.npi.contest.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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

import ufc.quixada.npi.contest.model.Avaliacao;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.PapelLdap;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.RevisaoJsonWrapper;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
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
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;
import ufc.quixada.npi.contest.util.RevisaoJSON;

@Controller
@RequestMapping("/eventoOrganizador")
public class EventoControllerOrganizador extends EventoGenericoController {

	private static final String ORGANIZADOR_SUCESS = "organizadorSucess";
	private static final String ORGANIZADOR_ERROR = "organizadorError";
	private static final String ERRO_ENVIO_EMAIL = "ERRO_ENVIO_EMAIL";
	private static final String EVENTOS_QUE_ORGANIZO = "eventosQueOrganizo";
	private static final String EXISTE_SUBMISSAO = "existeSubmissao";
	private static final String SUBMISSAO_REVISAO = "existeSubmissaoRevisao";
	private static final String SUBMISSAO_FINAL = "existeSubmissaoFinal";
	private static final String EVENTOS_INATIVOS = "eventosInativos";
	private static final String TRABALHOS_DO_EVENTO = "organizador/org_ver_trabalhos_evento";

	private static final String CONVIDAR_EVENTO_INATIVO = "CONVIDAR_EVENTO_INATIVO";
	private static final String EMAIL_ENVIADO_SUCESSO = "EMAIL_ENVIADO_SUCESSO";

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

	@PreAuthorize("isOrganizadorInEvento(#id)")
	@RequestMapping(value = "/evento/{id}", method = RequestMethod.GET)
	public String detalhesEvento(@PathVariable String id, Model model) {
		Long eventoId = Long.parseLong(id);
		Pessoa pessoa = PessoaLogadaUtil.pessoaLogada();
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		Boolean eventoPrivado = false;

		if (evento.getVisibilidade() == VisibilidadeEvento.PRIVADO) {
			eventoPrivado = true;
		}

		boolean organizaEvento = evento.getOrganizadores().contains(pessoa);

		model.addAttribute("organizaEvento", organizaEvento);
		model.addAttribute("evento", evento);
		model.addAttribute("eventoPrivado", eventoPrivado);

		int trabalhosSubmetidos = trabalhoService.buscarQuantidadeTrabalhosPorEvento(evento);
		int trabalhosNaoRevisados = trabalhoService.buscarQuantidadeTrabalhosNaoRevisadosPorEvento(evento);
		int trabalhosRevisados = trabalhosSubmetidos - trabalhosNaoRevisados;

		List<Pessoa> organizadores = pessoaService.getOrganizadoresEvento(eventoId);

		for (Pessoa organizador : organizadores) {
			if (organizador.getId().equals(pessoa.getId())) {
				model.addAttribute("gerarCertificado", true);
				break;
			}
		}

		model.addAttribute("numeroTrabalhos", trabalhosSubmetidos);
		model.addAttribute("numeroTrabalhosNaoRevisados", trabalhosNaoRevisados);
		model.addAttribute("numeroTrabalhosRevisados", trabalhosRevisados);

		model.addAttribute("comentarios",
				trabalhoService.buscarQuantidadeTrabalhosRevisadosEComentadosPorEvento(evento));

		return Constants.TEMPLATE_DETALHES_EVENTO_ORG;
	}

	@RequestMapping(value = "/evento/{id}/revisoes", method = RequestMethod.GET)
	public String consideracoesRevisores(@PathVariable String id, Model model, RedirectAttributes redirect) {
		Long eventoId = Long.parseLong(id);
		List<Revisao> revisoes = revisaoService.getRevisaoByEvento(eventoId);

		Pessoa organizadorLogado = PessoaLogadaUtil.pessoaLogada();
		Boolean participacaoComoOrganizador = participacaoEventoService.isOrganizadorDoEvento(organizadorLogado,
				eventoId);

		if (participacaoComoOrganizador) {
			if (!revisoes.isEmpty()) {
				model.addAttribute("revisoes", revisoes);
				return Constants.TEMPLATE_CONSIDERACOES_REVISORES_ORG;
			}
			redirect.addFlashAttribute("revisao_inexistente", messageService.getMessage("REVISAO_INEXISTENTE"));
			return "redirect:/eventoOrganizador/evento/" + eventoId;
		} else {
			redirect.addFlashAttribute("nao_organizador", messageService.getMessage("NAO_ORGANIZADOR"));
			return "redirect:/eventoOrganizador/evento/" + eventoId;
		}
	}

	@RequestMapping(value = "/evento/{id}/revisores", method = RequestMethod.GET)
	public String gerenciarRevisor(@PathVariable String id, Model model) {
		Long eventoId = Long.parseLong(id);
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		List<Trabalho> trabalhos = trabalhoService.getTrabalhosEvento(evento);

		Collections.sort(trabalhos);

		model.addAttribute("revisores", pessoaService.getRevisoresDoEventoQueNaoParticipaDoTrabalho(eventoId));
		model.addAttribute("evento", evento);
		model.addAttribute("trabalhos", trabalhos);
		return Constants.TEMPLATE_ATRIBUIR_REVISOR_ORG;
	}

	@RequestMapping("/evento/{id}/trabalhos")
	public String verTrabalhosDoEvento(@PathVariable("id") Long idEvento, Model model) {
		List<Trabalho> trabalhos = trabalhoService.getTrabalhosEvento(eventoService.buscarEventoPorId(idEvento));
		
		Map<Long, List<Map<String, String>>> resultadoRevisoes = new HashMap<>();
		for (Trabalho trabalho : trabalhos) {			
			List<Map<String, String>> revisoesWrappers = new ArrayList<>();
			List<Revisao> revisoes = trabalho.getRevisoes();
			for (Revisao revisao : revisoes) {
				revisoesWrappers.add(RevisaoJSON.fromJson(revisao));					
			}
			resultadoRevisoes.put(trabalho.getId(), revisoesWrappers);
		}

		Evento evento = eventoService.buscarEventoPorId(idEvento);
		if (evento == null) {
			return "redirect:/error";
		}

		List<Trabalho> trabalhosDoEvento = trabalhoService.getTrabalhosEvento(evento);

		model.addAttribute("resultadoRevisoes", resultadoRevisoes);
		model.addAttribute("evento", evento);
		model.addAttribute("opcoesFiltro", Avaliacao.values());
		model.addAttribute("trabalhos", trabalhosDoEvento);
		return TRABALHOS_DO_EVENTO;
	}

	@RequestMapping(value = "/evento/trabalho/revisor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String atibuirRevisor(@RequestBody RevisaoJsonWrapper dadosRevisao) {

		Pessoa revisor = pessoaService.get(dadosRevisao.getRevisorId());
		Trabalho trabalho = trabalhoService.getTrabalhoById(dadosRevisao.getTrabalhoId());

		if (!trabalho.isAutorInTrabalho(revisor)) {
			throw new IllegalArgumentException("Revisor selecionado é autor no trabalho");
		}

		ParticipacaoTrabalho participacaoTrabalho = participacaoTrabalhoService
				.getParticipacaoTrabalhoRevisor(revisor.getId(), trabalho.getId());

		if (participacaoTrabalho == null) {
			ParticipacaoTrabalho participacaoTrabalhoTemp = new ParticipacaoTrabalho();
			participacaoTrabalhoTemp.setPapel(Tipo.REVISOR);
			participacaoTrabalhoTemp.setPessoa(revisor);
			participacaoTrabalhoTemp.setTrabalho(trabalho);

			participacaoTrabalhoService.adicionarOuEditar(participacaoTrabalhoTemp);
		}

		return "{\"result\":\"ok\"}";
	}

	@RequestMapping(value = "/evento/trabalho/removerRevisor", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String removerRevisor(@RequestBody RevisaoJsonWrapper dadosRevisao) {
		ParticipacaoTrabalho participacao = participacaoTrabalhoService
				.getParticipacaoTrabalhoRevisor(dadosRevisao.getRevisorId(), dadosRevisao.getTrabalhoId());
		participacaoTrabalhoService.remover(participacao);
		return "{\"result\":\"ok\"}";
	}

	@RequestMapping(value = "/removerOrganizador/{eventoId}", method = RequestMethod.POST)
	public String removerOrganizador(@RequestParam("pessoaId") Long pessoaId, @PathVariable Long eventoId,
			RedirectAttributes redirect) {
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		List<ParticipacaoEvento> organizadores = participacaoEventoService.getOrganizadoresNoEvento(eventoId);

		if (organizadores.size() <= 1) {
			redirect.addFlashAttribute(ORGANIZADOR_ERROR, "Deve existir pelo menos um organizador no evento.");
		} else {
			Pessoa pessoa = pessoaService.get(pessoaId);
			ParticipacaoEvento participacao = participacaoEventoService.buscarOrganizadorPorPessoaEEvento(evento,
					pessoa);
			participacaoEventoService.remover(participacao);
			redirect.addFlashAttribute(ORGANIZADOR_SUCESS, "Organizador removido do evento.");
		}

		return "redirect:/eventoOrganizador/evento/" + evento.getId();
	}

	@RequestMapping(value = "/removerRevisor/{eventoId}", method = RequestMethod.POST)
	public String removerRevisorDoEvento(@RequestParam("pessoaId") Long pessoaId, @PathVariable Long eventoId,
			RedirectAttributes redirect) {
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		Pessoa pessoa = pessoaService.get(pessoaId);
		ParticipacaoEvento participacao = participacaoEventoService.buscarRevisorPorPessoaEEvento(evento, pessoa);
		participacaoEventoService.remover(participacao);
		redirect.addFlashAttribute(ORGANIZADOR_SUCESS, "Revisor removido do evento.");

		return "redirect:/eventoOrganizador/evento/" + evento.getId();
	}

	@RequestMapping(value = { "/meusEventos", "" }, method = RequestMethod.GET)
	public String meusEventos(Model model) {
		Pessoa revisor = PessoaLogadaUtil.pessoaLogada();
		model.addAttribute("eventos", eventoService.getMeusEventos(revisor.getId()));
		return Constants.TEMPLATE_MEUS_EVENTOS_ORG;
	}

	@RequestMapping(value = "/ativos", method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		Pessoa p = PessoaLogadaUtil.pessoaLogada();
		List<Evento> eventosAtivos = eventoService.buscarEventoPorEstado(EstadoEvento.ATIVO);
		List<ParticipacaoEvento> participacoesComoOrganizador = participacaoEventoService
				.getEventosDoOrganizador(EstadoEvento.ATIVO, p.getId());
		boolean existeEventos = true;

		if (eventosAtivos.isEmpty())
			existeEventos = false;

		List<Long> eventosComoOrganizador = new ArrayList<>();

		for (ParticipacaoEvento participacaoEvento : participacoesComoOrganizador) {
			eventosComoOrganizador.add(participacaoEvento.getEvento().getId());
		}

		model.addAttribute("existeEventos", existeEventos);
		model.addAttribute("eventosAtivos", eventosAtivos);
		model.addAttribute("eventosComoOrganizador", eventosComoOrganizador);
		model.addAttribute("pessoa", p);
		return Constants.TEMPLATE_LISTAR_EVENTOS_ATIVOS_ORG;
	}

	@RequestMapping(value = "/inativos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		Pessoa p = PessoaLogadaUtil.pessoaLogada();

		List<Evento> eventos = eventoService.buscarEventoPorEstado(EstadoEvento.INATIVO);
		List<Evento> eventosQueOrganizo = eventoService.getMeusEventosInativosComoOrganizador(p.getId());

		boolean existeEventos = true;

		for (Evento e : eventosQueOrganizo) {
			eventos.remove(e);
		}

		if (eventos.isEmpty() && eventosQueOrganizo.isEmpty())
			existeEventos = false;

		model.addAttribute("existeEventos", existeEventos);
		model.addAttribute(EVENTOS_INATIVOS, eventos);
		model.addAttribute(EVENTOS_QUE_ORGANIZO, eventosQueOrganizo);

		return Constants.TEMPLATE_LISTAR_EVENTOS_INATIVOS_ORG;
	}

	@PreAuthorize("isOrganizadorInEvento(#id)")
	@RequestMapping(value = "/editar/{id}", method = RequestMethod.GET)
	public String alterarEventoOrganizador(@PathVariable String id, Model model, RedirectAttributes redirect) {
		Long eventoId = Long.valueOf(id);
		boolean existeSubmissao = submissaoService.existeTrabalhoNesseEvento(eventoId);
		boolean existeRevisao = revisaoService.existeTrabalhoNesseEvento(eventoId);
		boolean existeSubmissaoFinal = submissaoService.existeTrabalhoFinalNesseEvento(eventoId);

		if (existeSubmissao) {
			model.addAttribute(EXISTE_SUBMISSAO, existeSubmissao);
			if (existeRevisao) {
				model.addAttribute(SUBMISSAO_REVISAO, existeSubmissao);
				if (existeSubmissaoFinal) {
					model.addAttribute(SUBMISSAO_FINAL, existeSubmissaoFinal);
				}
			}
		}
		return alterarEvento(id, model, redirect, Constants.TEMPLATE_EDITAR_EVENTO_ORG,
				"redirect:/eventoOrganizador/inativos");
	}

	@RequestMapping(value = "/editar", method = RequestMethod.POST)
	public String editarEvento(@Valid Evento evento, BindingResult result, Model model, RedirectAttributes redirect) {
		return ativarOuEditarEvento(evento, result, model, redirect, "redirect:/eventoOrganizador/ativos",
				Constants.TEMPLATE_EDITAR_EVENTO_ORG);
	}

	@RequestMapping(value = "/ativar/{id}", method = RequestMethod.GET)
	public String ativarEvento(@PathVariable Long id, Model model, RedirectAttributes redirect) {
		try {
			Evento evento = eventoService.buscarEventoPorId(id);
			model.addAttribute("evento", evento);
			return Constants.TEMPLATE_ATIVAR_EVENTO_ORG;
		} catch (NumberFormatException e) {
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
		}
		return "redirect:/eventoOrganizador/inativos";
	}

	@RequestMapping(value = "/ativar", method = RequestMethod.POST)
	public String ativarEvento(@Valid Evento evento, BindingResult result, Model model, RedirectAttributes redirect) {
		Evento eventoBd = eventoService.buscarEventoPorId(evento.getId());

		eventoBd.setPrazoRevisaoFinal(evento.getPrazoRevisaoFinal());
		eventoBd.setPrazoRevisaoInicial(evento.getPrazoRevisaoInicial());
		eventoBd.setPrazoSubmissaoFinal(evento.getPrazoSubmissaoFinal());
		eventoBd.setPrazoSubmissaoInicial(evento.getPrazoSubmissaoInicial());
		eventoBd.setVisibilidade(evento.getVisibilidade());
		eventoBd.setDescricao(evento.getDescricao());
		eventoBd.setNome(evento.getNome());

		return ativarOuEditarEvento(eventoBd, result, model, redirect, "redirect:/eventoOrganizador/ativos",
				Constants.TEMPLATE_ATIVAR_EVENTO_ORG);
	}

	@RequestMapping(value = "/trilhas/{id}", method = RequestMethod.GET)
	public String listaTrilhas(@PathVariable String id, Model model, RedirectAttributes redirect) {
		try {
			Long eventoId = Long.valueOf(id);
			model.addAttribute("trilha", new Trilha());
			model.addAttribute("evento", eventoService.buscarEventoPorId(eventoId));
			return Constants.TEMPLATE_LISTAR_TRILHAS_ORG;
		} catch (NumberFormatException e) {
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
			return "redirect:/eventoOrganizador/evento" + id;
		}
	}

	@RequestMapping(value = "/trilha/{idTrilha}/{idEvento}", method = RequestMethod.GET)
	public String detalhesTrilha(@PathVariable String idTrilha, @PathVariable String idEvento, Model model,
			RedirectAttributes redirect) {
		Pessoa pessoaLogado = PessoaLogadaUtil.pessoaLogada();
		try {
			Long trilhaId = Long.valueOf(idTrilha);
			Long eventoId = Long.valueOf(idEvento);
			Trilha trilha = trilhaService.get(trilhaId, eventoId);
			if (participacaoEventoService.isOrganizadorDoEvento(pessoaLogado, eventoId)) {
				model.addAttribute("trilha", trilha);
				model.addAttribute("trabalhos", trabalhoService.getTrabalhosTrilha(trilha));
				return Constants.TEMPLATE_DETALHES_TRILHA_ORG;
			} else {
				return Constants.TEMPLATE_ORGANIZADOR_SEM_PERMISSAO;
			}
		} catch (NumberFormatException e) {
			redirect.addFlashAttribute("erro", messageService.getMessage("EVENTO_NAO_EXISTE"));
		}
		return Constants.TEMPLATE_LISTAR_TRILHAS_ORG;
	}

	@RequestMapping(value = "/convidar/{id}", method = RequestMethod.GET)
	public String convidarPessoasPorEmail(@PathVariable String id, Model model, RedirectAttributes redirect) {
		Long eventoId = Long.parseLong(id);
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		Pessoa professorLogado = PessoaLogadaUtil.pessoaLogada();

		if (EstadoEvento.ATIVO.equals(evento.getEstado())
				&& PapelLdap.Tipo.DOCENTE.equals(professorLogado.getPapelLdap())) {
			model.addAttribute("eventoId", eventoId);
			return Constants.TEMPLATE_CONVIDAR_PESSOAS_EMAIL_ORG;

		} else {
			redirect.addFlashAttribute(ORGANIZADOR_ERROR, messageService.getMessage(CONVIDAR_EVENTO_INATIVO));
			return "redirect:/eventoOrganizador/evento/" + eventoId;
		}
	}

	@RequestMapping(value = "/convidar", method = RequestMethod.POST)
	public String convidarPorEmail(@RequestParam("email") String email, @RequestParam("funcao") String funcao,
			@RequestParam("eventoId") Long eventoId, Model model, RedirectAttributes redirect,
			HttpServletRequest request) {

		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		String[] emails = email.split(",");

		if (EstadoEvento.ATIVO.equals(evento.getEstado())) {

			boolean flag = false;

			switch (funcao) {
			case "ORGANIZADOR":
				for (String e : emails) {
					e = e.trim();
					flag = eventoService.adicionarOrganizador(e, evento, url);
				}

				break;

			case "AUTOR":
				flag = eventoService.adicionarAutor(email, evento, url);
				break;

			case "REVISOR":
				for (String e : emails) {
					e = e.trim();
					flag = eventoService.adicionarRevisor(e, evento, url);
				}
				break;

			default:
				break;
			}

			if (!flag) {
				redirect.addFlashAttribute(ORGANIZADOR_ERROR, messageService.getMessage(ERRO_ENVIO_EMAIL));
			} else {
				redirect.addFlashAttribute(ORGANIZADOR_SUCESS, messageService.getMessage(EMAIL_ENVIADO_SUCESSO));
			}
		} else {
			redirect.addFlashAttribute(ORGANIZADOR_ERROR, messageService.getMessage(CONVIDAR_EVENTO_INATIVO));
		}
		return "redirect:/eventoOrganizador/evento/" + eventoId;
	}

	@RequestMapping(value = "/trilhas", method = RequestMethod.POST)
	public String cadastraTrilha(@RequestParam(required = false) String eventoId, @Valid Trilha trilha, Model model,
			RedirectAttributes redirect) {
		long id = Long.parseLong(eventoId);
		if (trilhaService.exists(trilha.getNome(), id)) {
			redirect.addFlashAttribute(ORGANIZADOR_ERROR, messageService.getMessage("TRILHA_NOME_JA_EXISTE"));
			return "redirect:/eventoOrganizador/trilhas/" + eventoId;
		}
		if (eventoService.existeEvento(id)) {
			trilha.setEvento(eventoService.buscarEventoPorId(id));
			trilhaService.adicionarOuAtualizarTrilha(trilha);
			redirect.addFlashAttribute("trilhaAdd", messageService.getMessage("TRILHA_ADD_COM_SUCESSO"));
			return "redirect:/eventoOrganizador/trilhas/" + eventoId;
		} else {
			redirect.addFlashAttribute(ORGANIZADOR_ERROR, messageService.getMessage("EVENTO_NAO_ENCONTRADO"));
			return "redirect:/eventoOrganizador/trilhas/" + eventoId;
		}
	}

	@RequestMapping(value = "/trilha/editar", method = RequestMethod.POST)
	public String atualizarTrilha(@RequestParam(required = false) String eventoId, @Valid Trilha trilha, Model model,
			BindingResult result, RedirectAttributes redirect) {
		long idEvento = Long.parseLong(eventoId);
		model.addAttribute("trilha", trilhaService.get(trilha.getId(), idEvento));
		if (trilha.getNome().isEmpty()) {
			model.addAttribute(ORGANIZADOR_ERROR, messageService.getMessage("TRILHA_NOME_VAZIO"));
		} else {
			if (eventoService.existeEvento(idEvento)) {
				if (trilhaService.exists(trilha.getNome(), idEvento)) {
					model.addAttribute(ORGANIZADOR_ERROR, messageService.getMessage("TRILHA_NOME_JA_EXISTE"));
				} else if (trilhaService.existeTrabalho(trilha.getId())) {
					model.addAttribute(ORGANIZADOR_ERROR, messageService.getMessage("TRILHA_POSSUI_TRABALHO"));
				} else {
					trilha.setEvento(eventoService.buscarEventoPorId(idEvento));
					trilhaService.adicionarOuAtualizarTrilha(trilha);
					model.addAttribute("trilha", trilhaService.get(trilha.getId(), idEvento));
				}
			} else {
				model.addAttribute(ORGANIZADOR_ERROR, messageService.getMessage("EVENTO_NAO_EXISTE"));
			}
		}
		return Constants.TEMPLATE_DETALHES_TRILHA_ORG;
	}

	@RequestMapping(value = "/trilha/excluir/{trilhaId}/{eventoId}", method = RequestMethod.GET)
	public String excluirTrilha(@PathVariable String trilhaId, @PathVariable String eventoId, Model model,
			RedirectAttributes redirect) {
		Long idEvento = Long.valueOf(eventoId);
		Long idTrilha = Long.valueOf(trilhaId);
		if (eventoService.existeEvento(idEvento) && !trilhaService.existeTrabalho(idTrilha)) {
			Trilha trilha = trilhaService.get(idTrilha, idEvento);
			trilhaService.removerTrilha(trilha);
			return "redirect:/eventoOrganizador/trilhas/" + eventoId;
		}
		redirect.addFlashAttribute(ORGANIZADOR_ERROR, messageService.getMessage("EVENTO_VAZIO_OU_TEM_TRABALHO"));
		return "redirect:/eventoOrganizador/trilhas/" + eventoId;
	}

	@RequestMapping(value = "/addOrganizadores", method = RequestMethod.POST)
	public String addOrganizadores(@RequestParam(required = false) String idOrganizadores,
			@RequestParam String idEvento, Model model) {
		if (idOrganizadores != null) {
			String[] ids = idOrganizadores.split(Pattern.quote(","));
			List<Pessoa> organizadores = new ArrayList<>();

			for (String id : ids) {
				organizadores.add(pessoaService.get(Long.parseLong(id)));
			}
			Evento evento = eventoService.buscarEventoPorId(Long.parseLong(idEvento));

			for (Pessoa p : organizadores) {
				ParticipacaoEvento participacao = new ParticipacaoEvento();
				participacao.setEvento(evento);
				participacao.setPessoa(p);
				participacao.setPapel(Tipo.ORGANIZADOR);
				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacao);
			}
			return "redirect:/eventoOrganizador/evento/" + idEvento;
		}
		return "redirect:/eventoOrganizador/evento/" + idEvento;
	}

	@PreAuthorize("isOrganizadorInEvento(#idEvento)")
	@RequestMapping(value = "/gerarCertificadosOrganizador/{idEvento}", method = RequestMethod.GET)
	public String gerarCertificadoOrganizador(@PathVariable("idEvento") String idEvento, Model model,
			HttpServletResponse response) {
		Long id = Long.parseLong(idEvento);
		List<Pessoa> listaOrganizadores = pessoaService.getOrganizadoresEvento(id);
		model.addAttribute("organizadores", listaOrganizadores);

		return Constants.TEMPLATE_GERAR_CERTIFICADOS_ORGANIZADORES;
	}

	@RequestMapping(value = "/gerarCertificadosOrganizadores", method = RequestMethod.POST)
	public String gerarCertificadoOrganizador(Long[] organizadoresIds, Model model, HttpServletResponse response) 
			throws FileNotFoundException, IOException {

		criarDadosODS(organizadoresIds, model, "Organizadores", response);

		return "DADOS_ORGANIZADOR";
	}

	@PreAuthorize("isOrganizadorInEvento(#idEvento)")
	@RequestMapping(value = "/gerarCertificadosRevisores/{idEvento}", method = RequestMethod.GET)
	public String gerarCertificadoRevisores(@PathVariable("idEvento") String idEvento, Model model) {
		Long id = Long.parseLong(idEvento);
		List<Pessoa> listaRevisores = pessoaService.getRevisoresEvento(id);
		model.addAttribute("revisores", listaRevisores);

		return Constants.TEMPLATE_GERAR_CERTIFICADOS_REVISORES;
	}

	@RequestMapping(value = "/gerarCertificadosRevisores", method = RequestMethod.POST)
	public String gerarCertificadoRevisores(Long[] revisoresIds, Model model, HttpServletResponse response) 
			throws FileNotFoundException, IOException {

		criarDadosODS(revisoresIds, model, "Revisores", response);

		return "DADOS_REVISORES";
	}

	public void criarDadosODS(Long[] ids, Model model, String nomeDocumento, HttpServletResponse response)
			throws FileNotFoundException, IOException {
		if (ids != null) {
			List<Pessoa> pessoas = new ArrayList<>();
			for (Long id : ids) {
				Pessoa p = pessoaService.get(id);
				p.setNome(p.getNome().toUpperCase());
				pessoas.add(p);
			}

			if (pessoas != null) {
				final Object[][] dados = new Object[pessoas.size()][1];
				for (int i = 0; i < pessoas.size(); i++) {
					dados[i] = new Object[] { pessoas.get(i).getNome().toUpperCase() };
				}

				String[] colunas = new String[] { nomeDocumento };
				gerarODS(nomeDocumento, colunas, dados, response);
			}
		}
	}

	@PreAuthorize("isOrganizadorInEvento(#idEvento)")
	@RequestMapping(value = "/gerarCertificadosTrabalho/{idEvento}", method = RequestMethod.GET)
	public String gerarCertificadoTrabalhos(@PathVariable String idEvento, Model model, HttpServletResponse response) {
		Long id = Long.parseLong(idEvento);
		Evento e = eventoService.buscarEventoPorId(id);
		List<Trabalho> listaTrabalhos = trabalhoService.getTrabalhosEvento(e);
		model.addAttribute("trabalhos", listaTrabalhos);

		return Constants.TEMPLATE_GERAR_CERTIFICADOS_TRABALHO;

	}

	@RequestMapping(value = "/gerarCertificadosTrabalho", method = RequestMethod.POST)
	public String gerarCertificadoTrabalhos(@RequestParam Long[] trabalhosIds, Model model,
			HttpServletResponse response) throws FileNotFoundException, IOException {

		if (trabalhosIds != null) {
			List<Trabalho> trabalhos = new ArrayList<>();
			for (Long id : trabalhosIds) {
				Trabalho t = trabalhoService.getTrabalhoById(id);
				t.setTitulo(t.getTitulo().toUpperCase());
				trabalhos.add(trabalhoService.getTrabalhoById(id));
			}
			if (!trabalhos.isEmpty()) {
				final Object[][] dados = new Object[trabalhos.size()][4];
				for (int i = 0; i < trabalhos.size(); i++) {
					Trabalho t = trabalhos.get(i);

					SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy");
					String data = formatadorData.format(t.getEvento().getPrazoSubmissaoFinal());

					dados[i] = new Object[] { t.getAutor().getNome().toUpperCase(),
							t.getCoautoresInString().toUpperCase(), t.getTitulo().toUpperCase(),
							t.getTrilha().getNome().toUpperCase(), data };
				}
				String[] colunas = new String[] { "Nome", "Coautores", "Título", "Trilha", "Data" };
				gerarODS("trabalhos", colunas, dados, response);
			}
		}
		return "DADOS_TRABALHOS";
	}

	public Pessoa getUsuarioLogado() {
		return PessoaLogadaUtil.pessoaLogada();
	}

	public void gerarODS(String nomeDocumento, String[] colunas, Object[][] dados, HttpServletResponse response) 
			throws FileNotFoundException, IOException {
		TableModel modelo = new DefaultTableModel(dados, colunas);
		final File file = new File(nomeDocumento + ".ods");
		SpreadSheet.createEmpty(modelo).saveAs(file);

		response.setContentType("application/ods");
		response.setHeader("Content-Disposition", "attachment; filename = " + nomeDocumento + ".ods");
		InputStream is = new FileInputStream(file);
		IOUtils.copy(is, response.getOutputStream());
		response.flushBuffer();
	}

	@PreAuthorize("isOrganizador")
	@RequestMapping(value = "/")
	public String paginaOrganizador(Model model) {
		String cpf = SecurityContextHolder.getContext().getAuthentication().getName();
		Pessoa pessoaAux = pessoaService.getByCpf(cpf);
		model.addAttribute("pessoa", pessoaAux);
		return "organizador/organizador_meus_eventos";
	}

	@PreAuthorize("isOrganizadorInEvento(#idEvento)")
	@RequestMapping(value = "/avaliar/", method = RequestMethod.POST)
	public String avaliarTrabalhoModerado(@RequestParam Long idEvento, @RequestParam String avaliacao,
			@RequestParam Long idTrabalho, Model model) {
		Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
		trabalho.setStatus(Avaliacao.valueOf(avaliacao));
		trabalhoService.adicionarTrabalho(trabalho);
		List<Trabalho> trabalhos = trabalhoService.getTrabalhosEvento(eventoService.buscarEventoPorId(idEvento));

		Evento evento = eventoService.buscarEventoPorId(idEvento);
		if (evento == null) {
			return "redirect:/error";
		}

		model.addAttribute("evento", evento);
		model.addAttribute("opcoesFiltro", Avaliacao.values());
		model.addAttribute("trabalhos", trabalhos);

		return verTrabalhosDoEvento(idEvento, model);

	}
}