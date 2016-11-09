package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

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
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.AutorController;
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

public class AutorParticipaEvento {

	@InjectMocks
	private AutorController alunoController;

	@Mock
	private PessoaService pessoaService;

	@Mock
	private EventoService eventoService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;

	@Mock
	private MessageService messageService;

	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa pessoa;
	private Evento evento;
	private String EVENTO_ID = "2";
	private ParticipacaoEvento participacaoEvento;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(alunoController).build();
		participacaoEvento = new ParticipacaoEvento();
	}

	/**
	 * Cenário 1
	 */
	@Dado("^que existe um evento ativo e público cadastrado no sistema$")
	public void existeEventoAtivoEPublico() {
		// Nao há necessidade de realizar um teste aqui
	}

	@Quando("^o aluno decide participar de um evento com id (.*)$")
	public void casoTesteQuandoCenario1(String idEvento) throws Exception {

		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);

		evento = new Evento();
		evento.setId(Long.valueOf(idEvento));
		evento.setNome("Racha no Pinheiro");
		evento.setEstado(EstadoEvento.ATIVO);
		evento.setVisibilidade(VisibilidadeEvento.PUBLICO);

		participacaoEvento.setEvento(evento);
		participacaoEvento.setPessoa(pessoa);
		participacaoEvento.setPapel(Papel.AUTOR);

		pessoa = new Pessoa();
		pessoa.setCpf("123");
		pessoa.setId(1L);

		when(eventoService.existeEvento(Long.valueOf(idEvento))).thenReturn(true);
		when(eventoService.buscarEventoPorId(Long.valueOf(idEvento))).thenReturn(evento);
		when(participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento)).thenReturn(true);

		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		SecurityContextHolder.setContext(context);

		when(pessoaService.get(1L)).thenReturn(pessoa);

		action = mockMvc.perform(post("/autor/participarEvento").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idEvento", "2"));

	}

	@Então("^o sistema registra a participação$")
	public void casoTesteEntaoCenario1() {
		verify(eventoService).buscarEventoPorId(Long.valueOf(EVENTO_ID));
		verify(participacaoEventoService).adicionarOuEditarParticipacaoEvento(participacaoEvento);
	}

	@E("^retorna uma mensagem de sucesso para o aluno$")
	public void casoTesteECenario1() throws Exception {
		action.andExpect(redirectedUrl("/autor/enviarTrabalhoForm/"+EVENTO_ID))
				.andExpect(model().attributeDoesNotExist("eventoVazioError", "eventoInexistenteError"));
	}

	/**
	 * Cenário 2
	 *
	 */
	@Dado("^que existe um evento inativo cadastrado no sistema com id (.*)$")
	public void existeEventoInativo2(String idEvento) {
		EVENTO_ID = idEvento;
	}

	@Quando("^o aluno decide participar deste evento$")
	public void alunoDecideParticiparEvento() throws Exception {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(eventoService.existeEvento(Long.valueOf(EVENTO_ID))).thenReturn(true);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		SecurityContextHolder.setContext(context);
		
		evento = new Evento();
		evento.setId(Long.valueOf(EVENTO_ID));
		evento.setNome("Encontros Universitarios");
		evento.setEstado(EstadoEvento.INATIVO);

		when(eventoService.buscarEventoPorId(Long.valueOf(EVENTO_ID))).thenReturn(evento);

		when(messageService.getMessage("PARTICIPAR_EVENTO_INATIVO"))
				.thenReturn("Nao e permitido participar de um evento inativo");

		action = mockMvc.perform(post("/autor/participarEvento").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idEvento", EVENTO_ID));
	}

	@Então("^uma mensagem de erro é retornada dizendo que o aluno não pode participar de eventos inativos$")
	public void mensagemDeErroRetornada() throws Exception {
		action.andExpect(redirectedUrl("/autor")).andExpect(flash().attributeExists("participarEventoInativoError"));
	}
}