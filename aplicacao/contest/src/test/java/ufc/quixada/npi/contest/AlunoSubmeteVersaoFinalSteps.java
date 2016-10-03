package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Entao;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;

public class AlunoSubmeteVersaoFinalSteps {

	private static final String EVENTO_ID = "2";
	private static final String TRILHA_ID = "3";

	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	
	@Mock
	private PessoaService pessoaService;
	
	@Mock
	private TrilhaService trilhaService;
	
	@Mock
	private TrabalhoService trabalhoService;

	@Mock
	private MessageService messageService;
	
	@Mock
	private EventoService eventoService;

	private MockMvc mockMvc;
	private ResultActions action;
	
	private Pessoa pessoa;
	private Evento evento;
	private Trabalho trabalho;
	private Trilha trilha;
	private Submissao submissao;
	private AutorController autorController;
	private Date data;
	
	@SuppressWarnings("deprecation")
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
		evento = new Evento();
		pessoa = new Pessoa();
		submissao = new Submissao();
		trilha = new Trilha();

		evento.setId(1L);
		evento.setPrazoSubmissaoFinal(new Date("30/09/2016"));
		evento.setPrazoSubmissaoInicial(new Date("01/09/2016"));

		trilha.setId(3L);
	}
	
	@Dado("^existe um aluno$")
	public void existeAluno() throws Throwable{
		pessoa.setCpf("123");
		pessoa.setEmail("teste@tes.com");
	}
	
	@E("^que possui um trabalho que foi enviado para avaliação$")
	public void trabalhoFoiEnviado() throws Throwable{
		trabalho = new Trabalho();
		trabalho.setEvento(evento);
	}
	
	@E("^o trabalho foi avaliado pelos revisores$")
	public void trabalhoFoiAvaliado() throws Throwable{
		//TODO
	}
	
	@Quando("^está dentro do prazo de submissão de envio do evento$")
	public void dentroDoPrazoDeSubmissao() throws Throwable{
		data = new Date("15/09/2016");
	}
	
	@Entao("^o trabalho poderá ser reenviado como submissão final$")
	public void trabalhoReenviado() throws Throwable{
		//TODO
	}
	
	@Quando("^está fora do prazo de submissão de envio do evento$")
	public void foraDoPrazoDeSubmissao() throws Throwable{
		//TODO
	}
	
	@Então("^o trabalho não poderá ser reenviado$")
	public void trabalhoNaoReenviado() throws Throwable{
		//TODO
	}

}
