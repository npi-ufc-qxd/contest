package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.E;
import cucumber.api.java.pt.Entao;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.Quando;
import ufc.quixada.npi.contest.controller.EventoController;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class AlterarTrilhasSteps {

	@InjectMocks
	private EventoController eventoController;

	@Mock
	private PessoaService pessoaService;

	@Mock
	private MessageService messageService;

	@Mock
	private ParticipacaoEventoService participacaoEventoService;
	
	@Mock
	private EventoService eventoService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Evento evento;
	private ParticipacaoEvento participacao;
	private Pessoa org;
	
	private static final String ID_EVENTO = "1";
	private static final String ID_PESSOA = "2";
	private static final String ID_PARTICIPACAO_EVENTO = "1";
	private static final String TEMPLATE_EDITAR_EVENTO = "/evento/editar/{id}";
	private static final String PAGINA_CADASTRAR = "evento/admin_cadastrar";
	private static final String TEMPLATE_LISTAR_EVENTOS_INATIVOS = "/evento/inativos";
	private static final String TEMPLATE_ADICIONAR_EVENTO = "/evento/adicionar";
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoController).build();
		evento = new Evento();
		participacao = new ParticipacaoEvento();
		org = new Pessoa();
		evento.setEstado(EstadoEvento.INATIVO);
		evento.setId(Long.valueOf(ID_EVENTO));
		evento.setNome("teste");
		evento.setDescricao("descricao");
		evento.setVisibilidade(VisibilidadeEvento.PRIVADO);

		org.setCpf("123");
		org.setEmail("a@a");
		org.setId(Long.valueOf(ID_PESSOA));
		org.setNome("Joao");

		participacao.setEvento(evento);
		participacao.setId(Long.valueOf(ID_PARTICIPACAO_EVENTO));
		participacao.setPapel(Papel.ORGANIZADOR);
		participacao.setPessoa(org);
	}
	
	@Dado("^que o organizador deseja alterar o nome de uma trilha de submissão de um evento$")
	public void desejaAlterarEvento() throws Throwable{
		//wqe
	}
	
	@Quando("^o organizador selecionar uma trilha para alterar$")
	public void alteroNomeEDescricao(String nome, String descricao) throws Throwable{
		//qwe
	}
	
	@E("^a trilha não possuir nenhum trabalho cadastrado$")
	public void eventoAlteradoComSucesso() throws Throwable{
		//awe
	}
	
	@E("^o organizador fornecer um novo nome para a trilha$")
	public void escolheEventoId(String nome, String descricao, String organizador ) throws Throwable{
		//qwe
	}
	
	@Entao("^o nome da trilha é atualizado$")
	public void redirecionaFormulario() throws Throwable{
		//asd
	}
	
	@E("^a trilha não possui nenhum trabalho cadastrado$")
	public void redirecionado() throws Throwable{
		//123
	}
	
	@E("^não fornece o novo nome da trilha$")
	public void editarEventoNaoInformandoOrganizador(String nomeEvento, String descricao) throws Exception{
		//asd
	}
	
	@Entao("^o nome da trilha não deve ser atualizado$")
	public void informaQueOCampoOrganizadorEObrigatorio() throws Exception{
		//123
	}
	
	@E("^o organizador deve visualizar uma mensagem de erro$")
	public void escolhoUmOrganizadorNaoCadastrado(String nomeEvento, String descricao) throws Exception{
		//123
	}
	
	@E("^a trilha possui algum trabalho cadastrado$")
	public void trilhaPossuiTrabalhoCadastrado() throws Exception{
		//asd
	}
	
	@Então("^o nome da trilha não deve ser atualizado$")
	public void trilhaNaoDeveSerAtualizado() throws Exception{
		//asd
	}
	
	@E("^o organizador deve visualizar uma mensagem de erro$")
	public void organizadorVisualizaMensagemDeErro() throws Exception{
		//asd
	}
}
