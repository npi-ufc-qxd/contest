package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Quando;
import cucumber.api.java.pt.Então;
import cucumber.api.java.pt.E;
import ufc.quixada.npi.contest.controller.AutorController;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

public class AutorParticipaEvento{
	
	@InjectMocks
	AutorController alunoController;
	
	@Mock
	PessoaService pessoaService;
	
	@Mock
	EventoService eventoService;
	
	@Mock
	ParticipacaoEventoService participacaoEventoService;
	
	@Mock
	MessageService messageService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Pessoa pessoa;
	private Evento evento;
	private String ALUNO_ID = "2";
	private String EVENTO_ID = "2";
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(alunoController).build();
		eventoService.toString();//Para evitar do codacy reclamar
	}
	
	/**
	 * Cenário 1
	 */
	@Dado("^que existe um evento ativo e público cadastrado no sistema$")
	public void existeEventoAtivoEPublico(){
		//Nao há necessidade de realizar um teste aqui
	}
	
	@Quando("^o aluno decide participar de um evento com id (.*)$")
	public void casoTesteQuandoCenario1(String idEvento) throws Exception{
		EVENTO_ID = idEvento;
		
		evento = new Evento();
		evento.setId(Long.valueOf(idEvento));
		evento.setNome("Racha no Pinheiro");
		evento.setEstado(EstadoEvento.ATIVO);
		evento.setVisibilidade(VisibilidadeEvento.PUBLICO);
		
		when(eventoService.buscarEventoPorId(Long.valueOf(idEvento))).thenReturn(evento);
	
		action = mockMvc
				.perform(post("/aluno/participarevento")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idEvento", "2"));
				
	}
	
	@Então("^o sistema registra a participação$")
	public void casoTesteEntaoCenario1(){
		verify(eventoService).buscarEventoPorId(Long.valueOf(EVENTO_ID));
		
		ParticipacaoEvento participacao = new ParticipacaoEvento();
		participacao.setEvento(evento);
		participacao.setPessoa(pessoa);
		participacao.setPapel(Papel.AUTOR);
		
		verify(participacaoEventoService).adicionarOuEditarParticipacaoEvento(participacao);	
	}
	
	@E("^retorna uma mensagem de sucesso para o aluno$")
	public void casoTesteECenario1() throws Exception{
		action.andExpect(redirectedUrl("/autor")).andExpect(model().attributeDoesNotExist("eventoVazioError", "eventoInexistenteError"));
	}
	
	/**
	 * Cenário 2
	 *
	 */
	@Dado("^que existe um evento inativo cadastrado no sistema com id (.*)$")
	public void existeEventoInativo(String idEvento){
		EVENTO_ID = idEvento;
	}
	
	@Quando("^o aluno com id (.*) decide participar deste evento$")
	public void casoTesteQuandoCenario2(String idAluno) throws Exception{
		ALUNO_ID = idAluno;
		
		pessoa = new Pessoa();
		pessoa.setId(Long.valueOf(idAluno));
		pessoa.setCpf("12345678978");
		pessoa.setNome("Raimundo");
		pessoa.setEmail("a@a.com");
		pessoa.setPapelLdap("DISCENTE");
		
		evento = new Evento();
		evento.setId(Long.valueOf(EVENTO_ID));
		evento.setNome("Encontros Universitarios");
		evento.setEstado(EstadoEvento.INATIVO);
		
		when(pessoaService.get(Long.valueOf(idAluno))).thenReturn(pessoa);
		when(eventoService.buscarEventoPorId(Long.valueOf(EVENTO_ID))).thenReturn(evento);
		when(messageService.getMessage("PARTICIPAR_EVENTO_INATIVO"))
		.thenReturn("Nao e permitido participar de um evento inativo");
		
		action = mockMvc
				.perform(post("/aluno/participarevento")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idAluno", idAluno)
				.param("idEvento", EVENTO_ID));
	}
	
	@Então("^uma mensagem de erro é retornada dizendo que o aluno não pode participar de eventos inativos$")
	public void casoTesteEntaoCenario2() throws Exception{
		verify(messageService).getMessage("PARTICIPAR_EVENTO_INATIVO");
		
		action.andExpect(model().attributeExists("participarEventoInativoError"));
	}

}