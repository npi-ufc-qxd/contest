package ufc.quixada.npi.contest.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
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
	private TrabalhoService trabalhoService;

	@Autowired
	private RevisaoService revisaoService;

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
	private static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	private static final String CRITERIOS_REVISAO_VAZIO = "CRITERIOS_REVISAO_VAZIO";
	private static final String TRABALHO_NAO_EXISTE = "TRABALHO_NAO_EXISTE";
	private static final String TRABALHO_REVISADO = "TRABALHO_REVISADO";

	private static Long ID_EVENTO;
	private static Long ID_TRABALHO;

	@RequestMapping
	public String index(Model model) {
		Pessoa revisor = getRevisorLogado();
		model.addAttribute("eventos", eventoService.eventosParaParticipar(revisor.getId()));
		return REVISOR_INDEX;
	}

	@RequestMapping(value = "/meusEventos", method = RequestMethod.GET)
	public String meusEventos(Model model) {
		Pessoa revisor = getRevisorLogado();
		model.addAttribute("eventos", eventoService.buscarEventosParticapacaoRevisor(revisor.getId()));
		return REVISOR_MEUS_EVENTOS;
	}

	@RequestMapping(value = "/{idEvento}/trabalhosRevisao")
	public String trabalhosRevisao(Model model, @PathVariable("idEvento") Long idEvento, RedirectAttributes redirect) {
		Evento evento = eventoService.buscarEventoPorId(idEvento);
		
		Pessoa revisor = getRevisorLogado();
		model.addAttribute("trabalhos", trabalhoService.getTrabalhosParaRevisar(revisor.getId(), idEvento));
		model.addAttribute("evento", evento);
		
		RevisorController.ID_EVENTO = idEvento;
		return REVISOR_TRABALHOS_REVISAO;
	}

	@RequestMapping(value = "/{idEvento}/{idTrabalho}/revisar", method = RequestMethod.GET)
	public String revisarTrabalho(Model model, @PathVariable("idTrabalho") Long idTrabalho,
			@PathVariable("idEvento") Long idEvento, RedirectAttributes redirect) {
		Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
		Evento evento = eventoService.buscarEventoPorId(idEvento);
		
		model.addAttribute("nomeEvento", evento.getNome());
		model.addAttribute("trabalho", trabalho);
		model.addAttribute("autores", getAutoresDoTrabalho(trabalho));
		model.addAttribute("coAutores", getCoAutoresDoTrabalho(trabalho));
		
		RevisorController.ID_EVENTO = idEvento;
		RevisorController.ID_TRABALHO = idTrabalho;
		return REVISOR_AVALIAR_TRABALHO;
	}

	@RequestMapping(value = "/avaliar", method = RequestMethod.POST)
	public String avaliarTrabalho(@RequestParam(value = "idTrabalho") String idTrabalho,
			@RequestParam(value = "formatacao", required = false) String formatacao,
			@RequestParam(value = "originalidade", required = false) String originalidade,
			@RequestParam(value = "merito", required = false) String merito,
			@RequestParam(value = "clareza", required = false) String clareza,
			@RequestParam(value = "qualidade", required = false) String qualidade,
			@RequestParam(value = "relevancia", required = false) String relevancia,
			@RequestParam(value = "auto-avaliacao", required = false) String auto_avaliacao,
			@RequestParam(value = "avaliacao-geral", required = false) String avaliacao_geral,
			@RequestParam(value = "comentarios", required = false) String comentarios,
			@RequestParam(value = "indicar", required = false) String indicar, RedirectAttributes redirect) {

		Trabalho trabalho = trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho));

		CriteriosRevisaoValidator criterios = new CriteriosRevisaoValidator();
		boolean validacao = criterios.validate(originalidade, merito, clareza, qualidade, relevancia, auto_avaliacao,
				avaliacao_geral, comentarios);

		if (!validacao) {
			redirect.addFlashAttribute("criterioRevisaoVazioError", messageService.getMessage(CRITERIOS_REVISAO_VAZIO));
			return "redirect:/revisor/" + RevisorController.ID_EVENTO + "/" + RevisorController.ID_TRABALHO + "/revisar";
		}

		RevisaoJSON revisaoJson = new RevisaoJSON();
		String conteudo = revisaoJson.toJson(formatacao, originalidade, merito, clareza, qualidade, relevancia,
				auto_avaliacao, avaliacao_geral, comentarios, indicar);

		Revisao revisao = new Revisao();
		revisao.setConteudo(conteudo);
		revisao.setRevisor(getRevisorLogado());
		revisao.setTrabalho(trabalho);

		revisaoService.addOrUpdate(revisao);
			
		redirect.addFlashAttribute("trabalhoRevisado", messageService.getMessage(TRABALHO_REVISADO));
		return "redirect:/revisor/" + RevisorController.ID_EVENTO + "/trabalhosRevisao";
	}

	@RequestMapping(value = "/trabalho/{trabalhoID}", method = RequestMethod.GET)
	public String validaTrabalho(@PathVariable("trabalhoID") String idTrabalho, 
			HttpServletResponse response,
			RedirectAttributes redirect) throws IOException {

		if (trabalhoService.existeTrabalho(Long.valueOf(idTrabalho))) {
			Trabalho trabalho = trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho));
			baixarTrabalho(response, trabalho);
			return "redirect:/revisor/" + RevisorController.ID_EVENTO + "/" + idTrabalho + "/revisar";
		}

		redirect.addFlashAttribute("trabalhoNaoExisteError", messageService.getMessage(TRABALHO_NAO_EXISTE));
		return "redirect:/revisor/" + RevisorController.ID_EVENTO + "/" + idTrabalho + "/revisar";	
	}

	@ResponseBody
	private void baixarTrabalho(HttpServletResponse response, Trabalho trabalho) throws IOException {
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

		Pessoa professorLogado = getRevisorLogado();
		Evento evento = eventoService.buscarEventoPorId(Long.parseLong(idEvento));

		if (evento != null) {
			if (evento.getEstado() == EstadoEvento.ATIVO) {
				ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
				participacaoEvento.setEvento(evento);
				participacaoEvento.setPessoa(professorLogado);
				participacaoEvento.setPapel(Papel.REVISOR);

				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento);
				redirect.addFlashAttribute(PARTICAPACAO_EVENTO_SUCESSO,
						messageService.getMessage(PARTICAPAR_EVENTO_SUCESSO));
			} else {
				redirect.addFlashAttribute(PARTICIPAR_EVENTO_INATIVO_ERROR,
						messageService.getMessage(PARTICIPAR_EVENTO_INATIVO));
			}
		} else {
			redirect.addFlashAttribute(EVENTO_NAO_EXISTE, messageService.getMessage(EVENTO_NAO_EXISTE));
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

	public List<Pessoa> getAutoresDoTrabalho(Trabalho trabalho) {
		List<Pessoa> autores = new Vector<Pessoa>();
		for (ParticipacaoTrabalho p : trabalho.getParticipacoes()) {
			if (p.getPapel() == Papel.AUTOR)
				autores.add(p.getPessoa());
		}

		return autores;
	}

	public List<Pessoa> getCoAutoresDoTrabalho(Trabalho trabalho) {
		List<Pessoa> coAutores = new Vector<Pessoa>();
		for (ParticipacaoTrabalho p : trabalho.getParticipacoes()) {
			if (p.getPapel() == Papel.COAUTOR)
				coAutores.add(p.getPessoa());
		}

		return coAutores;
	}

}