package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.GregorianCalendar;
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
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;

public class AlunoRecebeAvaliacaoSteps {

	@InjectMocks
	private AutorController autorController;
	@Mock
	private EventoService eventoService;
	@Mock
	private PessoaService pessoaService;
	@Mock
	private MessageService messageService;
	@Mock
	private SubmissaoService submissaoService;
	@Mock
	private TrilhaService trilhaService;
	@Mock
	private TrabalhoService trabalhoService;
	@Mock
	private RevisaoService revisaoService;
	

	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private Trabalho trabalho;
	private Revisao revisao;
	private List<Revisao> revisoes;
	private Pessoa aluno;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
		pessoaService.toString(); //Para evitar do codacy reclamar
		trilhaService.toString(); //Para evitar do codacy reclamar
		
		evento = new Evento();
		evento.setId(3L);
		
		evento.setPrazoRevisaoInicial(new GregorianCalendar(2015, 9, 5).getTime());
		evento.setPrazoRevisaoFinal(new GregorianCalendar(2015, 9, 10).getTime());
		
		aluno = new Pessoa();
		aluno.setCpf("00000");
		aluno.setId(1L);
		aluno.setEmail("aluno@gmail.com");
		
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn(aluno.getCpf());
		SecurityContextHolder.setContext(context);
		
		
	}
	
	@Dado("^que o aluno deseja ver a revisão de seu trabalho$")
	public void existeAluno() throws Throwable{
		trabalho = new Trabalho();
		trabalho.setTitulo("Trabalho");
		trabalho.setId(5L);
		trabalho.setParticipacoes(new ArrayList<>());
		trabalho.setEvento(evento);
		trabalho.setAutores(aluno, new ArrayList<Pessoa>());
		
		
		
		revisoes = new ArrayList<Revisao>();
		
		trabalho.setRevisoes(revisoes);
		
	}
	
	@Quando("^não há revisões$")
	public void naoExisteRevisao() throws Throwable{
		when(trabalhoService.getTrabalhoById(trabalho.getId())).thenReturn(trabalho);
		when(revisaoService.getRevisaoByTrabalho(trabalho)).thenReturn(revisoes);
		when(pessoaService.getByCpf(aluno.getCpf())).thenReturn(aluno);
		when(PessoaLogadaUtil.pessoaLogada()).thenReturn(aluno);
		
		action = mockMvc
				.perform(get("/autor/revisao/trabalho/{trabalhoId}",
						trabalho.getId().toString()));
	}
	
	@Então("^deve ver uma mensagem de erro de revisão inexistente$")
	public void mensagemErro() throws Throwable{
		action.andExpect(redirectedUrl("/autor/listarTrabalhos/" + evento.getId()))
		 .andExpect(flash().attribute("REVISAO_INEXISTENTE", messageService.getMessage("REVISAO_INEXISTENTE")));
	}
	
	@Quando("^há uma ou mais revisões$")
	public void existeRevisao() throws Throwable{

		revisao = new Revisao();
		revisao.setId(10L);
		revisao.setConteudo("{originalidade:FRACO}");
		
		revisoes = new ArrayList<Revisao>();
		revisoes.add(revisao);
		
		revisao.setId(11L);
		revisoes.add(revisao);
		
		trabalho.setRevisoes(revisoes);
		
		when(trabalhoService.getTrabalhoById(trabalho.getId())).thenReturn(trabalho);
		when(revisaoService.getRevisaoByTrabalho(trabalho)).thenReturn(revisoes);
		when(pessoaService.getByCpf(aluno.getCpf())).thenReturn(aluno);
		when(PessoaLogadaUtil.pessoaLogada()).thenReturn(aluno);
		
		action = mockMvc
				.perform(get("/autor/revisao/trabalho/{trabalhoId}",
						trabalho.getId().toString()));
	}
	
	@Então("^deve obter acesso as revisões$")
	public void sucesso() throws Throwable{
		action.andExpect(view().name(Constants.TEMPLATE_REVISAO_AUTOR));
	}

}
