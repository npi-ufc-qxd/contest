package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.StorageService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.validator.TrabalhoValidator;

public class EnviarTrabalhoSteps {
	
	private static final String EMAIL_PARTICIPANTES = "participacoes[0].pessoa.email";
	private static final String NOME_PARTICIPANTES = "participacoes[0].pessoa.nome";
	private static final String TRILHA_ID = "trilhaId";
	private static final String PAGINA_AUTOR_MEUS_TRABALHOS = "/autor/meusTrabalhos";
	private static final String PAGINA_AUTOR_ENVIAR_TRABALHO_FORM_ID = "/autor/enviarTrabalhoForm/{id}";
	private static final String PAGINA_AUTOR_ENVIAR_TRABALHO_FORM = "/autor/enviarTrabalhoForm";
	private static final String TITULO = "titulo";
	private static final String EVENTO_ID = "eventoId";
	private static final String TEMPLATE_AUTOR_AUTOR_ENVIAR_TRABALHO_FORM = "autor/autor_enviar_trabalho_form";
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
		evento = new Evento();
		pessoa = new Pessoa();
		submissao = new Submissao();
		trilha = new Trilha();
		
		evento.setId(1L);
		Calendar dataInicialSubmissao = Calendar.getInstance();
		dataInicialSubmissao.set(2016, Calendar.SEPTEMBER, 30);
		
		Calendar dataFinalSubmissao = Calendar.getInstance();
		dataFinalSubmissao.set(2017, Calendar.SEPTEMBER, 1);
		
		Date dataInicial = dataInicialSubmissao.getTime();
		Date dataFinal = dataFinalSubmissao.getTime();
		evento.setPrazoSubmissaoInicial(dataInicial);
		evento.setPrazoSubmissaoFinal(dataFinal);
		
		
		trilha.setId(3L);
		
		pessoa.setCpf("123");
		pessoa.setEmail("teste@tes.com");
	}
	
	@Dado("^que o autor seleciona um evento que ele participa$")
	public void autorSelecionaEvento() throws Throwable{
		List<Trilha> trilhas = new ArrayList<>();
		trilhas.add(trilha);
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(trilhaService.buscarTrilhas(1L)).thenReturn(trilhas);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		
		SecurityContextHolder.setContext(context);
		action = mockMvc.perform(
				get(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM_ID, "1"));
	}
	
	@Quando("^ele preenche os campos corretamente e escolhe um arquivo .pdf$")
	public void preencherOsCamposSemErro() throws Exception{
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(trilhaService.get(trilha.getId(), evento.getId())).thenReturn(trilha);
		when(pessoaService.getByEmail(pessoa.getEmail())).thenReturn(pessoa);
		
		when(submissaoService.adicionarOuEditar(submissao)).thenReturn(true);
		
		
		MockMultipartFile  multipartFile = new MockMultipartFile("file", "arquivo.pdf","text/plain",CONTEUDO);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM)
                        .file(multipartFile)
                        .param(EVENTO_ID, "1")
                        .param(TITULO, "Teste")
                        .param(NOME_PARTICIPANTES, "joao")
                        .param(EMAIL_PARTICIPANTES , "joao@gmail.com")
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
                        .param(NOME_PARTICIPANTES, "joao")
                        .param(EMAIL_PARTICIPANTES , "joao@gmail.com")
                        .param(TRILHA_ID, "3"));
	}
	@Então("^deve ser mostrado uma mensagem de erro dizendo que o titulo está em branco$")
	public void mostrarErroNoCampoTitulo() throws Exception{
		action.andExpect(model().attributeHasFieldErrors("trabalho", TITULO))
		      .andExpect(view().name(TEMPLATE_AUTOR_AUTOR_ENVIAR_TRABALHO_FORM));
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
		      .andExpect(redirectedUrl(PAGINA_AUTOR_MEUS_TRABALHOS))
		      .andExpect(flash().attribute("camposVazios", messageService.getMessage("CAMPOS_VAZIOS")));
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
	      .andExpect(redirectedUrl(PAGINA_AUTOR_ENVIAR_TRABALHO_FORM+"/1"))
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
		action.andExpect(model().hasErrors())
		      .andExpect(view().name(TEMPLATE_AUTOR_AUTOR_ENVIAR_TRABALHO_FORM))
		      .andExpect(status().isOk());
	}
	
}