package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

public class AtivarEvento {
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
	
	private static final String PAGINA_ADD_OR_EDIT_EVENTO_ORG = "organizador/org_ativar_eventos";
	private static final String TEMPLATE_ATIVAR_EVENTO_GET = "/eventoOrganizador/ativar/{id}";
	private static final String TEMPLATE_ATIVAR_EVENTO_POST = "/eventoOrganizador/ativar";
	private static final String TEMPLATE_LISTAR_EVENTOS_ATIVOS_ORG = "/eventoOrganizador/ativos";
	
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
		action = mockMvc.perform(get(TEMPLATE_ATIVAR_EVENTO_GET, Long.valueOf(id))).andExpect(view().name(PAGINA_ADD_OR_EDIT_EVENTO_ORG));
	}

	@Quando("^o organizador configura o evento para a data de submissao inicial para (.*), data final de submissao para (.*), data de revisão inicial para (.*) e data de revisão final para (.*) e visibilidade (.*)$")
	public void organizadorConfiguraDatasDeSubimissaoERevisaoEVisibilidade(String dataSubmissaoInicial, String dataSubmissaoFinal, 
			String dataRevisaoInicial, String dataRevisaoFinal, String visibilidade) throws Exception{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate dateSubmissaoInicial = LocalDate.parse(dataSubmissaoInicial, formatter);
		LocalDate dateSubmissaoFinal = LocalDate.parse(dataSubmissaoFinal, formatter);
		LocalDate dateRevisaoInicial = LocalDate.parse(dataRevisaoInicial, formatter);
		LocalDate dateRevisaoFinal = LocalDate.parse(dataRevisaoFinal, formatter);

		evento.setPrazoSubmissaoInicial(Date.valueOf(dateSubmissaoInicial));
		evento.setPrazoSubmissaoFinal(Date.valueOf(dateSubmissaoFinal));
		evento.setPrazoRevisaoInicial(Date.valueOf(dateRevisaoInicial));
		evento.setPrazoRevisaoFinal(Date.valueOf(dateRevisaoFinal));
		
		if(visibilidade.equals(VisibilidadeEvento.PRIVADO)){
			evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		}else{
			evento.setVisibilidade(VisibilidadeEvento.PUBLICO);
		}
		
		action = mockMvc
				.perform(post(TEMPLATE_ATIVAR_EVENTO_POST)
				.param("nome", evento.getNome())
				.param("descricao", evento.getDescricao())
				.param("visibilidade", evento.getVisibilidade().toString())
				.param("prazoSubmissaoInicial", dataSubmissaoInicial)
				.param("prazoSubmissaoFinal", dataSubmissaoFinal)
				.param("prazoRevisaoInicial", dataRevisaoInicial)
				.param("prazoRevisaoFinal", dataRevisaoFinal));
		
	}

	@Então("^o evento é ativado$")
	public void eventoEAtivado() throws Exception{
		action.andExpect(status().isFound())
	      .andExpect(redirectedUrl(TEMPLATE_LISTAR_EVENTOS_ATIVOS_ORG));
	}
	
	@Quando("^o organizador apaga o campo nome e ativa o evento com visibilidade (.*)$")
	public void organizadorAtivaEventoSemNome(String visibilidade) throws Exception{
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
		
		String dataSubmissaoInicial = "01-02-2020";
		String dataSubmissaoFinal = "28-02-2020";
		String dataRevisaoInicial = "12-02-2020";
		String dataRevisaoFinal = "20-02-2020";
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate dateSubmissaoInicial = LocalDate.parse(dataSubmissaoInicial, formatter);
		LocalDate dateSubmissaoFinal = LocalDate.parse(dataSubmissaoFinal, formatter);
		LocalDate dateRevisaoInicial = LocalDate.parse(dataRevisaoInicial, formatter);
		LocalDate dateRevisaoFinal = LocalDate.parse(dataRevisaoFinal, formatter);

		evento.setPrazoSubmissaoInicial(Date.valueOf(dateSubmissaoInicial));
		evento.setPrazoSubmissaoFinal(Date.valueOf(dateSubmissaoFinal));
		evento.setPrazoRevisaoInicial(Date.valueOf(dateRevisaoInicial));
		evento.setPrazoRevisaoFinal(Date.valueOf(dateRevisaoFinal));
		
		when(messageService.getMessage("NOME_EVENTO_VAZIO_ERROR"))
		.thenReturn("O nome do evento deve ser informado");
		if(visibilidade.equals(VisibilidadeEvento.PRIVADO)){
			evento.setVisibilidade(VisibilidadeEvento.PRIVADO);
		}else{
			evento.setVisibilidade(VisibilidadeEvento.PUBLICO);
		}
		action = mockMvc
				.perform(post(TEMPLATE_ATIVAR_EVENTO_POST)
				.param("nome", "")
				.param("descricao", evento.getDescricao())
				.param("visibilidade",evento.getVisibilidade().toString())
				.param("prazoSubmissaoInicial", dataSubmissaoInicial)
				.param("prazoSubmissaoFinal", dataSubmissaoFinal)
				.param("prazoRevisaoInicial", dataRevisaoInicial)
				.param("prazoRevisaoFinal", dataRevisaoFinal));
		
	}

	@Então("^uma mensagem de erro no nome é retornada ao organizador$")
	public void mensagemDeErroNoNome() throws Exception{
		action.andExpect(model().attributeHasFieldErrors("evento", "nome"));
	}
}