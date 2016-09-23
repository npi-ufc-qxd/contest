package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.util.ArrayList;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.gl.E;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Entao;
import cucumber.api.java.pt.Quando;
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
		this.mockMvc = MockMvcBuilders.standaloneSetup(revisorController).build();
		participacaoEvento = new ParticipacaoEvento();
	}

	/**
	 * Cenário 1
	 */
	@Dado("^Estou logado no sistema como professor$")
	public void logadoComoProfessor() {
		revisorLogado = new Pessoa();
		revisorLogado.setPapelLdap("DOCENTE");
		revisorLogado.setId(Long.valueOf(5));
		
		pessoaService.getClass();
		when(revisorController.getRevisorLogado()).thenReturn(revisorLogado);
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
		
		action = mockMvc.perform(post("/revisor/participarevento")
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
		action.andExpect(redirectedUrl("/revisor"))
		.andExpect(model().attributeDoesNotExist("eventoVazioError", "eventoInexistenteError"));
	}
	
	/**
	 * Cenário 2: Verificar os eventos listados para o professor
	 * @throws Exception 
	 */
	
	@Quando("^Realizo uma busca por eventos ativos$")
	public void buscarEventosAtivos() throws Exception{
		action = mockMvc.perform(get("/revisor")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED));
		
		verify(eventoService).eventosParaParticipar(revisorLogado.getId());
	}
	
	@Entao("^Deve ser mostrado apenas eventos públicos$")
	public void mostrarEventosPublicos(){
		verify(eventoService).eventosParaParticipar(revisorLogado.getId());
	}
	
	/**
	 * Cenário 3: Verificar os eventos listados para o professor
	 * @throws Exception 
	 */
	
	
}