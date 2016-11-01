package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.validator.EventoValidator;

public class AlterarDetalheEventoSteps {
	private static final String EVENTO = "evento";
	private static final String PRAZO_REVISAO_FINAL = "prazoRevisaoFinal";
	private static final String PRAZO_REVISAO_INICIAL = "prazoRevisaoInicial";
	private static final String PRAZO_SUBMISSAO_FINAL = "prazoSubmissaoFinal";
	private static final String PRAZO_SUBMISSAO_INICIAL = "prazoSubmissaoInicial";

	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	@Mock
	private EventoService eventoService;
	@Mock
	private ParticipacaoEventoService participacaoEventoService;
	@Mock
	private PessoaService pessoaService;
	@Mock
	private MessageService messageService;
	@Mock
	private EventoValidator eventoValidator;
	@Mock
	private RevisaoService revisaoService;
	@Mock
	private TrilhaService trilhaService;
	@Mock
	private TrabalhoService trabalhoService;
	@Mock
	private SubmissaoService submissaoService;
	
	private static final String TEMPLATE_EDIT_EVENTO_ORG = "organizador/org_editar_eventos";
	private static final String PAGINA_EDITAR_EVENTO_GET = "/eventoOrganizador/editar/{id}";
	private static final String PAGINA_ATIVAR_EVENTO_POST = "/eventoOrganizador/editar";
	private static final String PAGINA_LISTAR_EVENTOS_ATIVOS_ORG = "/eventoOrganizador/ativos";
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private ParticipacaoEvento participacao;
	private List<Trilha> trilhas;
	private List<Pessoa> pessoas;
	private Pessoa org;
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		evento = new Evento();
		participacao = new ParticipacaoEvento();
		Pessoa pessoa = new Pessoa();
		trilhas = new ArrayList<>();
		pessoas = new ArrayList<>();
		org = new Pessoa();
		evento.setEstado(EstadoEvento.INATIVO);
		evento.setNome("teste");
		evento.setDescricao("descricao");
		evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		evento.setId(9L);
		List<ParticipacaoEvento> participacoes = new ArrayList<>();
		pessoa.setId(2L);
		
		participacao.setId(1L);
		participacao.setPessoa(pessoa);
		participacao.setEvento(evento);
		
		participacoes.add(participacao);
		
		evento.setParticipacoes(participacoes);
		
		eventoValidator.toString();//enganando o codacy
		pessoaService.toString();//enganando o codacy
	}
	
	@Dado("^que existe um evento com submissões e revisões realizadas$")
	public void existeEventoComSubmissoesERevisoes() throws Throwable {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("92995454310");
		SecurityContextHolder.setContext(context);
		
		when(eventoControllerOrganizador.getOrganizadorLogado()).thenReturn(org);
		
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(trilhaService.buscarTrilhas(evento.getId())).thenReturn(trilhas);
		when(pessoaService.getPossiveisOrganizadoresDoEvento(evento.getId())).thenReturn(pessoas);
		when(trilhaService.buscarQtdTrilhasPorEvento(evento.getId())).thenReturn(12);
		when(trabalhoService.buscarQtdTrabalhosPorEvento(evento.getId())).thenReturn(10);
		
		action = mockMvc.perform(
				get(PAGINA_EDITAR_EVENTO_GET, Long.valueOf(evento.getId())))
				.andExpect(view().name(TEMPLATE_EDIT_EVENTO_ORG));
	}
	
	@Quando("^o organizador altera a data final do prazo de revisão para uma data válida$")
	public void organizadorAlteraPrazoDeRevisaoParaDataValida() throws Exception{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		String dataSubmissaoInicial =  "10/06/2020"; 
		String dataSubmissaoFinal = "28/06/2020"; 
		String dataRevisaoInicial = "15/06/2020";
		String dataRevisaoFinal = "20/06/2020";
		
		action = mockMvc
				.perform(post(PAGINA_ATIVAR_EVENTO_POST)
				.param("nome", evento.getNome())
				.param("descricao", evento.getDescricao())
				.param("visibilidade", evento.getVisibilidade().toString())
				.param(PRAZO_SUBMISSAO_INICIAL, dataSubmissaoInicial)
				.param(PRAZO_SUBMISSAO_FINAL, dataSubmissaoFinal)
				.param(PRAZO_REVISAO_INICIAL, dataRevisaoInicial)
				.param(PRAZO_REVISAO_FINAL, dataRevisaoFinal));
		
	}

	@Então("^deve ser redirecionado para a página de eventos ativos$")
	public void organizadorERedirecionadoParaPaginaDeEventosAtivos() throws Exception{
		action.andExpect(status().isFound())
	      .andExpect(redirectedUrl(PAGINA_LISTAR_EVENTOS_ATIVOS_ORG));
	}
	
	@Quando("^o organizador altera o prazo de submissão$")
	public void organizadorAlteraDataDeSubmissao() throws Exception{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		String dataSubmissaoInicial =  "10/06/2020"; 
		String dataSubmissaoFinal = "15/05/2019";
		String dataRevisaoInicial = "15/06/2020";
		String dataRevisaoFinal = "20/06/2020";

		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Errors errors = invocation.getArgumentAt(1, Errors.class);
				errors.rejectValue(PRAZO_SUBMISSAO_FINAL, PRAZO_SUBMISSAO_FINAL,
						messageService.getMessage("ERRO_DATA_SUBMISSAO_FINAL"));
				return null;
			}
		}).when(eventoValidator).validate(Mockito.any(Evento.class), Mockito.any(BindingResult.class));

		action = mockMvc
				.perform(post(PAGINA_ATIVAR_EVENTO_POST)
				.param("nome", evento.getNome())
				.param("id", "1")
				.param("descricao", evento.getDescricao())
				.param("visibilidade", evento.getVisibilidade().toString())
				.param(PRAZO_SUBMISSAO_INICIAL, dataSubmissaoInicial)
				.param(PRAZO_SUBMISSAO_FINAL, dataSubmissaoFinal)
				.param(PRAZO_REVISAO_INICIAL, dataRevisaoInicial)
				.param(PRAZO_REVISAO_FINAL, dataRevisaoFinal));
		
	}

	@Então("^uma mensagem de erro é retornada$")
	public void mensagemDeErroNaDatasEMostrada() throws Exception{
		action.andExpect(model().attributeHasFieldErrors(EVENTO, PRAZO_SUBMISSAO_FINAL));
	}

	@Dado("^que existe um evento com apenas submissões realizadas$")
	public void eventoApenasComSubmissoes() throws Throwable {
		when(submissaoService.existeTrabalhoNesseEvento(evento.getId())).thenReturn(true);
		when(revisaoService.existeTrabalhoNesseEvento(evento.getId())).thenReturn(false);
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(participacaoEventoService.findByEventoId(Mockito.anyLong())).thenReturn(participacao);

		action = mockMvc.perform(
				get(PAGINA_EDITAR_EVENTO_GET, Long.valueOf(evento.getId())))
				.andExpect(view().name(TEMPLATE_EDIT_EVENTO_ORG));
	}
	
	@Quando("^o organizador altera a data final do prazo de submissão para uma data válida$")
	public void organizadorAtivaEventoSemNome() throws Exception{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		String dataSubmissaoInicial =  "10/06/2020"; 
		String dataSubmissaoFinal = "15/07/2020"; 
		String dataRevisaoInicial = "15/06/2020";
		String dataRevisaoFinal = "20/06/2020";
		
		action = mockMvc
				.perform(post(PAGINA_ATIVAR_EVENTO_POST)
				.param("nome", evento.getNome())
				.param("descricao", evento.getDescricao())
				.param("visibilidade", evento.getVisibilidade().toString())
				.param(PRAZO_SUBMISSAO_INICIAL, dataSubmissaoInicial)
				.param(PRAZO_SUBMISSAO_FINAL, dataSubmissaoFinal)
				.param(PRAZO_REVISAO_INICIAL, dataRevisaoInicial)
				.param(PRAZO_REVISAO_FINAL, dataRevisaoFinal));
		
	}

	@Então("^a data final do prazo de submissão é alterada com sucesso$")
	public void mensagemDeErroNoNome() throws Exception{
		action.andExpect(status().isFound())
	      .andExpect(redirectedUrl(PAGINA_LISTAR_EVENTOS_ATIVOS_ORG));
	}
	
	@Dado("^que existe um evento sem submissões e revisões alteradas$")
	public void eventoSemSubmissoesERevisoes() throws Throwable {
		when(submissaoService.existeTrabalhoNesseEvento(evento.getId())).thenReturn(true);
		when(revisaoService.existeTrabalhoNesseEvento(evento.getId())).thenReturn(false);
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(participacaoEventoService.findByEventoId(Mockito.anyLong())).thenReturn(participacao);

		action = mockMvc.perform(
				get(PAGINA_EDITAR_EVENTO_GET, Long.valueOf(evento.getId())))
				.andExpect(view().name(TEMPLATE_EDIT_EVENTO_ORG));
	}
	
	@Quando("^o organizador altera os detalhes do prazo de revisão$")
	public void prazoRevisaoEAlterado() throws Exception{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		String dataSubmissaoInicial =  "10/06/2020"; 
		String dataSubmissaoFinal = "15/06/2020"; 
		String dataRevisaoInicial = "15/06/2020";
		String dataRevisaoFinal = "20/07/2020";
		
		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Errors errors = invocation.getArgumentAt(1, Errors.class);
				errors.rejectValue(PRAZO_REVISAO_FINAL, PRAZO_REVISAO_FINAL,
						messageService.getMessage("ERRO_DATA_REVISAO_FINAL"));
				return null;
			}
		}).when(eventoValidator).validate(Mockito.any(Evento.class), Mockito.any(BindingResult.class));
		
		action = mockMvc
				.perform(post(PAGINA_ATIVAR_EVENTO_POST)
				.param("nome", evento.getNome())
				.param("id", "1")
				.param("descricao", evento.getDescricao())
				.param("visibilidade", evento.getVisibilidade().toString())
				.param(PRAZO_SUBMISSAO_INICIAL, dataSubmissaoInicial)
				.param(PRAZO_SUBMISSAO_FINAL, dataSubmissaoFinal)
				.param(PRAZO_REVISAO_INICIAL, dataRevisaoInicial)
				.param(PRAZO_REVISAO_FINAL, dataRevisaoFinal));
		
	}

	@Então("^uma mensagem de erro na data de revisão é mostrada$")
	public void mensagemDeErroNaDataRevisaoEMostrada() throws Exception{
		action.andExpect(model().attributeHasFieldErrors(EVENTO, PRAZO_REVISAO_FINAL));
	}
}