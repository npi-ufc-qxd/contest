package ufc.quixada.npi.contest;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;

import br.ufc.quixada.npi.ldap.service.UsuarioService;
import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.StorageService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.validator.TrabalhoValidator;

public class EnviarTrabalhoSteps {
	
	private static final String EMAIL_PARTICIPANTES = "participacoes[0].pessoa.email";
	private static final String NOME_PARTICIPANTES = "participacoes[0].pessoa.nome";
	private static final String TRILHA_ID = "trilhaId";
	private static final String PAGINA_AUTOR_MEUS_TRABALHOS = "/autor/meusTrabalhos";
	private static final String PAGINA_AUTOR_ENVIAR_TRABALHO_FORM = "/autor/enviarTrabalhoForm";
	private static final String PAGINA_AUTOR_ENVIAR_TRABALHO_FORM_ID = "/autor/enviarTrabalhoForm/{id}";
	private static final String TITULO = "titulo";
	private static final String EVENTO_ID = "eventoId";
	private static final byte[] CONTEUDO = "Ola Mundo".getBytes();
	
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
	private StorageService storageService;
	@Mock
	private TrabalhoService trabalhoService;
	@Mock
	private UsuarioService usuarioService;
	
	
	@Mock
	private TrabalhoValidator trabalhoValidator;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private Pessoa pessoa, coautor;
	private ParticipacaoEvento participacao;
	private Submissao submissao;
	private Trilha trilha;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
		evento = new Evento();
		pessoa = new Pessoa();
		coautor = new Pessoa();
		submissao = new Submissao();
		trilha = new Trilha();
		participacao = new ParticipacaoEvento();
		
		evento.setId(1L);
		Calendar dataInicialSubmissao = Calendar.getInstance();
		dataInicialSubmissao.add(Calendar.DAY_OF_MONTH, -1);
		
		Calendar dataInicialRevisao = Calendar.getInstance();
		dataInicialRevisao.add(Calendar.DAY_OF_MONTH, 1);
		
		Calendar dataFinalRevisao = Calendar.getInstance();
		dataFinalRevisao.add(Calendar.DAY_OF_MONTH, 5);
		
		Calendar dataFinalSubmissao = Calendar.getInstance();
		dataFinalSubmissao.add(Calendar.DAY_OF_MONTH, 10);
		
		Date dataInicial = dataInicialSubmissao.getTime();
		Date dataFinal = dataFinalSubmissao.getTime();
		Date dataRevisaoInicial = dataInicialRevisao.getTime();
		Date dataRevisaoFinal = dataFinalRevisao.getTime();
		
		evento.setPrazoSubmissaoInicial(dataInicial);
		evento.setPrazoSubmissaoFinal(dataFinal);
		evento.setPrazoRevisaoInicial(dataRevisaoInicial);
		evento.setPrazoRevisaoFinal(dataRevisaoFinal);
		evento.setDescricao("asdasd");
		evento.setEstado(EstadoEvento.ATIVO);
		
		List<ParticipacaoEvento> participacoes = new ArrayList<>();
		
		trilha.setId(3L);
		trilha.setEvento(evento);
		trilha.setNome("Principal");
		
		pessoa.setCpf("123");
		pessoa.setEmail("teste@tes.com");
		pessoa.setId(1L);
		
		coautor.setCpf("000");
		coautor.setEmail("coautor@gmail.com");
		
		participacao.setId(1L);
		participacao.setPessoa(pessoa);
		participacao.setEvento(evento);
		
		participacoes.add(participacao);
		evento.setParticipacoes(participacoes);
	}
	
	@Dado("^que o autor seleciona um evento que ele participa e o prazo esta vigente$")
	public void autorSelecionaEvento() throws Throwable{
		List<Trilha> trilhas = new ArrayList<>();
		trilhas.add(trilha);
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(trilhaService.buscarTrilhas(1L)).thenReturn(trilhas);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getPrincipal()).thenReturn(pessoa);
		
		SecurityContextHolder.setContext(context);
		action = mockMvc.perform(
				get(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM_ID, "1"));
	}
	
	@Quando("^ele preenche os campos corretamente e escolhe um arquivo .pdf$")
	public void preencherOsCamposSemErro() throws Exception{
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(trilhaService.get(trilha.getId(), evento.getId())).thenReturn(trilha);
		when(pessoaService.getByEmail(coautor.getEmail())).thenReturn(coautor);
		doNothing().when(trabalhoService).notificarAutoresEnvioTrabalho(evento, new Trabalho());
		
		when(submissaoService.adicionarOuEditar(submissao)).thenReturn(true);
		MultipartFile file = new MockMultipartFile("file", "arquivo.pdf","text/plain",CONTEUDO);
		String nomeDoArquivo = new StringBuilder("CONT-").append(evento.getId()).toString();
		when(storageService.store(file, nomeDoArquivo)).thenReturn("caminho/certo");
		
		MockMultipartFile  multipartFile = new MockMultipartFile("file", "arquivo.pdf","text/plain",CONTEUDO);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM)
                        .file(multipartFile)
                        .param(EVENTO_ID, "1")
                        .param(TITULO, "Teste")
                        .param(NOME_PARTICIPANTES, coautor.getNome())
                        .param(EMAIL_PARTICIPANTES , coautor.getEmail())
                        .param(TRILHA_ID, "3"));
	}
	@Então("^o autor deve ser redirecionado para a página meusTrabalhos com uma mensagem de sucesso$")
	public void mensagemDeSucesso() throws Exception{
		action.andExpect(redirectedUrl(PAGINA_AUTOR_MEUS_TRABALHOS))
			  .andExpect(status().isFound());
	}
	
	@Quando("^ele não preenche o campo titulo$")
	public void campoTituloNaoPreenchido() throws Exception{
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		
		MockMultipartFile  multipartFile = new MockMultipartFile("file", "arquivo.pdf","text/plain",CONTEUDO);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM)
                        .file(multipartFile)
                        .param(EVENTO_ID, "1")
                        .param(TITULO, "")
                        .param(NOME_PARTICIPANTES, coautor.getNome())
                        .param(EMAIL_PARTICIPANTES , coautor.getEmail())
                        .param(TRILHA_ID, "3"));
	}
	@Então("^deve ser mostrado uma mensagem de erro dizendo que o titulo está em branco$")
	public void mostrarErroNoCampoTitulo() throws Exception{
		action
		//.andExpect(model().attributeHasFieldErrors("trabalho", TITULO))
		      .andExpect(redirectedUrl(PAGINA_AUTOR_MEUS_TRABALHOS));
	}
	
	@Quando("^ele não preenche os campos nom do orientador e email do orientador$")
	public void camposNomeEEmailDoOrientadorEmBranco() throws Exception{
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		
		MockMultipartFile  multipartFile = new MockMultipartFile("file", "arquivo.pdf","text/plain",CONTEUDO);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM)
                        .file(multipartFile)
                        .param(EVENTO_ID, "1")
                        .param(TITULO, "Titulo")
                        .param(NOME_PARTICIPANTES, "")
                        .param(EMAIL_PARTICIPANTES , "")
                        .param(TRILHA_ID, "3"));
	}
	@Então("^deve ser mostrado uma mensagem de erro que há campos em branco$")
	public void camposEmBanco() throws Exception{
		action.andExpect(status().isFound())
		      .andExpect(redirectedUrl(PAGINA_AUTOR_MEUS_TRABALHOS));
		      //.andExpect(flash().attribute("camposVazios", messageService.getMessage("CAMPOS_VAZIOS")));
	}
	
	@Quando("^o autor escolhe um arquivo em um formato diferete de .pdf$")
	public void autoEscolheArquivoInvalido() throws Exception{
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(pessoaService.getByEmail(pessoa.getEmail())).thenReturn(pessoa);
		
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		SecurityContextHolder.setContext(context);
		
		when(submissaoService.adicionarOuEditar(submissao)).thenReturn(true);
		
		
		MockMultipartFile  multipartFile = new MockMultipartFile("file", "pgadmin.log","text/plain",CONTEUDO);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM)
                        .file(multipartFile)
                        .param(EVENTO_ID, "1")
                        .param(TITULO, "Teste")
                        .param(NOME_PARTICIPANTES, "joao")
                        .param(EMAIL_PARTICIPANTES , "joao@gmail.com")
                        .param(TRILHA_ID, "3"));
		
	}
	@Então("^deve ser mostrado uma mensagem de erro que o formato do arquivo é invalido$")
	public void mensagemDeErroArquivoInvalido() throws Exception{
		action.andExpect(status().isFound())
	      .andExpect(redirectedUrl(PAGINA_AUTOR_MEUS_TRABALHOS))
	      .andExpect(flash().attribute("erro", messageService.getMessage("FORMATO_ARQUIVO_INVALIDO")));
	}
	
	@Quando("^ele muda o id do evento para um inexistente$")
	public void eventoInexistente() throws Exception{
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(null);
		
		MockMultipartFile  multipartFile = new MockMultipartFile("file", "arquivo.pdf","text/plain",CONTEUDO);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM)
                        .file(multipartFile)
                        .param(EVENTO_ID, "155")
                        .param(TITULO, "")
                        .param(NOME_PARTICIPANTES, "joao")
                        .param(EMAIL_PARTICIPANTES , "joao@gmail.com")
                        .param(TRILHA_ID, "3"));
		
	}
	@Então("^deve ser mostrado uma mensagem de erro de evento não existe$")
	public void eventoNaoExiste() throws Exception{
		action.andExpect(redirectedUrl(PAGINA_AUTOR_MEUS_TRABALHOS))
		      .andExpect(status().is3xxRedirection());
	}
	
}