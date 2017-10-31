package ufc.quixada.npi.contest.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.Avaliacao;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;
import ufc.quixada.npi.contest.util.RevisaoJSON;
import ufc.quixada.npi.contest.validator.CriteriosRevisaoValidator;

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
	private ParticipacaoTrabalhoService participacaoTrabalhoService;

	@Autowired
	private TrabalhoService trabalhoService;

	@Autowired
	private RevisaoService revisaoService;

	private static final String REVISOR_TRABALHOS_REVISAO = "revisor/revisor_trabalhos";
	private static final String REVISOR_AVALIAR_TRABALHO = "revisor/revisor_avaliar_trabalho";
	private static final String REVISOR_SEM_PERMISSAO = "revisor/erro_permissao_de_revisor";
	private static final String TRABALHO_REVISAO_PELO_REVISOR = "revisor/erro_trabalho_ja_revisado";

	private static final String EVENTO_VAZIO_ERROR = "eventoVazioError";
	private static final String ID_EVENTO_VAZIO_ERROR = "ID_EVENTO_VAZIO_ERROR";
	private static final String PARTICAPACAO_EVENTO_SUCESSO = "particapacaoEventoSucesso";
	private static final String PARTICIPAR_EVENTO_INATIVO_ERROR = "participarEventoInativoError";
	private static final String PARTICAPAR_EVENTO_SUCESSO = "PARTICAPAR_EVENTO_SUCESSO";
	private static final String PARTICIPAR_EVENTO_INATIVO = "PARTICIPAR_EVENTO_INATIVO";
	private static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	private static final String CRITERIOS_REVISAO_VAZIO = "CRITERIOS_REVISAO_VAZIO";
	private static final String TRABALHO_NAO_EXISTE = "TRABALHO_NAO_EXISTE";
	private static final String TRABALHO_REVISADO = "TRABALHO_REVISADO";
	private static final String FORA_PERIODO_REVISAO = "FORA_PERIODO_REVISAO";

	//@PreAuthorize("isRevisorInEvento(#eventoId)")
	@RequestMapping(value = "/{idEvento}/trabalhosRevisao")
	public String trabalhosRevisao(Model model, @PathVariable("idEvento") Long idEvento, RedirectAttributes redirect) {
		Evento evento = eventoService.buscarEventoPorId(idEvento);
		Pessoa p = PessoaLogadaUtil.pessoaLogada();
		List<ParticipacaoEvento> participacoesComoRevisor = participacaoEventoService
				.getEventosDoRevisor(EstadoEvento.ATIVO, p.getId());
		List<Long> eventosComoRevisor = new ArrayList<>();
		for (ParticipacaoEvento participacaoEvento : participacoesComoRevisor) {
			eventosComoRevisor.add(participacaoEvento.getEvento().getId());
		}

		if (!evento.isPeriodoRevisao()) {
			redirect.addFlashAttribute("periodoRevisaoError", messageService.getMessage(FORA_PERIODO_REVISAO));
			return "redirect:/eventoOrganizador";
		}

		Pessoa revisor = PessoaLogadaUtil.pessoaLogada();
		model.addAttribute("trabalhos", trabalhoService.getTrabalhosParaRevisar(revisor.getId(), idEvento));
		model.addAttribute("trabalhosRevisados",
				trabalhoService.getTrabalhosRevisadosDoRevisor(revisor.getId(), idEvento));

		model.addAttribute("evento", evento);

		return REVISOR_TRABALHOS_REVISAO;
	}

	//@PreAuthorize("isRevisorInTrabalho(#idTrabalho)")
	@RequestMapping(value = "/{idTrabalho}/revisar", method = RequestMethod.GET)
	public String revisarTrabalho(HttpSession session, Model model, @PathVariable("idTrabalho") Long idTrabalho,
			RedirectAttributes redirect) {

		Trabalho trabalho = trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho));
		Evento evento;
		Pessoa revisor = PessoaLogadaUtil.pessoaLogada();

		if (trabalho != null) {
			evento = trabalho.getEvento();
			if (!evento.isPeriodoRevisao()) {
				redirect.addFlashAttribute("periodoRevisaoError", messageService.getMessage(FORA_PERIODO_REVISAO));
				return "redirect:/eventoOrganizador";
			} else if (revisaoService.isTrabalhoRevisadoPeloRevisor(trabalho.getId(), revisor.getId())) {
				return TRABALHO_REVISAO_PELO_REVISOR;
			}
		} else {
			return "redirect:/error";
		}

		if (participacaoTrabalhoService.getParticipacaoTrabalhoRevisor(revisor.getId(), trabalho.getId()) != null) {

			model.addAttribute("nomeEvento", evento.getNome());
			model.addAttribute("idEvento", evento.getId());
			model.addAttribute("trabalho", trabalho);

			session.setAttribute("ID_EVENTO_REVISOR", Long.valueOf(evento.getId()));
			session.setAttribute("ID_TRABALHO_REVISOR", Long.valueOf(idTrabalho));
			return REVISOR_AVALIAR_TRABALHO;
		}
		return REVISOR_SEM_PERMISSAO;

	}

	@RequestMapping(value = "/avaliar", method = RequestMethod.POST)
	public String avaliarTrabalho(@RequestParam(value = "idTrabalho") String idTrabalho,
			@RequestParam(value = "idEvento") String idEvento,
			@RequestParam(value = "formatacao", required = false) String formatacao,
			@RequestParam(value = "originalidade", required = false) String originalidade,
			@RequestParam(value = "merito", required = false) String merito,
			@RequestParam(value = "clareza", required = false) String clareza,
			@RequestParam(value = "qualidade", required = false) String qualidade,
			@RequestParam(value = "relevancia", required = false) String relevancia,
			@RequestParam(value = "auto-avaliacao", required = false) String auto_avaliacao,
			@RequestParam(value = "comentarios_autores", required = false) String comentarios_autores,
			@RequestParam(value = "comentarios_organizacao", required = false) String comentarios_organizacao,
			@RequestParam(value = "avaliacao-geral", required = false) String avaliacao_geral,
			@RequestParam(value = "avaliacao-final", required = false) String avaliacao_final,
			@RequestParam(value = "indicar", required = false) String indicar, RedirectAttributes redirect,
			HttpSession session) {

		Trabalho trabalho = trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho));
		Pessoa revisor = PessoaLogadaUtil.pessoaLogada();

		if (trabalho == null) {
			return "redirect:/error";
		} else if (trabalho.getEvento() == null) {
			return "redirect:/error";
		} else if (!trabalho.getEvento().isPeriodoRevisao()) {
			redirect.addFlashAttribute("periodoRevisaoError", messageService.getMessage(FORA_PERIODO_REVISAO));
			return "redirect:/eventoOrganizador";
		} else if (revisaoService.isTrabalhoRevisadoPeloRevisor(trabalho.getId(), revisor.getId())) {
			return TRABALHO_REVISAO_PELO_REVISOR;
		}

		if (participacaoTrabalhoService.getParticipacaoTrabalhoRevisor(revisor.getId(), trabalho.getId()) != null) {

			session.setAttribute("ID_EVENTO_REVISOR", Long.valueOf(idEvento));
			session.setAttribute("ID_TRABALHO_REVISOR", Long.valueOf(idTrabalho));

			CriteriosRevisaoValidator criterios = new CriteriosRevisaoValidator();
			boolean validacao = criterios.validate(originalidade, merito, clareza, qualidade, relevancia,
					auto_avaliacao, comentarios_autores, avaliacao_geral, avaliacao_final);

			if (!validacao) {
				redirect.addFlashAttribute("criterioRevisaoVazioError",
						messageService.getMessage(CRITERIOS_REVISAO_VAZIO));
				return "redirect:/revisor/" + idTrabalho + "/revisar";
			}

			String conteudo = RevisaoJSON.toJson(formatacao, originalidade, merito, clareza, qualidade, relevancia,
					auto_avaliacao, comentarios_autores, avaliacao_geral, avaliacao_final, indicar);

			Revisao revisao = new Revisao();
			revisao.setConteudo(conteudo);
			revisao.setRevisor(revisor);
			revisao.setTrabalho(trabalho);
			revisao.setObservacoes(comentarios_organizacao);

			switch (avaliacao_final) {
			case "APROVADO":
				revisao.setAvaliacao(Avaliacao.APROVADO);
				break;
			case "RESSALVAS":
				revisao.setAvaliacao(Avaliacao.RESSALVAS);
				break;
			case "REPROVADO":
				revisao.setAvaliacao(Avaliacao.REPROVADO);
				break;
			default:
				break;
			}

			revisaoService.addOrUpdate(revisao);

			redirect.addFlashAttribute("trabalhoRevisado", messageService.getMessage(TRABALHO_REVISADO));
			return "redirect:/revisor/" + idEvento + "/trabalhosRevisao";
		}
		return REVISOR_SEM_PERMISSAO;
	}

	@PreAuthorize("isRevisorInTrabalho(#idTrabalho)")
	@RequestMapping(value = "/trabalho/{trabalhoID}", method = RequestMethod.GET)
	public String validaTrabalho(HttpSession session, @PathVariable("trabalhoID") String idTrabalho,
			HttpServletResponse response, RedirectAttributes redirect) throws IOException {

		if (trabalhoService.existeTrabalho(Long.valueOf(idTrabalho))) {
			Trabalho trabalho = trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho));
			baixarTrabalho(response, trabalho);

			session.setAttribute("ID_TRABALHO_REVISOR", idTrabalho);
			return "redirect:/revisor/" + session.getAttribute("ID_EVENTO_REVISOR") + "/" + idTrabalho + "/revisar";
		}

		redirect.addFlashAttribute("trabalhoNaoExisteError", messageService.getMessage(TRABALHO_NAO_EXISTE));
		return "redirect:/revisor/" + session.getAttribute("ID_EVENTO_REVISOR") + "/"
				+ session.getAttribute("ID_TRABALHO_REVISOR") + "/revisar";
	}

	@ResponseBody
	public void baixarTrabalho(HttpServletResponse response, Trabalho trabalho) throws IOException {
		String titulo = trabalho.getTitulo();
		titulo = titulo.replaceAll("\\s", "_");
		String src = trabalho.getPath();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename = " + titulo + ".pdf");
		InputStream is = new FileInputStream(src);
		IOUtils.copy(is, response.getOutputStream());
		response.flushBuffer();
	}

	@RequestMapping(value = "/participarevento", method = RequestMethod.POST)
	public String professorParticipa(@RequestParam String idEvento, Model model, RedirectAttributes redirect) {
		if (!eventoService.existeEvento(Long.parseLong(idEvento))) {
			redirect.addFlashAttribute(EVENTO_VAZIO_ERROR, messageService.getMessage(ID_EVENTO_VAZIO_ERROR));
			return "redirect:/revisor";
		}

		Pessoa professorLogado = PessoaLogadaUtil.pessoaLogada();
		Evento evento = eventoService.buscarEventoPorId(Long.parseLong(idEvento));

		if (evento != null) {
			if (evento.getEstado() == EstadoEvento.ATIVO) {
				ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
				participacaoEvento.setEvento(evento);
				participacaoEvento.setPessoa(professorLogado);
				participacaoEvento.setPapel(Tipo.REVISOR);

				// TEM QUE ATUALIZAR O USUARIO DA SESSÂO (getPrincipal())

				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento);
				redirect.addFlashAttribute(PARTICAPACAO_EVENTO_SUCESSO,
						messageService.getMessage(PARTICAPAR_EVENTO_SUCESSO));
			} else {
				redirect.addFlashAttribute(PARTICIPAR_EVENTO_INATIVO_ERROR,
						messageService.getMessage(PARTICIPAR_EVENTO_INATIVO));
			}
		} else {
			redirect.addFlashAttribute(EVENTO_NAO_EXISTE, messageService.getMessage(EVENTO_NAO_EXISTE));
			return "redirect:/revisor/";
		}

		return "redirect:/revisor/";
	}


	
	@RequestMapping(value = "/evento/{id}")
	public String paginaRevisor(@PathVariable Long id, Model model) {
		
		String cpf = SecurityContextHolder.getContext().getAuthentication().getName();
		Pessoa pessoaAux = pessoaService.getByCpf(cpf);		
		
		List<Evento> eventos;
		
		if(id != null) {
			eventos = new ArrayList<Evento>();
			eventos.add(eventoService.buscarEventoPorId(id));
		} else {
			eventos = eventoService.getMeusEventosAtivosComoRevisor(pessoaAux.getId());		
		}
		
		model.addAttribute("pessoa", pessoaAux);
		
		model.addAttribute("eventos", eventos);
				
		return "revisor/revisor_meus_eventos";
	}

	@RequestMapping(value = "/")
	public String paginaRevisor(Model model) {
		return paginaRevisor(null, model);
	}
	
	@RequestMapping(value = "/ativos", method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		Pessoa pessoa = PessoaLogadaUtil.pessoaLogada();
		List<Evento> eventosAtivos = eventoService.buscarEventoPorEstado(EstadoEvento.ATIVO);
		List<ParticipacaoEvento> participacoesComoRevisor = participacaoEventoService
				.getEventosDoRevisor(EstadoEvento.ATIVO, pessoa.getId());
		List<ParticipacaoEvento> participacoesComoOrganizador = participacaoEventoService
				.getEventosDoOrganizador(EstadoEvento.ATIVO, pessoa.getId());
		boolean existeEventos = true;

		if (eventosAtivos.isEmpty())
			existeEventos = false;

		List<Long> eventosComoRevisor = new ArrayList<>();
		List<Long> eventosComoOrganizador = new ArrayList<>();

		for (ParticipacaoEvento participacaoEvento : participacoesComoRevisor) {
			eventosComoRevisor.add(participacaoEvento.getEvento().getId());
		}

		for (ParticipacaoEvento participacaoEvento : participacoesComoOrganizador) {
			eventosComoOrganizador.add(participacaoEvento.getEvento().getId());
		}

		model.addAttribute("existeEventos", existeEventos);
		model.addAttribute("eventosAtivos", eventosAtivos);
		model.addAttribute("eventosComoOrganizador", eventosComoOrganizador);
		model.addAttribute("eventosComoRevisor", eventosComoRevisor);
		return Constants.TEMPLATE_LISTAR_EVENTOS_ATIVOS_REV;

	}

	
	@RequestMapping(value = "/evento/{id}/detalhes", method = RequestMethod.GET)
	public String detalhesEvento(@PathVariable String id, Model model) {
		Long eventoId = Long.parseLong(id);
		Pessoa pessoa = PessoaLogadaUtil.pessoaLogada();
		List<ParticipacaoEvento> participacoesComoRevisor = participacaoEventoService
				.getEventosDoRevisor(EstadoEvento.ATIVO, pessoa.getId());
		List<Long> eventosComoRevisor = new ArrayList<>();
		Evento evento = eventoService.buscarEventoPorId(eventoId);
		Boolean eventoPrivado = false;

		if (evento.getVisibilidade() == VisibilidadeEvento.PRIVADO) {
			eventoPrivado = true;
		}
		for (ParticipacaoEvento participacaoEvento : participacoesComoRevisor) {
			eventosComoRevisor.add(participacaoEvento.getEvento().getId());
		}
		List<Pessoa> pessoas = pessoaService.getTodos();
		boolean organizaEvento = evento.getOrganizadores().contains(pessoa);

		model.addAttribute("organizaEvento", organizaEvento);
		model.addAttribute("evento", evento);
		model.addAttribute("pessoas", pessoas);
		model.addAttribute("eventoPrivado", eventoPrivado);

		int trabalhosSubmetidos = trabalhoService.buscarQuantidadeTrabalhosPorEvento(evento);
		int trabalhosNaoRevisados = trabalhoService.buscarQuantidadeTrabalhosNaoRevisadosPorEvento(evento);
		int trabalhosRevisados = trabalhosSubmetidos - trabalhosNaoRevisados;

		List<Pessoa> organizadores = pessoaService.getOrganizadoresEvento(eventoId);

		for (Pessoa p : organizadores) {
			if (p.getId() == pessoa.getId()) {
				model.addAttribute("gerarCertificado", true);
				break;
			}
		}

		model.addAttribute("numeroTrabalhos", trabalhosSubmetidos);
		model.addAttribute("numeroTrabalhosNaoRevisados", trabalhosNaoRevisados);
		model.addAttribute("numeroTrabalhosRevisados", trabalhosRevisados);
		model.addAttribute("comentarios",
				trabalhoService.buscarQuantidadeTrabalhosRevisadosEComentadosPorEvento(evento));
		model.addAttribute("eventosComoRevisor", eventosComoRevisor);

		return Constants.TEMPLATE_DETALHES_EVENTO_REV;
	}
	
	
}