package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Entao;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class AlterarEventoAtivoSteps {

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
	private Pessoa admin;
	private Pessoa org;
	private Evento evento;

	@cucumber.api.java.Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
		evento = new Evento();
		org = new Pessoa();
	}
	
	@Dado("^que o administrador deseja alterar um evento$")
	public void desejaAlterarEvento() throws Throwable{
		admin = new Pessoa();
		admin.setNome("lucas");
		admin.setCpf("789287454457");
		admin.setEmail("a@a.com");
	}
	
	@Quando("^o evento escolhido é um evento ativo$")
	public void escolheEventoId(String nome, String descricao, String organizador ) throws Throwable{
		evento.setNome(nome);
		evento.setDescricao(descricao);
		evento.setId(Long.valueOf(EVENTO_ID));
		evento.setEstado(EstadoEvento.ATIVO);
		
		
		org.setNome(organizador);
		org.setCpf("789287454457");
		org.setEmail("a@a.com");
		org.setPapelLdap("DOCENTE");
		
		when(eventoService.existeEvento(Long.valueOf(EVENTO_ID))).thenReturn(true);
		when(eventoService.buscarEventoPorId(Long.valueOf(EVENTO_ID))).thenReturn(evento);
		action = mockMvc
				.perform(get("/evento/alterar")
				.param("id", EVENTO_ID));
	}
	
	@Entao("^o evento não deve ser alterado$")
	public void redirecionaFormulario() throws Throwable{
		action.andExpect(view().name(TEMPLATE_ADICIONAR_OU_EDITAR));
	}
	
	@E("^o usuário é avisado via mensagem o motivo do insucesso do cadastro$")
	public void redirecionado() throws Throwable{
		action.andExpect(view().name(TEMPLATE_ADICIONAR_OU_EDITAR));
	}
	
	
	
}