package ufc.quixada.npi.contest.controller;

import java.util.ArrayList;
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
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping("/evento")
public class EventoController extends EventoGenericoController{

	private static final String EVENTO_INATIVO_EXCLUIDO_ERRO = "EVENTO_INATIVO_EXCLUIDO_ERRO";
	private static final String ERRO_EXCLUIR = "erroExcluir";
	private static final String EVENTO_INATIVO_EXCLUIDO_SUCESSO = "EVENTO_INATIVO_EXCLUIDO_SUCESSO";
	private static final String SUCESSO_EXCLUIR = "sucessoExcluir";
	private static final String PESSOA_NAO_ENCONTRADA = "PESSOA_NAO_ENCONTRADA";
	private static final String EVENTO_CADASTRADO_COM_SUCESSO = "EVENTO_CADASTRADO_COM_SUCESSO";
	private static final String EVENTO_EDITADO_COM_SUCESSO = "EVENTO_EDITADO_COM_SUCESSO";
	private static final String SUCESSO_CADASTRAR = "sucessoCadastrar";
	private static final String SUCESSO_EDITAR = "sucessoEditar";
	private static final String ORGANIZADOR_VAZIO_ERROR = "ORGANIZADOR_VAZIO_ERROR";
	private static final String ORGANIZADOR_ERROR = "organizadorError";
	private static final String EVENTO = "evento";
	private static final String EVENTOS_INATIVOS = "eventosInativos";
	private static final String EVENTOS_ATIVOS = "eventosAtivos";

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
		List<Evento> listaEventos = eventoService.buscarEventoPorEstado(EstadoEvento.ATIVO);
		model.addAttribute(EVENTOS_ATIVOS, listaEventos);
		
		return Constants.TEMPLATE_LISTAR_EVENTOS_ATIVOS_ADMIN;
	}

	@RequestMapping(value = "/inativos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		List<Evento> listaEventos = eventoService.buscarEventoPorEstado(EstadoEvento.INATIVO);
		model.addAttribute(EVENTOS_INATIVOS, listaEventos);
		return Constants.TEMPLATE_LISTAR_EVENTOS_INATIVOS_ADMIN;
	}

	@RequestMapping(value = "/adicionar", method = RequestMethod.GET)
	public String adicionarEvento(Model model) {
		model.addAttribute(EVENTO, new Evento());
		return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ADMIN;
	}	

	@RequestMapping(value = "/adicionar", method = RequestMethod.POST)
	public String adicionarEvento(@RequestParam(required = false) String email, @Valid Evento evento,
			BindingResult result, RedirectAttributes redirect) {

		if (email == null || email.isEmpty()) {
			result.reject(ORGANIZADOR_ERROR, messageService.getMessage(ORGANIZADOR_VAZIO_ERROR));
		}

		if (result.hasErrors()) {
			return Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ADMIN;
		}

		Pessoa pessoa = pessoaService.getByEmail(email);
		
		if (pessoa != null) {
			//ParticipacaoEvento participacao = new ParticipacaoEvento();
			boolean flag = eventoService.adicionarOrganizador(email, evento, pessoa.getNome());
			if(evento.getId() != null){
				//verificar caso a pessoa seja inserida novamente
				if(flag){
				//participacao = participacaoEventoService.findByEventoId(evento.getId());
					redirect.addFlashAttribute(SUCESSO_EDITAR, messageService.getMessage(EVENTO_EDITADO_COM_SUCESSO));
				//addEventoEmParticipacao(evento, participacao, pessoa);
				}else{
					//TODO adicionar a mensagem de erro coerente
				}
			}else{
				
				redirect.addFlashAttribute(SUCESSO_CADASTRAR, messageService.getMessage(EVENTO_CADASTRADO_COM_SUCESSO));
			}
			
			
			//addEventoEmParticipacao(evento, participacao, pessoa);
		} else {
			eventoService.adicionarOrganizador(email, evento, pessoa.getNome());
			//result.reject(ORGANIZADOR_ERROR, messageService.getMessage(PESSOA_NAO_ENCONTRADA));
		}
		
		eventoService.adicionarOuAtualizarEvento(evento);
		
		List<Trilha> trilhas = new ArrayList<>();
		
        		Trilha trilha = new Trilha();
        		trilha.setEvento(evento);
		trilha.setNome("Principal");
		trilhas.add(trilha);
        
		evento.setTrilhas(trilhas);
		
		return "redirect:/evento/inativos";
	}
	
	@RequestMapping(value = "/editar/{id}", method = RequestMethod.GET)
	public String alterarEventoAdmin(@PathVariable String id, Model model, RedirectAttributes redirect){
		return alterarEvento(id, model, redirect, Constants.TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ADMIN, "redirect:/evento/inativos");
	}

	@RequestMapping(value = "/remover", method = RequestMethod.POST)
	public String removerEvento(@RequestParam("idEvento") String id, RedirectAttributes redirect) {
		try {
			Long idEvento = Long.valueOf(id);
			Evento evento = eventoService.buscarEventoPorId(idEvento);
			if (evento != null) {
				participacaoEventoService.removerParticipacaoEvento(evento);

				redirect.addFlashAttribute(SUCESSO_EXCLUIR, messageService.getMessage(EVENTO_INATIVO_EXCLUIDO_SUCESSO));
			} else {
				redirect.addFlashAttribute(ERRO_EXCLUIR, messageService.getMessage(EVENTO_INATIVO_EXCLUIDO_ERRO));
			}
		} catch (NumberFormatException e) {
			redirect.addFlashAttribute(ERRO_EXCLUIR, messageService.getMessage(EVENTO_INATIVO_EXCLUIDO_ERRO));
		}

		return "redirect:/evento/inativos";
	}
	
	public void addEventoEmParticipacao(Evento evento, ParticipacaoEvento participacao, Pessoa pessoa){
        evento.setEstado(EstadoEvento.INATIVO);
		evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		participacao.setEvento(evento);
		participacao.setPessoa(pessoa);
		participacao.setPapel(Tipo.ORGANIZADOR);
		participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacao);
	}
	
}
