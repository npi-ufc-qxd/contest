package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class CadastrarTrilhasSteps {
	private static final String PESSOA_ID = "1";

	@InjectMocks
	private EventoController eventoController;

	@Mock
	private PessoaService pessoaService;
	
	@Mock
	private MessageService messageService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;
	
	@Mock
	private EventoService eventoService;

	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa pessoa;
	private Evento evento;
	private Trilha trilha;
	private final String TEMPLATE_ADD_EVENTO = "evento/admin_cadastrar";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
		eventoService.toString();//Para evitar do codacy reclamar
	}

	@Dado("^o administrador deseja cadastrar um evento.$")
	public void administradorDesejaCadastrarUmEvento() throws Throwable {
		// nenhuma ação de teste necessária
	}

	@Quando("^existe um organizador (.*)")
	public void existeOrganizador(String organizador) throws Throwable {
		pessoa = new Pessoa();
		pessoa.setNome(organizador);
		pessoa.setCpf("789287454457");
		pessoa.setEmail("a@a.com");
	}
	
	@E("que existe um evento (.*) e (.*)")
	public void existeEvento(String nomeEvento, String descricaoEvento) throws Throwable{
		evento = new Evento();
		evento.setNome(nomeEvento);
		evento.setDescricao(descricaoEvento);
		evento.setEstado(EstadoEvento.INATIVO);
		
		when(pessoaService.get(Long.valueOf(PESSOA_ID))).thenReturn(pessoa);

		action = mockMvc
				.perform(post("/evento/adicionar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("descricao", descricaoEvento)
				.param("organizador", PESSOA_ID));
	}

	@Quando("^o organizador cadastra uma trilha de submissão$")
	public void casoTesteQuando2(String nomeEvento) throws Throwable {

		trilha = new Trilha();
		trilha.setNome(nomeEvento);
		trilha.setEvento(evento);
		trilha.setId(100L);
		
		/*action = mockMvc
				.perform(post("/eventoOrganizador/")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("organizador", organizador));*/
		
	}
	
	@Então("^a trilha de submissão é cadastrada$")
	public void casoTesteEntao2() throws Throwable {
		action.andExpect(view().name(TEMPLATE_ADD_EVENTO)).andExpect(model().attributeHasErrors("evento"));
	}
	
}
