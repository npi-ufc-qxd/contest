package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.StorageService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.validator.TrabalhoValidator;

public class AlunoSubmeteVersaoFinalSteps {

	private static final String TRILHA_ID = "trilhaId";
	private static final String PAGINA_AUTOR_MEUS_TRABALHOS = "/autor/meusTrabalhos";
	private static final String PAGINA_AUTOR_ENVIAR_TRABALHO_FORM_ID = "/autor/enviarTrabalhoForm/{id}";
	private static final String CAMINHO_ARQUIVO_VALIDO = "/home/allef.lobo/Documentos/certificado.pdf";
	private static final String CAMINHO_ARQUIVO_INVALIDO = "/home/lucas.vieira/Downloads/pgadmin.log";
	private static final String PAGINA_AUTOR_ENVIAR_TRABALHO_FORM = "/autor/enviarTrabalhoForm";
	private static final String EMAIL_ORIENTADOR = "emailOrientador";
	private static final String NOME_ORIENTADOR = "nomeOrientador";
	private static final String TITULO = "titulo";
	private static final String EVENTO_ID = "eventoId";
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
	private Submissao submissao;
	private Trilha trilha;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
		pessoaService.toString(); //Para evitar do codacy reclamar
		trilhaService.toString(); //Para evitar do codacy reclamar
		
		evento = new Evento();
		pessoa = new Pessoa();
		submissao = new Submissao();
		trilha = new Trilha();
	}
	
	@Dado("^que existe um aluno$")
	public void existeAluno() throws Throwable{
		pessoa.setCpf("123");
		pessoa.setEmail("teste@tes.com");
	}
	
	@E("^que possui um trabalho$")
	public void trabalhoFoiEnviado() throws Throwable{
		evento.setId(1L);
		evento.setPrazoSubmissaoFinal(new Date("30/09/2017"));
		evento.setPrazoSubmissaoInicial(new Date("01/09/2016"));
		
		trilha.setId(3L);
	}
	
	@Quando("^o trabalho foi avaliado pelos revisores$")
	public void trabalhoFoiAvaliado() throws Throwable{
		//TODO
	}
	
	@Então("^o trabalho poderá ser reenviado como submissão final$")
	public void trabalhoSubmissaoFinal() throws Throwable{
		//TODO
	}
	
	@Quando("^está dentro do prazo de submissão de envio do evento$")
	public void dentroDoPrazoDeSubmissao() throws Throwable{
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(pessoaService.getByEmail(pessoa.getEmail())).thenReturn(pessoa);
		when(trilhaService.get(trilha.getId(), evento.getId())).thenReturn(trilha);
		
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		SecurityContextHolder.setContext(context);
		
		when(submissaoService.adicionarOuEditar(submissao)).thenReturn(true);
		
		
		FileInputStream fi2 = new FileInputStream(new File(CAMINHO_ARQUIVO_VALIDO));
		final MockMultipartFile  multipartFile = new MockMultipartFile("file", "certificado.pdf","multipart/form-data",fi2);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM)
                        .file(multipartFile)
                        .param(EVENTO_ID, "1")
                        .param(TITULO, "Teste")
                        .param(NOME_ORIENTADOR, "joao")
                        .param(EMAIL_ORIENTADOR , "joao@gmail.com")
                        .param(TRILHA_ID, "3"));
	}
	
	@Então("^o trabalho poderá ser reenviado$")
	public void trabalhoReenviado() throws Throwable{
		action.andExpect(redirectedUrl(PAGINA_AUTOR_MEUS_TRABALHOS))
		  .andExpect(status().isFound());
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
