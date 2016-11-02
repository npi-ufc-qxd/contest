package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.gl.E;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Entao;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.controller.RevisorController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class ProfessorParticipaEvento {

	@InjectMocks
	private RevisorController revisorController;
	
	@InjectMocks
	private EventoControllerOrganizador eventoOrganizadorController;

	@Mock
	private EventoService eventoService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;
	
	@Mock
	private PessoaService pessoaService;

	@Mock
	private MessageService messageService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa revisorLogado;
	private String EVENTO_ID = "2";
	private ParticipacaoEvento participacaoEvento;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoOrganizadorController).build();
		participacaoEvento = new ParticipacaoEvento();
		revisorLogado = new Pessoa();
		revisorLogado.setParticipacoesEvento(new ArrayList<ParticipacaoEvento>());
	}

	/**
	 * Cenário 1
	 */
	@Dado("^Estou logado no sistema como professor$")
	public void logadoComoProfessor() {
		revisorLogado.setPapelLdap("DOCENTE");
		revisorLogado.setId(Long.valueOf(5));
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		
		SecurityContextHolder.setContext(context);
		
		when(eventoOrganizadorController.getOrganizadorLogado()).thenReturn(revisorLogado);
	}
	
	@E("^Realizo uma busca por eventos ativos no sistema$")
	public void realizadoBuscaPorEventosAtivos(){
		when(eventoService.buscarEventosAtivosEPublicos()).thenReturn(new ArrayList<Evento>());
	}
	
	@Quando("^Escolho participar de um evento ativo com id (.*)$")
	public void escolhoParticiparEventoAtivo(String id) throws Exception{
		Evento evento = new Evento();
		evento.setId(Long.valueOf(id));
		evento.setNome("Racha no Pinheiro");
		evento.setEstado(EstadoEvento.ATIVO);
		
		participacaoEvento.setEvento(evento);
		participacaoEvento.setPessoa(revisorLogado);
		participacaoEvento.setPapel(Papel.REVISOR);
		
		when(eventoService.existeEvento(Long.valueOf(id))).thenReturn(true);
		when(eventoService.buscarEventoPorId(Long.valueOf(id))).thenReturn(evento);
		when(participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento)).thenReturn(true);
		
		action = mockMvc.perform(post("/eventoOrganizador/participarevento")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idEvento", id));
	
	}
	
	@Entao("^Confirmo minha incrição$")
	public void confirmoInscricao(){
		verify(eventoService).buscarEventoPorId(Long.valueOf(EVENTO_ID));
		verify(participacaoEventoService).adicionarOuEditarParticipacaoEvento(participacaoEvento);
	}
	
	@E("^Deve ser mostrado uma mensagem de feedback$")
	public void mostrarMensagemFeedback() throws Exception{
		messageService.getClass();
		action.andExpect(redirectedUrl("/eventoOrganizador"))
		.andExpect(model().attributeDoesNotExist("eventoVazioError", "eventoInexistenteError"));
	}
	
	/**
	 * Cenário 2: Verificar os eventos listados para o professor
	 * @throws Exception 
	 */
	
	@Quando("^Realizo uma busca por eventos ativos$")
	public void buscarEventosAtivos() throws Exception{
		List<Evento> eventos = new ArrayList<>();
		when(eventoService.buscarEventoPorEstado(EstadoEvento.ATIVO)).thenReturn(eventos);
		action = mockMvc.perform(get("/eventoOrganizador/ativos")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED));
		
	}
	
	@Entao("^Deve ser mostrado apenas eventos públicos$")
	public void mostrarEventosPublicos() throws Exception{
		action.andExpect(view().name("organizador/org_eventos_listar_ativos"));
	}
	
	/**
	 * Cenário 3: Verificar os eventos listados para o professor
	 * @throws Exception 
	 */
	
	
}