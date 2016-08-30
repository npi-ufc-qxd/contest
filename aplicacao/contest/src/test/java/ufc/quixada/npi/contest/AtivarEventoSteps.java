package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.validator.EventoValidator;

public class AtivarEventoSteps {
	private static final String PRAZO_REVISAO_FINAL = "prazoRevisaoFinal";
	private static final String PRAZO_REVISAO_INICIAL = "prazoRevisaoInicial";
	private static final String PRAZO_SUBMISSAO_FINAL = "prazoSubmissaoFinal";
	private static final String PRAZO_SUBMISSAO_INICIAL = "prazoSubmissaoInicial";
	private static final String ID = "1";
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
	private SubmissaoService submissaoService;
	
	private static final String TEMPLATE_ADD_OR_EDIT_EVENTO_ORG = "organizador/org_ativar_eventos";
	private static final String PAGINA_ATIVAR_EVENTO_GET = "/eventoOrganizador/ativar/{id}";
	private static final String PAGINA_ATIVAR_EVENTO_POST = "/eventoOrganizador/ativar";
	private static final String PAGINA_LISTAR_EVENTOS_ATIVOS_ORG = "/eventoOrganizador/ativos";
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		evento = new Evento();
		
		evento.setEstado(EstadoEvento.INATIVO);
		evento.setNome("teste");
		evento.setDescricao("descricao");
		evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		evento.setId(1L);
		
		participacaoEventoService.toString();
		pessoaService.toString();
	}

	@Dado("^que o organizado deseja ativar um evento com o id (.*)$")
	public void administradorDesejaAtivarEvento(String id) throws Throwable {
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		action = mockMvc.perform(get(PAGINA_ATIVAR_EVENTO_GET, Long.valueOf(id)))
				.andExpect(view().name(TEMPLATE_ADD_OR_EDIT_EVENTO_ORG));
	}

	@Quando("^o organizador configura o evento para a data de submissao inicial para (.*), data final de submissao para (.*), data de revisão inicial para (.*) e data de revisão final para (.*) e visibilidade (.*)$")
	public void organizadorConfiguraDatasDeSubimissaoERevisaoEVisibilidade(String dataSubmissaoInicial, String dataSubmissaoFinal, 
			String dataRevisaoInicial, String dataRevisaoFinal, String visibilidade) throws Exception{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		
		if(visibilidade.equals(VisibilidadeEvento.PRIVADO)){
			evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		}else{
			evento.setVisibilidade(VisibilidadeEvento.PUBLICO);
		}
		when(eventoService.adicionarOuAtualizarEvento(evento)).thenReturn(true);
		
		action = mockMvc
				.perform(post(PAGINA_ATIVAR_EVENTO_POST)
				.param("nome", evento.getNome())
				.param("id", ID)
				.param("descricao", evento.getDescricao())
				.param("visibilidade", evento.getVisibilidade().toString())
				.param(PRAZO_SUBMISSAO_INICIAL, dataSubmissaoInicial)
				.param(PRAZO_SUBMISSAO_FINAL, dataSubmissaoFinal)
				.param(PRAZO_REVISAO_INICIAL, dataRevisaoInicial)
				.param(PRAZO_REVISAO_FINAL, dataRevisaoFinal));
		
	}

	@Então("^o evento é ativado$")
	public void eventoEAtivado() throws Exception{
		action.andExpect(status().isFound())
	      .andExpect(redirectedUrl(PAGINA_LISTAR_EVENTOS_ATIVOS_ORG));
	}
	
	@Quando("^o organizador apaga o campo nome e ativa o evento com visibilidade (.*)$")
	public void organizadorAtivaEventoSemNome(String visibilidade) throws Exception{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		
		String dataSubmissaoInicial = "01//02//2020";
		String dataSubmissaoFinal = "28//02//2020";
		String dataRevisaoInicial = "12//02//2020";
		String dataRevisaoFinal = "20//02//2020";

		when(messageService.getMessage("NOME_EVENTO_VAZIO_ERROR"))
		.thenReturn("O nome do evento deve ser informado");
		
		if(visibilidade.equals(VisibilidadeEvento.PRIVADO)){
			evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		}else{
			evento.setVisibilidade(VisibilidadeEvento.PUBLICO);
		}
		
		when(submissaoService.existeTrabalhoNesseEvento(evento.getId())).thenReturn(false);
		when(revisaoService.existeTrabalhoNesseEvento(evento.getId())).thenReturn(false);
		
		action = mockMvc
				.perform(post(PAGINA_ATIVAR_EVENTO_POST)
				.param("nome", "")
				.param("id", ID)
				.param("descricao", evento.getDescricao())
				.param("visibilidade",evento.getVisibilidade().toString())
				.param(PRAZO_SUBMISSAO_INICIAL, dataSubmissaoInicial)
				.param(PRAZO_SUBMISSAO_FINAL, dataSubmissaoFinal)
				.param(PRAZO_REVISAO_INICIAL, dataRevisaoInicial)
				.param(PRAZO_REVISAO_FINAL, dataRevisaoFinal));
		
	}

	@Então("^uma mensagem de erro no nome é retornada ao organizador$")
	public void mensagemDeErroNoNome() throws Exception{
		action.andExpect(model().attributeHasFieldErrors("evento", "nome"));
	}
}