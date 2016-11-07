package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.gl.E;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Entao;
import cucumber.api.java.pt.Então;
import ufc.quixada.npi.contest.controller.RevisorController;
import ufc.quixada.npi.contest.model.AvaliacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.RevisaoJSON;
import ufc.quixada.npi.contest.validator.CriteriosRevisaoValidator;

public class ProfessorRevisaTrabalhoSteps {

	@InjectMocks
	private RevisorController revisorController;

	@Mock
	private TrabalhoService trabalhoService;
	
	@Mock
	private EventoService eventoService;
	
	@Mock
	private CriteriosRevisaoValidator criterios;
	
	@Mock
	private RevisaoJSON revisaoJSON;
	
	@Mock
	private RevisaoService revisaoService;
	
	@Mock
	private MessageService messageService;
	
	@Mock
	private PessoaService pessoaService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Trabalho trabalho;
	private Revisao revisao;
	private Pessoa revisor;
	private String idEvento = "1", formatacao = "Problemas com a formatação", 
			originalidade = "OTIMO", merito = "OTIMO",
			clareza = "OTIMO", qualidade = "OTIMO", 
			relevancia = "Especialista", auto_avaliacao = "OTIMO",
			comentarios_autores = "Bom Trabalho", comentarios_organizacao="Bom Trabalho", 
			avaliacao_geral = "OTIMO", indicar = "Digno de indicação aos melhores trabalhos",
			avaliacao_final="APROVADO";
	
	String idTrabalho = "2";
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(revisorController).build();
	}
	
	
	//Contexto
	@Dado("^Dado Estou logado no sistema como professor$")
	public void professorLogadoNoSistema() throws Throwable {
		// nenhuma ação de teste necessária
	}

	
	//Cenário: Professor revisa um trabalho com sucesso
	@E("^Realizo a revisão de um artigo com todos os critérios obrigatórios selecionados$")
	public void todosOsCriteriosObrigatoriosSelecionados() throws Exception{
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		revisor = new Pessoa();
		revisor.setCpf("92995454310");
		
		trabalho = new Trabalho();
		trabalho.setId(Long.valueOf(2));
		trabalho.setTitulo("O que fazer na greve");
		
		when(trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho))).thenReturn(trabalho);
		when(eventoService.existeEvento(Long.valueOf(idEvento))).thenReturn(true);
		when(criterios.validate(originalidade, merito, clareza, qualidade, relevancia, auto_avaliacao,
				comentarios_autores, avaliacao_geral, avaliacao_final)).thenReturn(true);
		
		when(RevisaoJSON.toJson(formatacao, originalidade, merito, clareza, qualidade, relevancia, auto_avaliacao, comentarios_autores, 
				avaliacao_geral, avaliacao_final, indicar)).thenReturn("{'originalidade':'OTIMO','clareza':'OTIMO',"
						+ "'avaliacao_geral':'OTIMO','qualidade':'OTIMO','formatacao':'Problemas com a formatação',"
						+ "'relevancia':'Especialista','comentarios':'Bom Trabalho',"
						+ "'merito':'OTIMO','avaliacao_final':'APROVADO'}");
		
		
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("92995454310");
		SecurityContextHolder.setContext(context);
		
		when(pessoaService.get(1L)).thenReturn(revisor);
		
		revisao = new Revisao();
		revisao.setConteudo("conteudo");
		revisao.setObservacoes(comentarios_organizacao);
		revisao.setAvaliacao(AvaliacaoTrabalho.Avaliacao.REPROVADO);
		revisao.setTrabalho(trabalho);
		
		action = mockMvc
				.perform(post("/revisor/avaliar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idTrabalho", idTrabalho)
				.param("idEvento", idEvento)
				.param("formatacao", formatacao)
				.param("originalidade", originalidade)
				.param("merito", merito)
				.param("clareza", clareza)
				.param("qualidade", qualidade)
				.param("relevancia", relevancia)
				.param("auto-avaliacao", auto_avaliacao)
				.param("avaliacao-geral", avaliacao_geral)
				.param("avaliacao-final", avaliacao_final)
				.param("comentarios_autores", comentarios_autores)
				.param("comentarios_organizacao", comentarios_organizacao)
				.param("indicar", indicar));
	}
	
	@Então("^A revisão é registrada$")
	public void revisaoERegistrada(){
		verify(revisaoService).addOrUpdate(revisao);
	}
	
	@E("^Uma mensagem de sucesso deve ser mostrada$")
	public void mensagemSucessoMostrada() throws Exception{
		action.andExpect(redirectedUrl("/revisor/" + 1 + "/trabalhosRevisao"))
		.andExpect(flash().attribute("trabalhoRevisado", messageService.getMessage("TRABALHO_REVISADO")));
	}
	
	//Cenário: Professor não preenche todos os campos obrigatórios
	@E("^Realizo a revisão de um artigo com um critério obrigatório não preenchido$")
	public void criterioObrigatorioNaoPreenchido() throws Exception{
		
		revisor = new Pessoa();
		revisor.setCpf("92995454310");
		
		trabalho = new Trabalho();
		trabalho.setId(Long.valueOf(2));
		trabalho.setTitulo("O que fazer na greve");
		
		when(trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho))).thenReturn(trabalho);
		when(eventoService.existeEvento(Long.valueOf(idEvento))).thenReturn(true);
		when(criterios.validate(originalidade, merito, "", "", "", "",
				comentarios_autores, avaliacao_geral, avaliacao_final)).thenReturn(false);
		
		action = mockMvc
				.perform(post("/revisor/avaliar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idTrabalho", idTrabalho)
				.param("idEvento", idEvento)
				.param("formatacao", formatacao)
				.param("originalidade", originalidade)
				.param("merito", merito)
				.param("clareza", "")
				.param("qualidade", "")
				.param("relevancia", "")
				.param("auto-avaliacao", "")
				.param("avaliacao-geral", avaliacao_geral)
				.param("avaliacao-final", avaliacao_final)
				.param("comentarios_autores", comentarios_autores)
				.param("comentarios_organizacao", comentarios_organizacao)
				.param("indicar", indicar));
	}
	
	@Entao("^A revisão não é aceita$")
	public void revisaoNaoAceita(){
		verify(revisaoService, never()).addOrUpdate(revisao);
	}
	
	@E("^Uma mensagem de erro deve ser mostrada$")
	public void mensagemErroMostrada() throws NoSuchMessageException, Exception{
		action.andExpect(redirectedUrl("/revisor/" + idEvento + "/" + idTrabalho + "/revisar"))
		.andExpect(flash().attribute("criterioRevisaoVazioError", messageService.getMessage("CRITERIOS_REVISAO_VAZIO")));
	}
	
	//Cenário: Professor tenta revisar um trabalho inexistente
	@E("^Preencho todos os critérios obrigatórios$")
	public void todosCriteriosObrigatorios() throws Exception{
		action = mockMvc
				.perform(post("/revisor/avaliar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idTrabalho", idTrabalho)
				.param("idEvento", idEvento)
				.param("formatacao", formatacao)
				.param("originalidade", originalidade)
				.param("merito", merito)
				.param("clareza", clareza)
				.param("qualidade", qualidade)
				.param("relevancia", relevancia)
				.param("auto-avaliacao", auto_avaliacao)
				.param("avaliacao-geral", avaliacao_geral)
				.param("avaliacao-final", avaliacao_final)
				.param("comentarios_autores", comentarios_autores)
				.param("comentarios_organizacao", comentarios_organizacao)
				.param("indicar", indicar));
	}
	
	@E("^O Trabalho a ser revisado não existe$")
	public void trabalhoNaoExiste(){
		when(trabalhoService.getTrabalhoById(Long.valueOf(-1))).thenReturn(null);
		when(eventoService.existeEvento(Long.valueOf(-1))).thenReturn(false);
	}
	
	@Entao("^A revisão não é registrada$")
	public void revisaoNaoRegistrada(){
		verify(revisaoService, never()).addOrUpdate(revisao);;
	}
	
	@E("^Um erro no sistema deve ser mostrado$")
	public void erroMostrado() throws NoSuchMessageException, Exception{
		action.andExpect(redirectedUrl("/error"));
	}

}