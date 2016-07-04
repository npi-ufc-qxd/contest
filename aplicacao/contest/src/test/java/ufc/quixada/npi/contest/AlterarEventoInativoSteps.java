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

import cucumber.api.java.pt.*;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class AlterarEventoInativoSteps {

	private static final String PESSOA_ID = "1";
	private static final String EVENTO_ID = "1";
	private static final String TEMPLATE_ADICIONAR_OU_EDITAR = "evento/add_ou_edit";


	@InjectMocks
	private EventoController eventoController;

	@Mock
	private PessoaService pessoaService;
	
	@Mock
	private EventoService eventoService;

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
	
	@Dado("^que existe um administrador$")
	public void existeAdministrador() throws Throwable{
		pessoa = new Pessoa();
		pessoa.setNome("lucas");
		pessoa.setCpf("789287454457");
		pessoa.setEmail("a@a.com");

	
		when(pessoaService.get(Long.valueOf(PESSOA_ID))).thenReturn(pessoa);
	}
	
	@Quando("^o administrador tenta alterar um evento que não existe$")
	public void administradorAlteraEventoInexistente() throws Throwable{
		action = mockMvc
				.perform(post("/evento/alterar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", "100")
				);
		

	}
	
	@Então("^uma mensagem de erro é retornada$")
	public void conclusaoAlterarInexistente() throws Throwable{
		action.andExpect(redirectedUrl("/evento"));
	}

	@Dado("^que o administrador deseja alterar um evento$")
	public void administradorDesejaAlterarUmEvento() throws Throwable {
		
	}
	
	@E("^o evento escolhido é um evento inativo de nome (.*)$")
	public void serEventoInativo(String nomeEvento) throws Throwable{
		evento = new Evento();
		evento.setNome(nomeEvento);
		evento.setId(Long.valueOf(EVENTO_ID));
		evento.setEstado(EstadoEvento.INATIVO);
	}

	@Quando("^um novo nome (.*) do evento é informado$")
	public void novoNomeDeEventoInformado(String novoNome) throws Throwable {
		
		when(eventoService.buscarEventoPorId(Long.valueOf(EVENTO_ID))).thenReturn(evento);
		when(pessoaService.get(Long.valueOf(PESSOA_ID))).thenReturn(pessoa);
		
		action = mockMvc
				.perform(post("/evento/adicionar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("organizador", PESSOA_ID)
				.param("nome", novoNome)
				.param("id", EVENTO_ID));
		
	}

	@Então("^os dados do evento devem ser atualizados$")
	public void eventoAtualizado() throws Throwable {
		verify(eventoService).buscarEventoPorId(Long.valueOf(EVENTO_ID));
		verify(pessoaService).get(Long.valueOf(PESSOA_ID));
		action.andExpect(redirectedUrl(TEMPLATE_ADICIONAR_OU_EDITAR));
	}
}