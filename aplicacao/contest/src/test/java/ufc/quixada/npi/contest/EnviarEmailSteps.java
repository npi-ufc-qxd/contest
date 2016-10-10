package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import ufc.quixada.npi.contest.service.ConvidaPessoaEmailService;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.util.Constants;

public class EnviarEmailSteps {
	
	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	@Mock
	private EventoService eventoService;
	@Mock
	private ConvidaPessoaEmailService emailService;
	@Mock
	private MessageService messageService;
	@Mock
	private PessoaService pessoaService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private EstadoEvento ativo_estado = EstadoEvento.ATIVO;
	private EstadoEvento inativo_estado = EstadoEvento.ATIVO;
	private static Long PESSOA_ID = (long) 1;
	private Pessoa pessoa;
	private String papelConvidado;

	


	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		evento = new Evento();
		evento.setId((long) 1);
		evento.setNome("NomeEvento");
		evento.setEstado(ativo_estado);
		VisibilidadeEvento visibilidade = VisibilidadeEvento.PUBLICO;
		evento.setVisibilidade(visibilidade);
		
		pessoa = new Pessoa();
		pessoa.setId(PESSOA_ID);
		pessoa.setCpf("92995454304");
		pessoa.setEmail("manuelac@npi.com");
		pessoa.setNome("Manuela Cardoso Fernandes");
		
	}
	
	
	/* O organizador convida pessoas para participarem de um evento ativo*/
	@Dado("^que existe um evento ativo$")
    public void casoTesteDadoCenario1() throws Throwable {
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		action = mockMvc.perform(get("/eventoOrganizador/convidar/{id}", evento.getId()))
				.andExpect(view().name("organizador/org_convidar_pessoas"));
    }
	@E("^que existe um organizador$")
	public void casoTesteECenario1(){
		when(pessoaService.get(PESSOA_ID)).thenReturn(pessoa);
	}
	@E("^que o organizador especifica o papel (.*) do convidado$")
	public void casoTesteE2Cenario1(String papel){
		papelConvidado = papel;
	}
	@Quando("^o organizador convida a pessoa com nome (.*) e email (.*) para participar do evento$")
    public void casoTesteQuandoCenario1(String nomeConvidado, String enderecoEmail) throws Throwable {
		when(emailService.send(Constants.FORMATO_EMAIL_ORGANIZADOR)).thenReturn(true);
		action = mockMvc
				.perform(post("/eventoOrganizador/convidar")
				.param("nomeEvento", evento.getNome())
				.param("nomeConvidado", nomeConvidado)
				.param("funcao", papelConvidado)
				.param("enderecoDestinatario", enderecoEmail));
		
    }
	@Então("^um convite por email é enviado para a pessoa$")
    public void casoTesteEntao1Cenario1() throws Throwable {
		action.andExpect(model().attribute("erro", messageService.getMessage("ERRO_ENVIO_EMAIL")));
    }
	
	
	/*O organizador enviar convite para email com formato inválido*/
	
	@Dado("^que existe um evento com estado ativo$")
    public void casoTesteDadoCenario2() throws Throwable {
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		action = mockMvc.perform(get("/eventoOrganizador/convidar/{id}", evento.getId()))
				.andExpect(view().name("organizador/org_convidar_pessoas"));
    }
	@E("^que existe um organizador cadastrado$")
	public void casoTesteECenario2(){
		when(pessoaService.get(PESSOA_ID)).thenReturn(pessoa);
	}
	@E("^que o organizador adiciona o papel de (.*) para o convidado$")
	public void casoTesteE2Cenario2(String papel){
		papelConvidado = papel;
	}
	@Quando("^o organizador tenta convidar uma pessoa com nome (.*) o email invalido (.*)$")
    public void casoTesteQuando(String nomeConvidado, String enderecoEmail) throws Throwable {
		when(emailService.send(Constants.FORMATO_EMAIL_ORGANIZADOR)).thenReturn(false);
		action = mockMvc
				.perform(post("/eventoOrganizador/convidar")
				.param("nomeEvento", evento.getNome())
				.param("nomeConvidado", nomeConvidado)
				.param("funcao", papelConvidado)
				.param("enderecoDestinatario", enderecoEmail)
				);
    }
	@Então("^uma mensagem de erro (.*) de impedimento é retornada$")
    public void casoTesteEntao1Cenario2(String mensagemErro) throws Throwable {
		action.andExpect(model().attribute("erro", messageService.getMessage(mensagemErro)));
    }
	
/*	O organizador convida pessoas para participarem de um evento inativo*/	
	
	@Dado("^que existe um evento inativo$")
    public void casoTesteDadoCenario3() throws Throwable {
		when(eventoService.buscarEventoPorId(evento.getId())).thenReturn(evento);
		evento.setEstado(inativo_estado);
		action = mockMvc.perform(get("/eventoOrganizador/convidar/{id}", evento.getId()))
				.andExpect(view().name("organizador/org_convidar_pessoas"));
    }
	@Então("^a mensagem de (.*) de impedimento é retornada para o organizador$")
    public void casoTesteEntaoCenario3(String msgErro) throws Throwable {
		action.andExpect(model().attribute("organizadorError", messageService.getMessage(msgErro)));
    }
}