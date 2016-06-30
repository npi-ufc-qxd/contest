package ufc.quixada.npi.contest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;

public class ExcluirEventoSteps extends Mockito {

	@InjectMocks
	private EventoController eventoController;

	@Mock
	private EventoService eventoService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;

	private static final String ID_EVENTO = "1";

	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;

	@cucumber.api.java.Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
	}

	@Dado("^o administrador deseja excluir um evento$")
	public void administradorDesejaExcluirEventoEmOutroEstado() throws Throwable {

	}

	@Quando("^removo um evento com id (.*)$")
	public void removoEventoComIdValido(String idEvento) throws Throwable {
		evento = new Evento();
		evento.setEstado(EstadoEvento.INATIVO);
		evento.setId(Integer.valueOf(ID_EVENTO));
		when(eventoService.buscarEventoPorId(Integer.valueOf(idEvento))).thenReturn(evento);

		action = mockMvc.perform(get("/evento/remover/{id}", Long.valueOf(ID_EVENTO)));
	}

	@Então("^evento deve ser excluido com sucesso$")
	public void eventoDeveSerExcluidoComSucesso() throws Throwable {

		action.andExpect(status().isFound()).andExpect(redirectedUrl("/evento"));

		verify(participacaoEventoService).removerParticipacaoEvento(evento);
	}

	@Quando("^tento remover um evento com estado (.*) e id (.*)$")
	public void removoEventoComIdValidoEstadoAtivo(String estado, String idEvento) throws Throwable {
		evento = new Evento();
		evento.setEstado(EstadoEvento.valueOf(estado));
		evento.setId(Integer.valueOf(ID_EVENTO));
		when(eventoService.buscarEventoPorId(Integer.valueOf(idEvento))).thenReturn(evento);

		action = mockMvc.perform(get("/evento/remover/{id}", Long.valueOf(ID_EVENTO)));
	}

	@Então("^acontece uma excecao$")
	public void aconteceExcecao() throws Throwable {
		doThrow(IllegalArgumentException.class).when(participacaoEventoService).removerParticipacaoEvento(evento);

		action.andExpect(status().is3xxRedirection());
	}
}