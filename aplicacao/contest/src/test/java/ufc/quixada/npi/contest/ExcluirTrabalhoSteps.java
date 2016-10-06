package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;

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
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.TrabalhoService;

public class ExcluirTrabalhoSteps {
	private static final long EVENTO_ID = 1L;
	private static final long TRABALHO_ID = 1L;

	@InjectMocks
	private AutorController autorController;
	
	@Mock
	private EventoService eventoService;
	
	@Mock
	private TrabalhoService trabalhoService;
	
	@Mock
	private MessageService messagesService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private Trabalho trabalho;
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
		
		evento = new Evento();
		evento.setId(EVENTO_ID);
		
		trabalho = new Trabalho();
		trabalho.setId(TRABALHO_ID);
		trabalho.setEvento(evento);
		
	}
	@Dado("^que o aluno deseja remover seu trabalho$")
	public void administradorDesejaExcluirEventoEmOutroEstado(){
		//não é necessária ação
	}
	
	@E("^o prazo de submissão não acabou$")
	public void prazoDeSubmissaoNaoAcabou(){
		Calendar c = Calendar.getInstance();
		c.set(2017, Calendar.FEBRUARY, 23);
		evento.setPrazoSubmissaoFinal(c.getTime());
	}
	@Quando("^seleciona o trabalho que desejo excluir$")
	public void selecionaTrabalhoParaExcluir() throws Throwable {
		when(eventoService.existeEvento(EVENTO_ID)).thenReturn(true);
		when(trabalhoService.existeTrabalho(TRABALHO_ID)).thenReturn(true);
		when(eventoService.buscarEventoPorId(EVENTO_ID)).thenReturn(evento);
		action = mockMvc
				.perform(post("/autor/excluirTrabalho")
				.param("trabalhoId", String.valueOf(EVENTO_ID))
				.param("eventoId", String.valueOf(TRABALHO_ID)));
		
	}
	@Então("^o trabalho deve ser excluído$")
	public void trabalhoExcluido() throws Throwable {
		action.andExpect(status().isFound())
		      .andExpect(redirectedUrl("/autor/listarTrabalhos/"+EVENTO_ID))
		      .andExpect(model().hasNoErrors());
		verify(trabalhoService).remover(TRABALHO_ID);
	}
	@E("^uma mensagem de sucesso deve ser informada$")
	public void mensagemDeSucesso(){
		
	}
	
	
	@E("^está fora do prazo de submissão$")
	public void fimDoPrazoDeSubmissao(){
		Calendar c = Calendar.getInstance();
		c.set(2015, Calendar.FEBRUARY, 23);
		evento.setPrazoSubmissaoFinal(c.getTime());
	}
	
	@Quando("^seleciona o trabalho que deverá ser excluir$")
	public void escolhoTrabalhoParaExcluir() throws Throwable {
		when(eventoService.existeEvento(EVENTO_ID)).thenReturn(true);
		when(trabalhoService.existeTrabalho(TRABALHO_ID)).thenReturn(true);
		when(eventoService.buscarEventoPorId(EVENTO_ID)).thenReturn(evento);
		action = mockMvc
				.perform(post("/autor/excluirTrabalho")
				.param("trabalhoId", String.valueOf(EVENTO_ID))
				.param("eventoId", String.valueOf(TRABALHO_ID)));
	}

	@Então("^o trabalho não deve ser excluído$")
	public void trabalhoNaoDeveSerExcluido() throws Throwable {
		action.andExpect(status().isFound())
	      .andExpect(redirectedUrl("/autor/listarTrabalhos/"+EVENTO_ID))
	      .andExpect(flash().attributeExists("erroExcluir"));
	}
	@E("^uma mensagem deve ser mostrada ao usuário$")
	public void mensagemDeErroMostrada(){
		
	}
}