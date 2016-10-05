package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.gl.E;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Entao;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;

public class OrganizadorVisualizaDetalhesEventoSteps {

	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	
	@Mock
	private EventoService eventoService;

	@Mock
	private PessoaService pessoaService;

	@Mock
	private MessageService messageService;
	
	@Mock
	private TrabalhoService trabalhoService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa organizadorLogado;
	private Long EVENTO_ID = 1L;
	private Long EVENTO_ID_INEXISTENTE = 2L;
	private Long EVENTO_ID_INATIVO = 3L;
	private Evento eventoAtivo;
	private Pessoa organizador;
	
	private static final String EVENTO_INATIVO = "eventoInativo";
	private static final String EVENTO_INEXISTENTE = "eventoInexistente";
	
	private static final String PAGINA_DETALHES_EVENTO_INATIVO = "/eventoOrganizador/detalhes-evento/3";
	private static final String PAGINA_DETALHES_EVENTO_INEXISTENTE = "/eventoOrganizador/detalhes-evento/2";
	private static final String PAGINA_DETALHES_EVENTO_ID_1 = "/eventoOrganizador/detalhes-evento/1";
	private static final String PAGINA_DETALHES_EVENTO = "/eventoOrganizador/detalhes-evento/{id}";
	private static final String ID_PESSOA = "2";
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		eventoAtivo = new Evento();
		eventoAtivo.setId(EVENTO_ID);
		eventoAtivo.setEstado(EstadoEvento.ATIVO);
		organizador = new Pessoa();
		
		organizador.setCpf("123");
		organizador.setEmail("a@a");
		organizador.setId(Long.valueOf(ID_PESSOA));
		organizador.setNome("Joao");
	}

	/**
	 * Cenário 1
	 */
	@Dado("^Dado Estou logado no sistema como organizador$")
	public void logadoComoOrganizador() {
		organizadorLogado = new Pessoa();
		organizadorLogado.setPapelLdap("DOCENTE");
		organizadorLogado.setId(Long.valueOf(5));
		
		pessoaService.getClass();
		when(eventoControllerOrganizador.getOrganizadorLogado()).thenReturn(organizadorLogado);
	}
	
	@Quando("^Escolho visualizar os detalhes de um evento ativo com id (.*)$")
	public void visualizarDetalhesDeUmEventoAtivo() throws Exception{
		action = mockMvc.perform(get(PAGINA_DETALHES_EVENTO, EVENTO_ID))
				.andExpect(view().name(PAGINA_DETALHES_EVENTO_ID_1));
	}
	
	@Entao("^Deve ser mostrado os periodos de submissão e revisão de trabalhos do evento$")
	public void mostrarPeriodosDeSubmissaoERevisaoDoEvento(){
		verify(eventoService).buscarEventoPorId(Long.valueOf(EVENTO_ID));
	}
	
	@E("^Deve ser mostrado a quantidade de Trilhas no evento$")
	public void mostrarQuantidadeDeTrilhasNoEvento(){
		verify(eventoAtivo.getTrilhas().size());
	}
	
	@E("^Deve ser mostrado a quantidade de Revisores no evento$")
	public void mostrarQuantidadeDeRevisoresNoEvento(){
		verify(pessoaService.getPossiveisOrganizadores());
	}
	
	@E("^Deve ser mostrado a quantidade de Trabalhos submetidos, revisados e não revisados no evento$")
	public void mostrarQuantidadeDeTrabalhosNoEvento(){
		verify(trabalhoService.getTrabalhosEvento(eventoAtivo).size());
	}
	
	/**
	 * Cenário 2: Organizador visualiza os detalhes de um evento que está inativo 
	 * @throws Exception 
	 */
	@Quando("^Escolho visualizar os detalhes de um evento inativo com id (.*)$")
	public void visualizarDetalhesDeEventoInativo() throws Exception{
		action = mockMvc.perform(get(PAGINA_DETALHES_EVENTO, EVENTO_ID_INATIVO))
				.andExpect(view().name(PAGINA_DETALHES_EVENTO_INATIVO));
	}
	
	@Entao("^Então Deve ser mostrado uma mensagem informando que o evento está inátivo$")
	public void informarQueOEventoEstaInativo() throws Exception{
		action.andExpect(model().attribute("EVENTO_INATIVO", EVENTO_INATIVO));
	}
	
	/**
	 * Cenário 3: Organizador visualiza os detalhes de um evento inexistente
	 * @throws Exception 
	 */
	//TODO mudar para evento inexistente
	@Quando("^Escolho visualizar os detalhes de um evento inexistente com id (.*)$")
	public void visualizarDetalhesDeEventoInexistente() throws Exception{
		action = mockMvc.perform(get(PAGINA_DETALHES_EVENTO, EVENTO_ID_INEXISTENTE))
				.andExpect(view().name(PAGINA_DETALHES_EVENTO_INEXISTENTE));
	}
	
	@Entao("^Então Deve ser mostrado uma mensagem informando que o evento não existe$")
	public void informarQueOEventoNaoExiste() throws Exception{
		action.andExpect(model().attribute(EVENTO_INEXISTENTE, EVENTO_ID_INEXISTENTE));
	}
	
}