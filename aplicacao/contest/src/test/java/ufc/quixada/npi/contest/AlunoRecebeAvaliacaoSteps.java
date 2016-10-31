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
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.model.Evento;
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
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
		pessoaService.toString(); //Para evitar do codacy reclamar
		trilhaService.toString(); //Para evitar do codacy reclamar
		
		evento = new Evento();
		evento.setId(3L);
		
	}
	
	@Dado("^que o aluno deseja ver a revisão de seu trabalho$")
	public void existeAluno() throws Throwable{
		trabalho = new Trabalho();
		trabalho.setTitulo("Trabalho");
		trabalho.setId(5L);
		trabalho.setParticipacoes(new ArrayList<>());
		trabalho.setEvento(evento);
		
		revisao = new Revisao();
		revisao.setId(10L);
		revisao.setConteudo("conteudo");
		
		revisoes = new ArrayList<Revisao>();
		revisoes.add(revisao);
		
		revisao.setId(11L);
		revisao.setConteudo("conteudo");
		revisoes.add(revisao);
		
	}
	
	@Quando("^tenta ver a revisão em período de submissão inicial$")
	public void revisaoEmPeriodoInicial() throws Throwable{
		evento.setPrazoSubmissaoInicial(new GregorianCalendar(2016, 9, 1).getTime());
		evento.setPrazoSubmissaoFinal(new GregorianCalendar(2017, 11, 30).getTime());
		
		evento.setPrazoRevisaoInicial(new GregorianCalendar(2017, 11, 27).getTime());
		evento.setPrazoRevisaoFinal(new GregorianCalendar(2017, 11, 28).getTime());
		
		when(trabalhoService.getTrabalhoById(trabalho.getId())).thenReturn(trabalho);
		when(revisaoService.getRevisaoByTrabalho(trabalho)).thenReturn(revisoes);
		action = mockMvc
				.perform(get("/autor/revisao")
						.param("trabalhoId", trabalho.getId().toString())
				);
	}
	
	@Então("^deve obter acesso a revisão$")
	public void obtemAcesso() throws Throwable{
		action.andExpect(view().name(Constants.TEMPLATE_REVISAO_AUTOR));
	}
	
	@Quando("^tenta enviar em período de revisão$")
	public void revisaoEmPeriodoRevisaol() throws Throwable{
		evento.setPrazoSubmissaoInicial(new GregorianCalendar(2016, 9, 1).getTime());
		evento.setPrazoSubmissaoFinal(new GregorianCalendar(2016, 10, 31).getTime());
		
		evento.setPrazoRevisaoInicial(new GregorianCalendar(2016, 10, 20).getTime());
		evento.setPrazoRevisaoFinal(new GregorianCalendar(2016, 10, 28).getTime());
		
		when(trabalhoService.getTrabalhoById(trabalho.getId())).thenReturn(trabalho);
		when(revisaoService.getRevisaoByTrabalho(trabalho)).thenReturn(revisoes);

		action = mockMvc
				.perform(get("/autor/revisao")
						.param("trabalhoId", trabalho.getId().toString())
				);
	}
	
	@Então("^deve ver uma mensagem de erro de revisão inexistente$")
	public void mensagemErro() throws Throwable{
		action.andExpect(redirectedUrl("/autor/listarTrabalhos/" + evento.getId()))
		 .andExpect(flash().attribute("REVISAO_INEXISTENTE", messageService.getMessage("REVISAO_INEXISTENTE")));
	}
	
	@Quando("^tenta ver a revisão em período de submissão final$")
	public void revisaoEmPeriodoFinal() throws Throwable{
		evento.setPrazoSubmissaoInicial(new GregorianCalendar(2016, 10, 1).getTime());
		evento.setPrazoSubmissaoFinal(new GregorianCalendar(2016, 10, 30).getTime());
		
		evento.setPrazoRevisaoInicial(new GregorianCalendar(2016, 10, 5).getTime());
		evento.setPrazoRevisaoFinal(new GregorianCalendar(2016, 10, 10).getTime());
		
		
		when(trabalhoService.getTrabalhoById(trabalho.getId())).thenReturn(trabalho);
		when(revisaoService.getRevisaoByTrabalho(trabalho)).thenReturn(revisoes);

		action = mockMvc
				.perform(get("/autor/revisao")
						.param("trabalhoId", trabalho.getId().toString())
				);
	}

}
