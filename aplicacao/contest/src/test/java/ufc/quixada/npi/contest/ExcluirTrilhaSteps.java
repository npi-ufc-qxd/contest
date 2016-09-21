package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;

public class ExcluirTrilhaSteps {

	private static final String EVENTO_ID = "2";
	private static final String TRILHA_ID = "3";

	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	
	@Mock
	private PessoaService pessoaService;
	
	@Mock
	private TrilhaService trilhaService;
	
	@Mock
	private TrabalhoService trabalhoService;

	@Mock
	private MessageService messageService;
	
	@Mock
	private EventoService eventoService;

	private MockMvc mockMvc;
	private ResultActions action;
	
	private Evento evento;
	private Trilha trilha;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		pessoaService.toString(); //Para evitar do codacy reclamar
		trilhaService.toString(); //Para evitar do codacy reclamar
		
		trilha = new Trilha();
		trilha.setNome("nomeTrilha");
		trilha.setId(Long.parseLong(TRILHA_ID));
	}
	
	@Dado("^que o organizador deseja excluir uma trilha de submissão de um evento$")
	public void desejaAlterarNomeDeTrilha() throws Throwable{
		evento = new Evento();
		evento.setNome("nomeEvento");
		evento.setDescricao("descricaoEvento");
		evento.setEstado(EstadoEvento.ATIVO);
		evento.setId(Long.parseLong(EVENTO_ID));
	}
	
	@E("^não existe nenhum trabalho submetido para essa trilha$")
	public void naoExisteTrabalhoSubmetido() throws Throwable {
		when(trilhaService.existeTrabalho(trilha.getId())).thenReturn(false);
	}
	
	@Quando("^o organizador seleciona uma trilha$")
	public void selecionaTrilhaParaAlterar() throws Throwable{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		action = mockMvc
				.perform(get("/eventoOrganizador/trilha/excluir/{trilhaId}/{eventoId}",TRILHA_ID, EVENTO_ID));
	}
	
	@Então("^a trilha é excluída$")
	public void trilhaExcluida() throws Throwable{
		action.andExpect(redirectedUrl("/eventoOrganizador/trilhas/" + EVENTO_ID));
	}
	
	@E("^existe algum trabalho submetido para essa trilha$")
	public void existeTrabalhoSubmetido() throws Throwable {
		when(trilhaService.existeTrabalho(trilha.getId())).thenReturn(true);
	}
	
	@Quando("^o organizador seleciona uma trilha sem trabalho$")
	public void selecionaTrilha() throws Throwable{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		action = mockMvc
				.perform(get("/eventoOrganizador/trilha/excluir/{trilhaId}/{eventoId}",TRILHA_ID, EVENTO_ID));
	}
	
	@Então("^retorna uma mensagem de erro$")
	public void retornaMensagemDeErro() throws Throwable{
		action.andExpect(redirectedUrl("/eventoOrganizador/trilhas/" + EVENTO_ID))
		.andExpect(model().attribute("organizadorError", messageService.getMessage("EVENTO_VAZIO_OU_TEM_TRABALHO")));
	}

}
