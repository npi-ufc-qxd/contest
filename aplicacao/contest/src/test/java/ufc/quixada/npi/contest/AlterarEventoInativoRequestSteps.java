package ufc.quixada.npi.contest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

public class AlterarEventoInativoRequestSteps {

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
	}
	
	@Quando("^o administrador tenta alterar um evento de nome (.*)$")
	public void administradorAlteraEventoInexistente(String nome) throws Throwable{
		evento = new Evento();
		evento.setNome(nome);
		evento.setId(Long.valueOf(EVENTO_ID));
		evento.setEstado(EstadoEvento.INATIVO);
		
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		action = mockMvc.perform(get("/evento/alterar/{id}", evento.getId()));
	}
	
	@Então("^é redirecionado para a pagina de formulario$")
	public void conclusaoAlterarInexistente() throws Throwable{
		action.andExpect(view().name(TEMPLATE_ADICIONAR_OU_EDITAR));
	}
	
}