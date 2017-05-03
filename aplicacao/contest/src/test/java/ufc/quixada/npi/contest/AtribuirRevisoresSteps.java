package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Date;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.RevisaoJsonWrapper;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;

public class AtribuirRevisoresSteps {

	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	@Mock
	private ParticipacaoTrabalhoService participacaoTrabalhoService;
	@Mock
	private PessoaService pessoaService;
	@Mock
	private TrabalhoService trabalhoService;

	
	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa pessoa;
	private Trabalho trabalho;
	private ParticipacaoTrabalho participacaoTrabalho;
	private RevisaoJsonWrapper dadosRevisao;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		pessoa = new Pessoa();
		trabalho = new Trabalho();
		dadosRevisao = new RevisaoJsonWrapper();
		participacaoTrabalho = new ParticipacaoTrabalho();
		dadosRevisao.setRevisorId(1L);
		dadosRevisao.setTrabalhoId(3L);
		Evento evento = new Evento();
		
		Calendar dataInicialRevisao = Calendar.getInstance();
		dataInicialRevisao.set(2016, Calendar.DECEMBER, 30);
		
		Date dataRevisaoInicial = dataInicialRevisao.getTime();
		
		evento.setPrazoRevisaoInicial(dataRevisaoInicial);
		
		pessoa.setId(1L);
		trabalho.setId(3L);

		pessoa.setCpf("123");
		pessoa.setNome("Kuririn");
		pessoa.setEmail("teste@teste.com");
		pessoa.setPapelLdap("DOCENTE");
		trabalho.setTitulo("Meu Trabalho");
		trabalho.setEvento(evento);
	}
	@Dado("^que sou organizador$")
	public void exitesUmEvento() throws Throwable {
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		
		SecurityContextHolder.setContext(context);
		
		when(eventoControllerOrganizador.getOrganizadorLogado()).thenReturn(pessoa);
	}
	@Quando("^o organizador seleciona atribuir um revisor para um trabalho$")
	public void organizadorAtribuiRevisoresAoTrabalho() throws Exception{
		when(pessoaService.get(dadosRevisao.getRevisorId())).thenReturn(pessoa);
		when(trabalhoService.getTrabalhoById(dadosRevisao.getTrabalhoId())).thenReturn(trabalho);
		
		participacaoTrabalho.setPessoa(pessoa);
		participacaoTrabalho.setTrabalho(trabalho);
		participacaoTrabalho.setPapel(Papel.REVISOR);

		String json = String.format("{\"revisorId\": \"1\",\"trabalhoId\": \"3\"}");

        action = mockMvc.perform(post("/eventoOrganizador/evento/trabalho/revisor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json));
	}

	@Então("^o revisor selecionado é atribuído ao trabalho selecionado$")
	public void revisorEAtribuidoAoTrabalho() throws Exception{
		verify(participacaoTrabalhoService).adicionarOuEditar(participacaoTrabalho);
		action.andExpect(status().isOk());
	}
	@Quando("^o organizador seleciona esse revisor para ser removido do trabalho$")
	public void selecionaRevisorParaSerRemovido() throws Exception{
		when(participacaoTrabalhoService.getParticipacaoTrabalhoRevisor(pessoa.getId(), trabalho.getId())).thenReturn(participacaoTrabalho);

		String json = String.format("{\"revisorId\": \"1\",\"trabalhoId\": \"3\"}");
		action = mockMvc
				.perform(post("/eventoOrganizador/evento/trabalho/removerRevisor")
						.contentType(MediaType.APPLICATION_JSON)
			            .content(json));
	}

	@Então("^o revisor selecionado é removido da lista de revisores do trabalho selecionado$")
	public void revisorERemovidoDaLista() throws Exception{
		verify(participacaoTrabalhoService).remover(participacaoTrabalho);
		action.andExpect(status().isOk());
	}
	
}