package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import ufc.quixada.npi.contest.model.PapelSistema.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
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
	private TrabalhoService trabalhoService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Long EVENTO_ID = 1L;
	private Evento eventoAtivo; 
	
	private static final String PAGINA_VISUAIZAR_DETALHES_EVENTO = "organizador/org_detalhes_evento";
	private static final String PAGINA_DETALHES_EVENTO = "/eventoOrganizador/evento/{id}";
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		eventoAtivo = new Evento();
		eventoAtivo.setId(EVENTO_ID);
		eventoAtivo.setEstado(EstadoEvento.ATIVO);
		
		Calendar c = Calendar.getInstance();
		eventoAtivo.setPrazoSubmissaoInicial(c.getTime());
		c.add(Calendar.DAY_OF_MONTH, 5);
		eventoAtivo.setPrazoRevisaoInicial(c.getTime());
		c.add(Calendar.DAY_OF_MONTH, 5);
		eventoAtivo.setPrazoRevisaoFinal(c.getTime());
		c.add(Calendar.DAY_OF_MONTH, 5);
		eventoAtivo.setPrazoSubmissaoFinal(c.getTime());
		
		List<Trilha> trilhas = new ArrayList<Trilha>();
		Trilha trilha = new Trilha();
		trilha.setId(1L);
		trilha.setNome("Trilha 1");
		trilhas.add(trilha);
		
		eventoAtivo.setTrilhas(trilhas);
		
		
	}

	/**
	 * Cenário 1
	 */
	@Dado("^Estou logado no sistema como organizador$")
	public void logadoComoOrganizador() {
		Pessoa organizadorLogado;
		organizadorLogado = new Pessoa();
		organizadorLogado.setPapelLdap("DOCENTE");
		organizadorLogado.setId(Long.valueOf(5));
		organizadorLogado.setCpf("000000");
		
		Pessoa p1 = new Pessoa();
		p1.setCpf("11111");
		p1.setNome("ze");
		p1.setPapelLdap("DOCENTE");
		
		Pessoa p2 = new Pessoa();
		p2.setCpf("222222");
		p2.setNome("joao");
		p2.setPapelLdap("DISCENTE");
		
		ParticipacaoEvento participacao1 = new ParticipacaoEvento();
		participacao1.setEvento(eventoAtivo);
		participacao1.setPapel(Papel.ORGANIZADOR);
		participacao1.setPessoa(organizadorLogado);
		
		ParticipacaoEvento participacao2 = new ParticipacaoEvento();
		participacao2.setEvento(eventoAtivo);
		participacao2.setPapel(Papel.REVISOR);
		participacao2.setPessoa(p1);
		
		ParticipacaoEvento participacao3 = new ParticipacaoEvento();
		participacao3.setEvento(eventoAtivo);
		participacao3.setPapel(Papel.AUTOR);
		participacao3.setPessoa(p2);
		
		List<ParticipacaoEvento> participacaoEventos = new ArrayList<ParticipacaoEvento>();
		participacaoEventos.add(participacao1);
		participacaoEventos.add(participacao2);
		participacaoEventos.add(participacao3);
		
		eventoAtivo.setParticipacoes(participacaoEventos);
		
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn(organizadorLogado.getCpf());
		
		SecurityContextHolder.setContext(context);
		
		when(pessoaService.getByCpf(organizadorLogado.getCpf())).thenReturn(organizadorLogado);
		
	}
	
	@Quando("^Escolho visualizar os detalhes de um evento ativo$")
	public void visualizarDetalhesDeUmEventoAtivo() throws Exception{
		
		when(eventoService.buscarEventoPorId(EVENTO_ID)).thenReturn(eventoAtivo);
		action = mockMvc.perform(get(PAGINA_DETALHES_EVENTO, EVENTO_ID))
				.andExpect(view().name(PAGINA_VISUAIZAR_DETALHES_EVENTO));
		
		verify(eventoService).buscarEventoPorId(Long.valueOf(EVENTO_ID));
	}
	
	@Entao("^Deve ser mostrado os periodos de submissão e revisão de trabalhos do evento$")
	public void mostrarPeriodosDeSubmissaoERevisaoDoEvento() throws Exception{
		action = action.andExpect(model().attribute("evento", eventoAtivo));
	}
	
	@E("^Deve ser mostrado a quantidade de Trilhas no evento$")
	public void mostrarQuantidadeDeTrilhasNoEvento(){
		//verify(eventoAtivo.getTrilhas().size());
	}
	
	@E("^Deve ser mostrado a quantidade de Revisores no evento$")
	public void mostrarQuantidadeDeRevisoresNoEvento(){
		//verify(pessoaService.getPossiveisOrganizadores());
	}
	
	@E("^Deve ser mostrado a quantidade de Trabalhos submetidos, revisados e não revisados no evento$")
	public void mostrarQuantidadeDeTrabalhosNoEvento() throws Exception{
		action = action.andExpect(model().attributeExists("numeroTrabalhos" ,
				"numeroTrabalhosNaoRevisados",
				"numeroTrabalhosRevisados"
				)
		);
	}
	
	@Quando("^Escolho visualizar os detalhes de um evento inexistente$")
	public void visualizarDetalhesDeEventoInexistente() throws Exception{
	/*	action = mockMvc.perform(get(PAGINA_DETALHES_EVENTO, EVENTO_ID_INEXISTENTE))
				.andExpect(view().name(PAGINA_DETALHES_EVENTO_INEXISTENTE));*/
	}
	
	@Entao("^Deve ser mostrado uma mensagem informando que o evento não existe$")
	public void informarQueOEventoNaoExiste() throws Exception{
		//action.andExpect(model().attribute(EVENTO_INEXISTENTE, EVENTO_ID_INEXISTENTE));
	}
	
}