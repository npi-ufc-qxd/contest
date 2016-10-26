package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.RevisaoJsonWrapper;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;

public class AtribuirRevisoresSteps {

	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;

	@Mock
	private ParticipacaoTrabalhoService participacaoTrabalhoService;
	@Mock
	private PessoaService pessoaService;
	@Mock
	private TrabalhoService trabalhoService;

	
	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa pessoa;
	private Trabalho trabalho;
	private ParticipacaoTrabalho participacaoTrabalho;
	private RevisaoJsonWrapper dadosRevisao;
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		pessoa = new Pessoa();
		trabalho = new Trabalho();
		dadosRevisao = new RevisaoJsonWrapper();
		participacaoTrabalho = new ParticipacaoTrabalho();
		
		dadosRevisao.setRevisorId(1L);
		dadosRevisao.setTrabalhoId(3L);
		
		pessoa.setId(1L);
		trabalho.setId(3L);

		pessoa.setCpf("123");
		pessoa.setNome("Kuririn");
		pessoa.setEmail("teste@teste.com");
		
		trabalho.setTitulo("Meu Trabalho");

	}
	@Dado("^que existe um organizador$")
	public void exitesUmEvento() throws Throwable {
	}

	@E("^que existe um evento$")
	public void existeEvento(){
		
	}

	@E("^que o evento possui trilhas de submissão cadastradas$")
	public void existeTrilhasDeSubmissaoCadastradas(){
		
	}
	@E("^a trilha possui um trabalho cadastrado$")
	public void trilhaPossuiTrabalhoCadastrados(){
	}


	@Quando("^o organizador seleciona atribuir um revisor para um trabalho$")
	public void organizadorAtribuiRevisoresAoTrabalho() throws Exception{
		when(pessoaService.get(dadosRevisao.getRevisorId())).thenReturn(pessoa);
		when(trabalhoService.getTrabalhoById(dadosRevisao.getTrabalhoId())).thenReturn(trabalho);
		participacaoTrabalho.setPessoa(pessoa);
		participacaoTrabalho.setTrabalho(trabalho);
		participacaoTrabalho.setPapel(Papel.REVISOR);

		String json = String.format("{\"revisorId\": \"1\",\"trabalhoId\": \"3\"}");

        action = mockMvc.perform(post("/eventoOrganizador/evento/trabalho/revisor")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json));
	}

	@Então("^o revisor selecionado é atribuído ao trabalho selecionado$")
	public void revisorEAtribuidoAoTrabalho() throws Exception{
		
		verify(participacaoTrabalhoService).adicionarOuEditar(participacaoTrabalho);
		action.andExpect(status().isOk());
	}
	
	@Quando("^o trabalho tem um revisor$")
	public void trabalhoTemRevisor() throws Exception{

	}
	
	@E("^o organizador seleciona esse revisor para ser removido do trabalho$")
	public void SelecionaRevisorParaSerRemovido() throws Exception{
		when(participacaoTrabalhoService.getParticipacaoTrabalhoRevisor(pessoa.getId(), trabalho.getId())).thenReturn(participacaoTrabalho);

		String json = String.format("{\"revisorId\": \"1\",\"trabalhoId\": \"3\"}");
		action = mockMvc
				.perform(post("/eventoOrganizador/evento/trabalho/removerRevisor")
						.contentType(MediaType.APPLICATION_JSON)
			            .content(json));
	}

	@Então("^o revisor selecionado é removido da lista de revisores do trabalho selecionado$")
	public void e() throws Exception{
		verify(participacaoTrabalhoService).remover(participacaoTrabalho);
		action.andExpect(status().isOk());
	}
	
}