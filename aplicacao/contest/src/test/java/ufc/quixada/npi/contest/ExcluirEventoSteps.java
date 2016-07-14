package ufc.quixada.npi.contest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class ExcluirEventoSteps extends Mockito {

	@InjectMocks
	private EventoController eventoController;

	@Mock
	private EventoService eventoService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;
	
	@Mock
	private PessoaService pessoaService;

	@Mock
	private MessageService messages;

	private static final String ID_EVENTO = "1";
	private static final String TEMPLATE_REMOVER = "/evento/remover/{id}";
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private List<ParticipacaoEvento> listaParticipacaoEvento;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
		pessoaService.toString(); //Para evitar do codacy reclamar
	}

	@Dado("^o administrador deseja excluir um evento$")
	public void administradorDesejaExcluirEventoEmOutroEstado() throws Throwable {
		//Nenhuma ação de teste necessária aqui.
	}

	@Quando("^ele selecionar a lista de eventos (.*)$")
	public void administradorVaiParaPaginaDeEventosInativos(String estado) throws Throwable {
		listaParticipacaoEvento = new ArrayList<>();
		when(participacaoEventoService.getEventosByEstadoAndPapelOrganizador(EstadoEvento.valueOf(estado)))
				.thenReturn(listaParticipacaoEvento);

		if (estado.equals(EstadoEvento.INATIVO.toString())) {
			action = mockMvc.perform(get("/evento/inativos"))
					.andExpect(view().name("evento/admin_lista_inativos"));
		} else {
			action = mockMvc.perform(get("/evento/ativos"))
					.andExpect(view().name("evento/admin_lista_ativos"));
		}
	}

	@Então("^todos os eventos (.*) devem ser exibidos$")
	public void umaListaDeEventosInativosEMostrada(String estado) throws Throwable {
		if (estado.equals(EstadoEvento.INATIVO.toString())) {
			action.andExpect(model().attribute("eventosInativos", listaParticipacaoEvento));
		} else {
			action.andExpect(model().attribute("eventosAtivos", listaParticipacaoEvento));
		}

	}

	@Quando("^removo um evento com id (.*)$")
	public void removoEventoComIdValido(String idEvento) throws Throwable {
		evento = new Evento();
		evento.setEstado(EstadoEvento.INATIVO);
		evento.setId(Long.valueOf(ID_EVENTO));
		when(eventoService.buscarEventoPorId(Long.valueOf(idEvento))).thenReturn(evento);
		when(messages.getMessage("EVENTO_INATIVO_EXCLUIDO_SUCESSO"))
				.thenReturn("Evento inativo excluido com sucesso");

		action = mockMvc.perform(get(TEMPLATE_REMOVER, Long.valueOf(ID_EVENTO)));
	}

	@Então("^evento deve ser excluido com sucesso$")
	public void eventoDeveSerExcluidoComSucesso() throws Throwable {
		action.andExpect(status().isFound()).andExpect(redirectedUrl("/evento/inativos"))
				.andExpect(flash().attributeExists("sucesso"));

		verify(participacaoEventoService).removerParticipacaoEvento(evento);
	}

	@Quando("^tento remover um evento com estado (.*) e id (.*)$")
	public void removoEventoComIdValidoEstadoAtivo(String estado, String idEvento) throws Throwable {
		evento = new Evento();
		evento.setEstado(EstadoEvento.valueOf(estado));
		evento.setId(Long.valueOf(ID_EVENTO));
		when(eventoService.buscarEventoPorId(Long.valueOf(idEvento))).thenReturn(evento);

		action = mockMvc.perform(get(TEMPLATE_REMOVER, Long.valueOf(ID_EVENTO)));
	}

	@Então("^o usuário é informado que não pode excluir esse evento$")
	public void aconteceExcecao() throws Throwable {
		when(messages.getMessage("EVENTO_INATIVO_EXCLUIDO_ERRO")).thenReturn("Evento não encontrado");
		doThrow(IllegalArgumentException.class).when(participacaoEventoService).removerParticipacaoEvento(evento);

		action.andExpect(status().is3xxRedirection());
	}

	public List<ParticipacaoEvento> popularParticipacaoEvento() {
		listaParticipacaoEvento = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
			listaParticipacaoEvento.add(participacaoEvento);
		}

		return listaParticipacaoEvento;
	}
}