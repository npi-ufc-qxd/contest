package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

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
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.RevisaoJsonWrapper;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;

public class AtribuirRevisoresSteps {
	
	private static final String ID_TRABALHO = "3";
	private static final String ID_REVISOR = "1";

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
	private Evento evento;
	private ParticipacaoEvento participacaoEvento;
	private Trilha trilha;
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		pessoa = new Pessoa();
		trabalho = new Trabalho();
		dadosRevisao = new RevisaoJsonWrapper();
		participacaoTrabalho = new ParticipacaoTrabalho();
		participacaoEvento = new ParticipacaoEvento();
		evento = new Evento();
		trilha = new Trilha();
		
		dadosRevisao.setRevisorId(1L);
		dadosRevisao.setTrabalhoId(3L);
		
		pessoa.setId(1L);
		trabalho.setId(3L);
		evento.setId(1L);
		trilha.setId(5L);

		pessoa.setCpf("123");
		pessoa.setNome("Kuririn");
		pessoa.setEmail("teste@teste.com");
		
		evento.setEstado(EstadoEvento.ATIVO);
		evento.setDescricao("novo evento");
		evento.setNome("Evento");
		evento.setVisibilidade(VisibilidadeEvento.PUBLICO);
		
		trilha.setEvento(evento);
		trilha.setNome("Nome");
		
		trabalho.setTitulo("Meu Trabalho");
		trabalho.setEvento(evento);
		trabalho.setTrilha(trilha);
	}
	@Dado("^que existe um organizador$")
	public void exitesUmEvento() throws Throwable {
	}

	@E("^que existe um evento$")
	public void existeEvento(){
//		List<ParticipacaoEvento> participacoes = new ArrayList<>();
//		participacaoEvento.setEvento(evento);
//		participacaoEvento.setPapel(Papel.REVISOR);
//		participacaoEvento.setPessoa(pessoa);
//		participacaoEvento.setId(1L);
//		participacoes.add(participacaoEvento);
//		evento.setParticipacoes(participacoes);
		
		//pessoa.setParticipacoesEvento(participacoes);
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
		action = mockMvc
				.perform(post("/evento/trabalho/reviso")
				.param("revisorId", ID_REVISOR)
				.param("trabalhoId", ID_TRABALHO));
	}

	@Então("^o revisor selecionado é atribuído ao trabalho selecionado$")
	public void e() throws Exception{
		//verify(participacaoTrabalhoService).adicionarOuEditar(participacaoTrabalho);
		action.andExpect(status().isOk());
	}
}