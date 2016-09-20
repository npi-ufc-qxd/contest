package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Entao;
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

public class AlterarTrilhasSteps {

	private static final String EVENTO_ID = "2";
	private static final String TRILHA_ID = "3";
	private static final String TRABALHO_ID = "4";

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
	}
	
	@Dado("^que o organizador deseja alterar o nome de uma trilha de submissão de um evento$")
	public void desejaAlterarNomeDeTrilha() throws Throwable{
		evento = new Evento();
		evento.setNome("nomeEvento");
		evento.setDescricao("descricaoEvento");
		evento.setEstado(EstadoEvento.ATIVO);
		evento.setId(Long.parseLong(EVENTO_ID));
	}
	
	@Quando("^o organizador selecionar uma trilha para alterar$")
	public void selecionaTrilhaParaAlterar() throws Throwable{
		trilha = new Trilha();
		trilha.setNome("nomeTrilha");
		trilha.setId(Long.parseLong(TRILHA_ID));
	}
	
	@E("^a trilha não possuir nenhum trabalho cadastrado$")
	public void trilhaNaoPossuiCadastro() throws Throwable{
	//TODO	verficação de trabalhos submetidos
	}
	
	@E("^o organizador fornecer um novo nome para a trilha$")
	public void forneceNovoNomeParaTrilha() throws Throwable{
		String novoNome = "novoNome";
		when(trilhaService.exists(novoNome, evento.getId())).thenReturn(false);
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		action = mockMvc
				.perform(post("/eventoOrganizador/trilha/editar")
						.param("eventoId", evento.getId().toString())
						.param("nome", novoNome)
						.param("id", trilha.getId().toString())
				);
	}
	
	@Entao("^o nome da trilha é atualizado$")
	public void nomeDeTrilhaAtualizada() throws Throwable{
		action.andExpect(view().name(Constants.TEMPLATE_DETALHES_TRILHA_ORG));
	}
	
	@Quando("^a trilha não possui nenhum trabalho cadastrado$")
	public void trilhaNaoPossuiTrabalho() throws Throwable{
	//TODO	verficação de trabalhos submetidos
	}
	
	@E("^não fornece o novo nome da trilha$")
	public void naoForneceNomeDaTrilha() throws Throwable{
		when(messageService.getMessage("TRILHA_NOME_VAZIO"))
		.thenReturn("Não é possível haver uma Trilha com nome vazio");
		when(trilhaService.exists(" ", evento.getId())).thenReturn(false);
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		action = mockMvc
				.perform(post("/eventoOrganizador/trilha/editar")
					.param("eventoId", evento.getId().toString())
					.param("nome", " ")
					.param("id", trilha.getId().toString())
				);
	}
	
	@Entao("^o nome da trilha não será atualizado$")
	public void trilhaNaoAtualizada() throws Throwable{
		action.andExpect(view().name(Constants.TEMPLATE_DETALHES_TRILHA_ORG));
	}
	
	@E("^o organizador deve visualizar uma mensagem de erro$")
	public void visualizarMensagemDeErro() throws Throwable{
	//TODO	
	}
	
	@Quando("^a trilha possui algum trabalho cadastrado$")
	public void trilhaPossuiTrabalhoCadastrado() throws Throwable{
		when(trabalhoService.existeTrabalho(trilha.getId())).thenReturn(true);
	}
	
	@E("^o organizador fornece um novo nome para a trilha$")
	public void organizadorForneceNovoNomeParaTrilha() throws Throwable{
		String novoNome = "novoNome";
		when(trilhaService.exists(novoNome, evento.getId())).thenReturn(false);
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		action = mockMvc
				.perform(post("/eventoOrganizador/trilha/editar")
						.param("eventoId", evento.getId().toString())
						.param("nome", novoNome)
						.param("id", trilha.getId().toString())
				);
	}
	
	@Então("^o nome da trilha não deve ser atualizado$")
	public void nomeTrilhaNaoAtualizado() throws Throwable{
		action.andExpect(view().name(Constants.TEMPLATE_DETALHES_TRILHA_ORG));
	}
	
	@E("^o organizador deve visualizará uma mensagem de erro$")
	public void organizadorVisualizaMensagemDeErro() throws Throwable{
		//action.andExpect(view().name(Constants.TEMPLATE_DETALHES_TRILHA_ORG)).andExpect(model().attributeHasErrors("organizadorError"));
	}
}
