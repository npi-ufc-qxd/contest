package ufc.quixada.npi.contest;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

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
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class CadastrarEventosSteps {

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
	private List<Trilha> trilhas;
	private final String TEMPLATE_ADD_EVENTO = "evento/admin_cadastrar";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
		eventoService.toString();//Para evitar do codacy reclamar
		trilhas = new ArrayList<>();
		evento = new Evento();
		pessoa = new Pessoa();
		Trilha trilha = new Trilha();
		
		trilha.setNome("Principal");
		trilha.setEvento(evento);
		
		trilhas.add(trilha);
	}

	@Dado("^o administrador deseja cadastrar um evento.$")
	public void administradorDesejaCadastrarUmEvento() throws Throwable {
		// nenhuma ação de teste necessária
	}

	@Quando("^informar o organizador (.*) e o evento com nome (.*) e descricao (.*)")
	public void casoTesteQuando(String email, String nomeEvento, String descricaoEvento) throws Throwable {

		
		pessoa.setNome("fulano");
		pessoa.setCpf("789287454457");
		pessoa.setEmail(email);

		
		evento.setNome(nomeEvento);
		evento.setDescricao(descricaoEvento);
		evento.setEstado(EstadoEvento.INATIVO);
		evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		evento.setTrilhas(trilhas);
		
		when(pessoaService.get(Long.valueOf(PESSOA_ID))).thenReturn(pessoa);

		action = mockMvc
				.perform(post("/evento/adicionarEvento")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("descricao", descricaoEvento)
				.param("email",pessoa.getEmail()));
		
	}

	@Então("^o organizador deve ser convidado por email e o evento deve ser cadastrado com visibilidade privada e estado inativo.$")
	public void casoTesteEntao() throws Throwable {
		verify(eventoService).adicionarOuAtualizarEvento(evento);		
		verify(eventoService).adicionarOrganizador(pessoa.getEmail(), evento, "http://localhost:80");
		action.andExpect(redirectedUrl("/evento/inativos")).andExpect(model().hasNoErrors());
	}

	@Quando("^informar somente o nome do evento (.*)$")
	public void casoTesteQuando2(String nomeEvento) throws Throwable {

		evento.setNome(nomeEvento);
		evento.setEstado(EstadoEvento.INATIVO);
		evento.setTrilhas(trilhas);
		evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		
		String emailOrganizador =  "";
		
		when(messageService.getMessage("ORGANIZADOR_VAZIO_ERROR"))
			.thenReturn("O organizador do evento deve ser informado");
		
		action = mockMvc
				.perform(post("/evento/adicionarEvento")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("email", emailOrganizador));
		
	}
	
	@Então("^O evento não deve ser cadastrado$")
	public void casoTesteEntao2() throws Throwable {
		action.andExpect(view().name(TEMPLATE_ADD_EVENTO))
		.andExpect(model().attributeHasErrors("evento"));
	}
	
	
	
	@Quando("^informar somente o organizador (.*)$")
	public void casoTesteQuando4(String organizador) throws Exception{
		String nomeEvento = "";
		
		when(messageService.getMessage("NOME_EVENTO_VAZIO_ERROR"))
		.thenReturn("O nome do evento não pode ser vazio");
		
		action = mockMvc
				.perform(post("/evento/adicionarEvento")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("organizador", PESSOA_ID));
	
	}
	
	@Então("^O evento não deve ser cadastrado devido ao nome vazio$")
	public void casoTesteEntao4() throws Exception{
		action.andExpect(view().name(TEMPLATE_ADD_EVENTO))
		.andExpect(model().attributeHasFieldErrors("evento", "nome"));
	}
	
}