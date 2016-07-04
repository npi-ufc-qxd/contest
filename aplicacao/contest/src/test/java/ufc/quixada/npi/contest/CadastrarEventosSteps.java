package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.gl.E;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.PapelEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class CadastrarEventosSteps {

	private static final String PESSOA_ID = "1";

	@InjectMocks
	private EventoController eventoController;

	@Mock
	private PessoaService pessoaService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;

	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa pessoa;
	private Evento evento;

	@cucumber.api.java.Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
	}

	@Dado("^o administrador deseja cadastrar um evento.$")
	public void administradorDesejaCadastrarUmEvento() throws Throwable {

	}

	@Quando("^informar o organizador (.*) e o evento com nome (.*) e descricao (.*)")
	public void casoTesteQuando(String organizador, String nomeEvento, String descricaoEvento) throws Throwable {

		pessoa = new Pessoa();
		pessoa.setNome(organizador);
		pessoa.setCpf("789287454457");
		pessoa.setEmail("a@a.com");

		evento = new Evento();
		evento.setNome(nomeEvento);
		evento.setDescricao(descricaoEvento);
		evento.setEstado(EstadoEvento.INATIVO);
		
		when(pessoaService.get(Integer.valueOf(PESSOA_ID))).thenReturn(pessoa);

		action = mockMvc
				.perform(post("/evento/adicionar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("descricao", descricaoEvento)
				.param("organizador", PESSOA_ID));
		
	}

	@Então("^o evento deve ser cadastrado com visibilidade privada e estado inativo.$")
	public void casoTesteEntao() throws Throwable {
		verify(pessoaService).get(Integer.valueOf(PESSOA_ID));
		verify(participacaoEventoService).adicionarOuEditarParticipacaoEvento(evento, pessoa,
				PapelEvento.ORGANIZADOR);

		action.andExpect(redirectedUrl("/evento")).andExpect(model().hasNoErrors());
	}
	
	
	@Dado("^que o administrador deseja cadastrar um evento no sistema$")
	public void administradorDesejaCadastrarUmEvento2(){
		
	}
	
	@Quando("^informar somente o nome do evento (.*)$")
	public void casoTesteQuando2(String nomeEvento) throws Throwable {

		evento = new Evento();
		evento.setNome(nomeEvento);
		evento.setEstado(EstadoEvento.INATIVO);
		
		String organizador =  "";

		action = mockMvc
				.perform(post("/evento/adicionar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("organizador", organizador));
		
	}
	
	@Então("^O evento não deve ser cadastrado$")
	public void casoTesteEntao2() throws Throwable {
		action.andExpect(view().name("evento/add_ou_edit"));
	}
	
	@Dado("^que o administrador deseja cadastrar um evento no sistema.$")
	public void administradorDesejaCadastrarUmEvento3(){
		
	}
	
	@Quando("^informar o organizador (.*) e o nome evento (.*)")
	public void casoTesteQuando3(String organizador, String nomeEvento) throws Throwable{
		
		evento = new Evento();
		evento.setNome(nomeEvento);
		evento.setEstado(EstadoEvento.INATIVO);
		
<<<<<<< HEAD
		when(pessoaService.get(Integer.valueOf(PESSOA_ID))).thenReturn(null);

		action = mockMvc
				.perform(post("/evento/adicionar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("organizador", PESSOA_ID));
	}
	
	@E("^o organizador do evento informado não está cadastrado no sistema$")
	public void casoTesteE3(){
		verify(pessoaService).get(Integer.valueOf(PESSOA_ID));
=======
		when(pessoaService.findPessoaPorId(Long.valueOf(PESSOA_ID))).thenReturn(null);

		action = mockMvc
				.perform(post("/evento/adicionar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("organizador", PESSOA_ID));
	}
	
	@E("^o organizador do evento informado não está cadastrado no sistema$")
	public void casoTesteE3(){
		verify(pessoaService).findPessoaPorId(Long.valueOf(PESSOA_ID));
>>>>>>> branch 'US-01-CadastrarEvento' of https://github.com/npi-ufc-qxd/contest.git
	}
	
	@Então("^O evento não deve ser cadastrado no sistema$")
	public void casoTesteEntao3() throws Exception{
		action.andExpect(view().name("evento/add_ou_edit"));
	}
	
	
	@Dado("^que o administrador deseja cadastrar um evento no sistema contest$")
	public void administradorDesejaCadastrarUmEvento4(){
		
	}
	
	@Quando("^informar somente o organizador (.*)$")
	public void casoTesteQuando4(String organizador) throws Exception{
		String nomeEvento = "";
		
		action = mockMvc
				.perform(post("/evento/adicionar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("nome", nomeEvento)
				.param("organizador", PESSOA_ID));
	
	}
	
	@Então("^O evento não deve ser cadastrado devido ao nome vazio$")
	public void casoTesteEntao4() throws Exception{
		action.andExpect(model().attributeHasFieldErrors("evento", "nome"));
	}
	
}