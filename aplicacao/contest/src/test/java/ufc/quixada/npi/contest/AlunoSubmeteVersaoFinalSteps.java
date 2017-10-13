package ufc.quixada.npi.contest;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EnviarEmailService;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.StorageService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;

public class AlunoSubmeteVersaoFinalSteps {

	private static final String PAGINA_AUTOR_MEUS_TRABALHOS = "/autor/meusTrabalhos";
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
	private TrabalhoService trabalhoService;
	@Mock
	private StorageService storageService;
	
	

	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private Pessoa pessoa;
	private Submissao submissao;
	private Trabalho trabalho;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
		pessoaService.toString(); //Para evitar do codacy reclamar
		trilhaService.toString(); //Para evitar do codacy reclamar
		
		evento = new Evento();
		pessoa = new Pessoa();
		
		
		submissao = new Submissao();
		
		trabalho = new Trabalho();
		trabalho.setTitulo("Trabalho");
		trabalho.setId(5L);
		trabalho.setParticipacoes(new ArrayList<>());
		trabalho.setEvento(evento);
	}
	
	@Dado("^que existe um aluno$")
	public void existeAluno() throws Throwable{
		pessoa.setCpf("123");
		pessoa.setEmail("teste@tes.com");
		
	}
	
	@E("^que possui um trabalho$")
	public void trabalhoFoiEnviado() throws Throwable{
		evento.setId(1L);
		evento.setPrazoSubmissaoInicial(new GregorianCalendar(2016, 9, 1).getTime());
		evento.setPrazoSubmissaoFinal(new GregorianCalendar(2017, 11, 30).getTime());
		
		evento.setPrazoRevisaoInicial(new GregorianCalendar(2017, 11, 27).getTime());
		evento.setPrazoRevisaoFinal(new GregorianCalendar(2017, 11, 28).getTime());
		
		submissao.setId(6L);
		submissao.setTrabalho(trabalho);
	}
	
	@Quando("^está dentro do prazo de submissão de envio do evento$")
	public void dentroDoPrazoDeSubmissao() throws Throwable{
		when(trabalhoService.existeTrabalho(trabalho.getId())).thenReturn(true);
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
				
		when(submissaoService.adicionarOuEditar(submissao)).thenReturn(true);
		
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(trabalhoService.getTrabalhoById(trabalho.getId())).thenReturn(trabalho);
		when(submissaoService.getSubmissaoByTrabalho(trabalho)).thenReturn(submissao);
		doNothing().when(trabalhoService).notificarAutoresEnvioTrabalho(evento, trabalho);
		
		MockMultipartFile  multipartFile = new MockMultipartFile("file", "arquivo.pdf","text/plain",CONTEUDO);
        
		action = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/autor/reenviarTrabalho")
                        .file(multipartFile)
                        .param("trabalhoId", trabalho.getId().toString())
                        .param("eventoId", evento.getId().toString()));
	}
	
	@Então("^o trabalho poderá ser reenviado$")
	public void trabalhoReenviado() throws Throwable{
		action.andExpect(redirectedUrl(PAGINA_AUTOR_MEUS_TRABALHOS))
		  .andExpect(status().isFound());
	}
	
	@Quando("^está fora do prazo de submissão de envio do evento$")
	public void foraDoPrazoDeSubmissao() throws Throwable{
		evento.setPrazoSubmissaoInicial(new GregorianCalendar(2015, 9, 1).getTime());
		evento.setPrazoSubmissaoFinal(new GregorianCalendar(2015, 9, 20).getTime());
		
		evento.setPrazoRevisaoInicial(new GregorianCalendar(2015, 9, 5).getTime());
		evento.setPrazoRevisaoFinal(new GregorianCalendar(2015, 9, 10).getTime());
		
		when(trabalhoService.existeTrabalho(trabalho.getId())).thenReturn(true);
		when(eventoService.existeEvento(evento.getId())).thenReturn(true);
				
		when(submissaoService.adicionarOuEditar(submissao)).thenReturn(true);
		
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		when(trabalhoService.getTrabalhoById(trabalho.getId())).thenReturn(trabalho);
		when(submissaoService.getSubmissaoByTrabalho(trabalho)).thenReturn(submissao);
		
		MockMultipartFile  multipartFile = new MockMultipartFile("file", "arquivo.pdf","text/plain",CONTEUDO);

        action = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/autor/reenviarTrabalho")
                        .file(multipartFile)
                        .param("trabalhoId", trabalho.getId().toString())
                        .param("eventoId", evento.getId().toString()));
	}
	
	@Então("^o trabalho não poderá ser reenviado$")
	public void trabalhoNaoReenviado() throws Throwable{
		action.andExpect(status().isFound())
	      .andExpect(redirectedUrl("/autor/listarTrabalhos/" + evento.getId()))
	      .andExpect(flash().attribute("FORA_DO_PRAZO_SUBMISSAO", messageService.getMessage("FORA_DO_PRAZO_SUBMISSAO")));
	}

}
