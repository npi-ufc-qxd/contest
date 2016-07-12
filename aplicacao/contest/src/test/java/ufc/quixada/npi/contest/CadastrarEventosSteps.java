package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class CadastrarEventosSteps {

	private static final String PESSOA_ID = "1";

	@InjectMocks
	private EventoController eventoController;

	@Mock
	private PessoaService pessoaService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;

	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa pessoa;
	private Evento evento;

	@cucumber.api.java.Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
	}

	@Dado("^o administrador deseja cadastrar um evento.$")
	public void administradorDesejaCadastrarUmEvento() throws Throwable {

	}

	@Quando("^informa o organizador (.*) e o nome do evento (.*)$")
	public void casoTesteQuando(String organizador, String nomeEvento) throws Throwable {
		pessoa = new Pessoa();
		pessoa.setNome("lucas");
		pessoa.setCpf("789287454457");
		pessoa.setEmail("a@a.com");

		evento = new Evento();
		evento.setNome(nomeEvento);
		evento.setEstado(EstadoEvento.INATIVO);
		
		when(pessoaService.get(Long.valueOf(PESSOA_ID))).thenReturn(pessoa);

		action = mockMvc
				.perform(post("/evento/adicionar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("organizador", PESSOA_ID));
		
	}

	@Então("^o evento deve ser cadastrado com visibilidade privada e estado inativo.$")
	public void casoTesteEntao() throws Throwable {
		verify(pessoaService).get(Long.valueOf(PESSOA_ID));
		verify(participacaoEventoService).adicionarOuEditarParticipacaoEvento(evento, pessoa,
				Papel.ORGANIZADOR);
		action.andExpect(redirectedUrl("/evento")).andExpect(model().hasNoErrors());

	}

}