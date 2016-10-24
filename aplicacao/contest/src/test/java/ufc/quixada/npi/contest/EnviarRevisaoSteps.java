package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.StorageService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.validator.TrabalhoValidator;

public class EnviarRevisaoSteps {
	
	private static final String EMAIL_PARTICIPANTES = "participacoes[0].pessoa.email";
	private static final String NOME_PARTICIPANTES = "participacoes[0].pessoa.nome";
	private static final String PAGINA_AUTOR_MEUS_TRABALHOS = "/autor/meusTrabalhos";
	private static final String PAGINA_AUTOR_ENVIAR_TRABALHO_FORM_ID = "/autor/enviarTrabalhoForm/{id}";
	private static final String PAGINA_REVISOR_REVISAR_FORM = "/revisor/{id}/{id}/revisar";
	private static final String EVENTO_ID = "eventoId";
	private static final String TRABALHO_ID = "idTrabalho";
	private static final String FOMATACAO = "formatacao";
	private static final String ORIGINALIDADE = "originalidade";
	private static final String MERITO = "merito";
	private static final String CLAREZA = "clareza";
	private static final String QUALIDADE = "qualidade";
	private static final String RELEVANCIA = "relevancia";
	private static final String AUTO_AVALIACAO = "auto-avaliacao";
	private static final String AVALIACAO_GERAL = "avaliacao-geral";
	private static final String COMENTARIOS = "comentarios";
	private static final String INDICAR = "indicar";

	
	
	private static final String TEMPLATE_AUTOR_AUTOR_ENVIAR_TRABALHO_FORM = "autor/autor_enviar_trabalho_form";
	@InjectMocks
	private AutorController autorController;
	@Mock
	private EventoService eventoService;
	@Mock
	private TrabalhoValidator trabalhoValidator;
	@Mock
	private PessoaService pessoaService;
	@Mock
	private ParticipacaoTrabalhoService participacaoTrabalhoService;
	@Mock
	private StorageService storageService;
	@Mock
	private MessageService messageService;
	@Mock
	private SubmissaoService submissaoService;
	@Mock
	private TrilhaService trilhaService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private Pessoa pessoa;
	private Trabalho trabalho;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
		evento = new Evento();
		trabalho = new Trabalho();
		pessoa = new Pessoa();
		
		evento.setId(1L);
		Calendar data1 = Calendar.getInstance();
		data1.set(2016, Calendar.SEPTEMBER, 30);
		
		Calendar data2 = Calendar.getInstance();
		data2.set(2016, Calendar.SEPTEMBER, 1);
		
		evento.setPrazoSubmissaoFinal(data1.getTime());
		evento.setPrazoSubmissaoInicial(data2.getTime());
		
		pessoa.setCpf("123");
		pessoa.setEmail("teste@tes.com");
		
		trabalho.setEvento(evento);
	}
	
	
	@Quando("^ele preenche os campos corretamente$")
	public void preencherOsCamposSemErro() throws Exception{
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(pessoaService.getByEmail(pessoa.getEmail())).thenReturn(pessoa);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload(PAGINA_REVISOR_REVISAR_FORM)
                        .param(TRABALHO_ID, "1")
                        .param(FOMATACAO, "Teste")
                        .param(NOME_PARTICIPANTES, "joao")
                        .param(EMAIL_PARTICIPANTES , "joao@gmail.com"));
                      //  .param(TRILHA_ID, "3"));
	}
	
	

	
}
