package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;

public class CadastrarTrilhasSteps {

	private static final String EVENTO_ID = "2";

	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	
	@Mock
	private PessoaService pessoaService;
	
	@Mock
	private TrilhaService trilhaService;

	@Mock
	private EventoService eventoService;

	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		pessoaService.toString(); //Para evitar do codacy reclamar
		trilhaService.toString(); //Para evitar do codacy reclamar
	}

	@Dado("que o organizador criou um evento (.*) e (.*)")
	public void existeEvento(String nomeEvento, String descricaoEvento) throws Throwable{
		evento = new Evento();
		evento.setNome(nomeEvento);
		evento.setDescricao(descricaoEvento);
		evento.setEstado(EstadoEvento.ATIVO);
		evento.setId(Long.parseLong(EVENTO_ID));
	}

	@Quando("^o organizador cadastra uma trilha de submissão (.*)$")
	public void cadastraSubmissao(String nomeTrilha) throws Throwable {
		Trilha trilha = new Trilha();
		trilha.setNome(nomeTrilha);
		trilha.setId(3L);
		
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		action = mockMvc
				.perform(post("/eventoOrganizador/trilhas")
					.param("eventoId", evento.getId().toString())
					.param("nome", trilha.getNome())
					.param("id", trilha.getId().toString())
				);
	}

	@Então("^a trilha de submissão é cadastrada$")
	public void submissaoCadastrada() throws Throwable {
		action.andExpect(view().name(Constants.TEMPLATE_DETALHES_TRILHA_ORG));
	}
	
}
