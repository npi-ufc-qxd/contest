package ufc.quixada.npi.contest.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping("/evento")
public class EventoController extends EventoGenericoController{

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	@Autowired
	private EventoService eventoService;
	
	@Autowired
	private MessageService messageService;

	@ModelAttribute("pessoas")
	public List<Pessoa> listaPossiveisOrganizadores() {
		return pessoaService.getPossiveisOrganizadores();
	}

	@RequestMapping(value = {"/ativos", ""}, method = RequestMethod.GET)
	public String listarEventosAtivos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstadoAndPapelOrganizador(EstadoEvento.ATIVO);
		model.addAttribute("eventosAtivos", listaEventos);
		return Constants.TEMPLATE_LISTAR_EVENTOS_ATIVOS_ADMIN;
	}

	@RequestMapping(value = "/inativos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		List<ParticipacaoEvento> listaEventos = participacaoEventoService.getEventosByEstadoAndPapelOrganizador(EstadoEvento.INATIVO);
		model.addAttribute("eventosInativos", listaEventos);
		return Constants.TEMPLATE_LISTAR_EVENTOS_INATIVOS_ADMIN;
	}

	@RequestMapping(value = "/adicionar", method = RequestMethod.GET)
	public String adicionarEvento(Model model) {
		model.addAttribute("evento", new Evento());
		return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ADMIN;
	}

	@RequestMapping(value = "/adicionar", method = RequestMethod.POST)
	public String adicionarEvento(@RequestParam(required = false) String organizador, @Valid Evento evento,
			BindingResult result, RedirectAttributes redirect) {

		if (organizador == null || organizador.isEmpty()) {
			result.reject("organizadorError", messageService.getMessage("ORGANIZADOR_VAZIO_ERROR"));
		}

		if (result.hasErrors()) {
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ADMIN;
		}

		Pessoa pessoa = pessoaService.get(Long.valueOf(organizador));
		
		if (pessoa != null) {
			ParticipacaoEvento participacao = new ParticipacaoEvento();
			if(evento.getId() != null){
				participacao = participacaoEventoService.findByEventoId(evento.getId());
				redirect.addFlashAttribute("sucessoEditar", messageService.getMessage("EVENTO_EDITADO_COM_SUCESSO"));
			}else{
				redirect.addFlashAttribute("sucessoCadastrar", messageService.getMessage("EVENTO_CADASTRADO_COM_SUCESSO"));
			}

			evento.setEstado(EstadoEvento.INATIVO);
			evento.setVisibilidade(VisibilidadeEvento.PRIVADO);

			participacao.setEvento(evento);
			participacao.setPessoa(pessoa);
			participacao.setPapel(Papel.ORGANIZADOR);
			participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacao);
		} else {
			result.reject("organizadorError", messageService.getMessage("PESSOA_NAO_ENCONTRADA"));
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ADMIN;
		}

		return "redirect:/evento/inativos";
	}
	
	@RequestMapping(value = "/editar/{id}", method = RequestMethod.GET)
	public String alterarEventoAdmin(@PathVariable String id, Model model, RedirectAttributes redirect){
		return alterarEvento(id, model, redirect, Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ADMIN, "redirect:/evento/inativos");
	}

	@RequestMapping(value = "/remover/{id}", method = RequestMethod.GET)
	public String removerEvento(@PathVariable String id, RedirectAttributes redirect) {
		try {
			Long idEvento = Long.valueOf(id);
			Evento evento = eventoService.buscarEventoPorId(idEvento);
			if (evento != null) {
				participacaoEventoService.removerParticipacaoEvento(evento);

				redirect.addFlashAttribute("sucessoExcluir", messageService.getMessage("EVENTO_INATIVO_EXCLUIDO_SUCESSO"));
			} else {
				redirect.addFlashAttribute("erroExcluir", messageService.getMessage("EVENTO_INATIVO_EXCLUIDO_ERRO"));
			}
		} catch (NumberFormatException e) {
			redirect.addFlashAttribute("erroExcluir", messageService.getMessage("EVENTO_INATIVO_EXCLUIDO_ERRO"));
		}

		return "redirect:/evento/inativos";
	}
}