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
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.service.ConvidaPessoaEmailService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;

public class EnviarEmailSteps {
	
	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	
	@Mock
	private PessoaService pessoaService;
	@Mock
	private ConvidaPessoaEmailService emailService;
	@Mock
	private MessageService messageService;
	
	private MockMvc mockMvc;
	private ResultActions action;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
	}
	
	
	@Dado("^o organizador deseja convidar um participante para evento.$")
    public void casoTesteDado() throws Throwable {
		action = mockMvc.perform(get("/eventoOrganizador/convidar/{nome}", "EventoTeste"))
				.andExpect(view().name("organizador/org_convidar_pessoas"));
		
    }
	
	
	/* Cenário: Convite para evento enviado com sucesso */
	@Quando("^selecionar o evento (.*) com nome do convidado (.*) Funcao (.*) e email (.*)$")
    public void casoTesteQuando(String nomeEvento, String nomeConvidado, String funcao, String enderecoEmail) throws Throwable {
		when(emailService.send()).thenReturn(true);
		action = mockMvc
				.perform(post("/eventoOrganizador/convidar")
				.param("nomeEvento", nomeEvento)
				.param("nomeConvidado", nomeConvidado)
				.param("funcao", funcao)
				.param("enderecoDestinatario", enderecoEmail));
		
    }
	@Então("^um email deve ser enviado com sucesso$")
    public void casoTesteEntao() throws Throwable {
		action.andExpect(view().name("organizador/org_convidar_pessoas"));
    }
	
	
	/* Cenário: Enviar email sem preencher o campo Email do Convidado */
	@Quando("^informar campo email vazio para evento (.*) com nome do convidado (.*) Funcao (.*)$")
    public void casoTesteQuando2(String nomeEvento, String nomeConvidado, String funcao) throws Throwable {
		when(messageService.getMessage("ERRO_ENVIO_EMAIL"))
		.thenReturn("Erro ao enviar email");
		
		action = mockMvc
				.perform(post("/eventoOrganizador/convidar")
				.param("nomeEvento", nomeEvento)
				.param("nomeConvidado", nomeConvidado)		
				.param("funcao", funcao)		
				);
	}
	@Então("^O email nao deve ser enviado para o participante$")
    public void casoTesteEntao2() throws Throwable {
		action.andExpect(model().attribute("erro", messageService.getMessage("ERRO_ENVIO_EMAIL")));
    }
	/*  Cenário: Enviar convite para email com formato inválido */
	@Quando("^informar o nome (.*) e a Função (.*) e o email  exemplo (.*) para evento (.*)$")
    public void casoTesteQuando3(String nomeConvidado, String funcao, String enderecoDestinatario, String nomeEvento) throws Throwable {
		when(messageService.getMessage("ERRO_ENVIO_EMAIL"))
		.thenReturn("Erro ao enviar email");
		
		action = mockMvc
				.perform(post("/eventoOrganizador/convidar")
				.param("nomeEvento", nomeEvento)
				.param("nomeConvidado", nomeConvidado)		
				.param("funcao", funcao)
				.param("enderecoDestinatario", enderecoDestinatario)

				);
    }
	@Então("^O convite nao deve ser enviado$")
    public void casoTesteEntao3() throws Throwable {
		action.andExpect(model().attribute("erro", messageService.getMessage("ERRO_ENVIO_EMAIL")));
    }
}