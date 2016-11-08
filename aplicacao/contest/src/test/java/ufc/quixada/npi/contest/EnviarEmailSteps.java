package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import cucumber.api.java.gl.E;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EnviarEmailService;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;

public class EnviarEmailSteps {
	
	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	@Mock
	private EventoService eventoService;
	@Mock
	private EnviarEmailService emailService;
	@Mock
	private MessageService messageService;
	@Mock
	private PessoaService pessoaService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private Pessoa pessoa;
	private String papelConvidado;
	private Long EVENTO_ID = 1L;
	private static final String URL_DETALHES_EVENTO_ORGANIZADOR = "/eventoOrganizador/evento/";

	@Before
	public void setup() {
		
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		evento = new Evento();
		evento.setId(Long.valueOf(1));
		evento.setNome("NomeEvento");
		evento.setEstado(EstadoEvento.ATIVO);
		VisibilidadeEvento visibilidade = VisibilidadeEvento.PUBLICO;
		evento.setVisibilidade(visibilidade);
		
		pessoa = new Pessoa();
		pessoa.setId(Long.valueOf(1));
		pessoa.setCpf("92995454304");
		pessoa.setEmail("manuelac@npi.com");
		pessoa.setNome("Manuela Cardoso Fernandes");
		pessoa.setPapelLdap("DOCENTE");
		
	}
	/* O organizador convida pessoas para participarem de um evento ativo*/
	@Dado("^que existe um evento ativo$")
    public void casoTesteDadoCenario1() throws Throwable {
		when(eventoService.buscarEventoPorId(EVENTO_ID)).thenReturn(evento);
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		SecurityContextHolder.setContext(context);
		when(eventoControllerOrganizador.getOrganizadorLogado()).thenReturn(pessoa);
		
		action = mockMvc.perform(get("/eventoOrganizador/convidar/{id}", EVENTO_ID))
				.andExpect(view().name("organizador/org_convidar_pessoas"));
    }
	@E("^que existe um organizador$")
	public void casoTesteECenario1(){
		// já verificado no step anterior
	}
	@E("^que o organizador especifica o papel (.*) do convidado$")
	public void casoTesteE2Cenario1(String papel){
		papelConvidado = papel;
	}
	@Quando("^o organizador convida a pessoa com nome (.*) e email (.*) para participar do evento$")
    public void casoTesteQuandoCenario1(String nomeConvidado, String enderecoEmail) throws Throwable {
		when(emailService.enviarEmail()).thenReturn(true);
		when(eventoService.buscarEventoPorId(Long.valueOf(EVENTO_ID))).thenReturn(evento);
		evento.setEstado(EstadoEvento.ATIVO);
		action = mockMvc
				.perform(post("/eventoOrganizador/convidar")
				.param("eventoId", evento.getId().toString())
				.param("nomeConvidado", nomeConvidado)
				.param("funcao", papelConvidado)
				.param("email", enderecoEmail));
		
    }
	@Então("^um convite por email é enviado para a pessoa$")
    public void casoTesteEntao1Cenario1() throws Throwable {
		action.andExpect(redirectedUrl(URL_DETALHES_EVENTO_ORGANIZADOR+EVENTO_ID))
		.andExpect(status().isFound());    
	}
	/*O organizador enviar convite para email com formato inválido*/
	@Dado("^que existe um evento com estado ativo$")
    public void casoTesteDadoCenario2() throws Throwable {
		when(eventoService.buscarEventoPorId(EVENTO_ID)).thenReturn(evento);
		evento.setEstado(EstadoEvento.ATIVO);
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		SecurityContextHolder.setContext(context);
		when(eventoControllerOrganizador.getOrganizadorLogado()).thenReturn(pessoa);
		
		action = mockMvc.perform(get("/eventoOrganizador/convidar/{id}", EVENTO_ID))
				.andExpect(view().name("organizador/org_convidar_pessoas"));
    }
	@E("^que existe um organizador cadastrado$")
	public void casoTesteECenario2(){
		// já verificado no step anterior
	}
	@E("^que o organizador adiciona o papel de (.*) para o convidado$")
	public void casoTesteE2Cenario2(String papel){
		papelConvidado = papel;
	}
	@Quando("^o organizador tenta convidar uma pessoa com nome (.*) o email invalido (.*)$")
    public void casoTesteQuando(String nomeConvidado, String enderecoEmail) throws Throwable {
		when(emailService.enviarEmail()).thenReturn(false);
		action = mockMvc
				.perform(post("/eventoOrganizador/convidar")
				.param("eventoId", evento.getId().toString())
				.param("nomeConvidado", nomeConvidado)
				.param("funcao", papelConvidado)
				.param("email", enderecoEmail)
				);
    }
	@Então("^uma mensagem de erro de impedimento é retornada$")
    public void casoTesteEntao1Cenario2() throws Throwable {
		action.andExpect(redirectedUrl(URL_DETALHES_EVENTO_ORGANIZADOR+EVENTO_ID))
		      .andExpect(flash().attribute("organizadorError", messageService.getMessage("ERRO_ENVIO_EMAIL")));
    }
	/*O organizador convida pessoas para participarem de um evento inativo*/	
	
	@Dado("^que existe um evento inativo$")
    public void casoTesteDadoCenario3() throws Throwable {
		evento.setEstado(EstadoEvento.INATIVO);
		when(eventoService.buscarEventoPorId(Long.valueOf(EVENTO_ID))).thenReturn(evento);

		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("123");
		SecurityContextHolder.setContext(context);
		
		when(eventoControllerOrganizador.getOrganizadorLogado()).thenReturn(pessoa);

		action = mockMvc.perform(get("/eventoOrganizador/convidar/{id}", EVENTO_ID))
				        .andExpect(redirectedUrl( "/eventoOrganizador/evento"+EVENTO_ID));
    }
	@Então("^a mensagem de impedimento é retornada para o organizador$")
    public void casoTesteEntaoCenario3() throws Throwable {
		action.andExpect(redirectedUrl( "/eventoOrganizador/evento"+EVENTO_ID))
			  .andExpect(flash().attribute("organizadorError", messageService.getMessage("CONVIDAR_EVENTO_INATIVO")));
    }
}