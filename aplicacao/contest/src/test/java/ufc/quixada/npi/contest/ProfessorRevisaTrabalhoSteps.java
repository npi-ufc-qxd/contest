package ufc.quixada.npi.contest;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Calendar;
import java.util.Date;

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
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
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
	
	@Mock
	private ParticipacaoTrabalhoService participacaoTrabalhoService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	private Trabalho trabalho;
	private Revisao revisao;
	private ParticipacaoTrabalho participacaoTrabalho;
	private Pessoa revisor;
	private String idEvento = "1", idTrabalho = "1", formatacao = "Problemas com a formatação", 
			originalidade = "OTIMO", merito = "OTIMO",
			clareza = "OTIMO", qualidade = "OTIMO", 
			relevancia = "Especialista", auto_avaliacao = "OTIMO",
			comentarios_autores = "Bom Trabalho", comentarios_organizacao="Bom Trabalho", 
			avaliacao_geral = "OTIMO", indicar = "Digno de indicação aos melhores trabalhos",
			avaliacao_final="APROVADO";
	
	
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
		
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 10, 30);
		Date revisaoInicial =  cal.getTime();
		cal.set(2018, 11, 10);
		Date revisaoFinal = cal.getTime();
		
		Evento evento = Mockito.mock(Evento.class);
		evento.setId(1l);
		evento.setPrazoRevisaoInicial(revisaoInicial);
		evento.setPrazoRevisaoFinal(revisaoFinal);
		
		revisor = new Pessoa();
		revisor.setCpf("11111111101");
		revisor.setId(1l);
		
		trabalho = new Trabalho();
		trabalho.setId(Long.valueOf(1));
		trabalho.setTitulo("O que fazer na greve");
		
		participacaoTrabalho = new ParticipacaoTrabalho();
		participacaoTrabalho.setPessoa(revisor);
		participacaoTrabalho.setTrabalho(trabalho);
		
		when(trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho))).thenReturn(trabalho);
		when(eventoService.buscarEventoPorId(1l)).thenReturn(evento);
		when(evento.isPeriodoRevisao()).thenReturn(true);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("11111111101");
		SecurityContextHolder.setContext(context);
		
		when(revisorController.getRevisorLogado()).thenReturn(revisor);
		
		when(participacaoTrabalhoService.getParticipacaoTrabalhoRevisor(Long.valueOf(1), Long.valueOf(1))).thenReturn(participacaoTrabalho);
		when(trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho))).thenReturn(trabalho);
		when(eventoService.existeEvento(Long.valueOf(idEvento))).thenReturn(true);
		
		when(criterios.validate(originalidade, merito, clareza, qualidade, relevancia, auto_avaliacao,
				comentarios_autores, avaliacao_geral, avaliacao_final)).thenReturn(true);
		
		when(revisaoJSON.toJson(formatacao, originalidade, merito, clareza, qualidade, relevancia, auto_avaliacao, comentarios_autores, 
				avaliacao_geral, avaliacao_final, indicar)).thenReturn("{'originalidade':'OTIMO','clareza':'OTIMO',"
						+ "'avaliacao_geral':'OTIMO','qualidade':'OTIMO','formatacao':'Problemas com a formatação',"
						+ "'relevancia':'Especialista','comentarios':'Bom Trabalho',"
						+ "'merito':'OTIMO','avaliacao_final':'APROVADO'}");
		
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
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 10, 30);
		Date revisaoInicial =  cal.getTime();
		cal.set(2018, 11, 10);
		Date revisaoFinal = cal.getTime();
		
		Evento evento = Mockito.mock(Evento.class);
		evento.setId(1l);
		evento.setPrazoRevisaoInicial(revisaoInicial);
		evento.setPrazoRevisaoFinal(revisaoFinal);
		
		revisor = new Pessoa();
		revisor.setCpf("11111111101");
		revisor.setId(1l);
		
		trabalho = new Trabalho();
		trabalho.setId(Long.valueOf(1));
		trabalho.setTitulo("O que fazer na greve");
		
		participacaoTrabalho = new ParticipacaoTrabalho();
		participacaoTrabalho.setPessoa(revisor);
		participacaoTrabalho.setTrabalho(trabalho);
		
		when(trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho))).thenReturn(trabalho);
		when(eventoService.buscarEventoPorId(1l)).thenReturn(evento);
		when(evento.isPeriodoRevisao()).thenReturn(true);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("11111111101");
		SecurityContextHolder.setContext(context);
		
		when(revisorController.getRevisorLogado()).thenReturn(revisor);
		
		when(participacaoTrabalhoService.getParticipacaoTrabalhoRevisor(Long.valueOf(1), Long.valueOf(1))).thenReturn(participacaoTrabalho);
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
	
	@E("^Tento revisar um trabalho existente fora do prazo de revisão$")
	public void revisarTrabalhoForaPrazoRevisao() throws Exception{
		
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 11, 5);
		Date revisaoInicial =  cal.getTime();
		cal.set(2016, 11, 10);
		Date revisaoFinal = cal.getTime();
		
		Evento evento = Mockito.mock(Evento.class);
		evento.setId(1l);
		evento.setPrazoRevisaoInicial(revisaoInicial);
		evento.setPrazoRevisaoFinal(revisaoFinal);
		
		trabalho = new Trabalho();
		trabalho.setId(1l);
		
		when(eventoService.buscarEventoPorId(Long.valueOf(1l))).thenReturn(evento);
		when(trabalhoService.getTrabalhoById(1l)).thenReturn(trabalho);
		when(evento.isPeriodoRevisao()).thenReturn(false);
		
		action = mockMvc
				.perform(get("/revisor/1/1/revisar"));
	}
	
	@Então("^uma mensagem informativa deve ser mostrada$")
	public void mensagemInformativa() throws NoSuchMessageException, Exception{
		action.andExpect(redirectedUrl("/eventoOrganizador")).andExpect(flash().attribute("periodoRevisaoError", messageService.getMessage("FORA_PERIODO_REVISAO")));
	}
	
	@E("^Tento revisar um trabalho que não sou revisor$")
	public void revisarTrabalhoEmQueNaoSouRevisor() throws Exception{
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
		
		Calendar cal = Calendar.getInstance();
		cal.set(2016, 10, 30);
		Date revisaoInicial =  cal.getTime();
		cal.set(2018, 11, 10);
		Date revisaoFinal = cal.getTime();
		
		Evento evento = Mockito.mock(Evento.class);
		evento.setId(1l);
		evento.setPrazoRevisaoInicial(revisaoInicial);
		evento.setPrazoRevisaoFinal(revisaoFinal);
		
		revisor = new Pessoa();
		revisor.setCpf("11111111101");
		revisor.setId(1l);
		
		trabalho = new Trabalho();
		trabalho.setId(Long.valueOf(1));
		trabalho.setTitulo("O que fazer na greve");
		
		when(trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho))).thenReturn(trabalho);
		when(eventoService.buscarEventoPorId(1l)).thenReturn(evento);
		when(evento.isPeriodoRevisao()).thenReturn(true);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("11111111101");
		SecurityContextHolder.setContext(context);
		
		when(revisorController.getRevisorLogado()).thenReturn(revisor);
		when(participacaoTrabalhoService.getParticipacaoTrabalhoRevisor(Long.valueOf(1), Long.valueOf(2))).thenReturn(null);
	
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
	
	@Então("^uma mensagem de erro de permissão deve ser mostrada$")
	public void erroPermissaoRevisor() throws Exception{
		action.andExpect(view().name("revisor/erro_permissao_de_revisor"));
	}
	
	/* Cenário: Professor tenta revisar um trabalho que já foi revisado por ele*/
	@E("^Tento revisar um trabalho que já revisei$")
	public void trabalhoJaRevisado() throws Exception{
		SecurityContext context = Mockito.mock(SecurityContext.class);
		Authentication auth = Mockito.mock(Authentication.class);
				
		Evento evento = Mockito.mock(Evento.class);
		evento.setId(1L);
		
		trabalho = new Trabalho();
		trabalho.setId(1L);
		
		revisor = new Pessoa();
		revisor.setId(1L);
		
		when(trabalhoService.getTrabalhoById(Long.valueOf(1L))).thenReturn(trabalho);
		when(eventoService.buscarEventoPorId(1L)).thenReturn(evento);
		when(evento.isPeriodoRevisao()).thenReturn(true);
		when(context.getAuthentication()).thenReturn(auth);
		when(auth.getName()).thenReturn("11111111101");
		SecurityContextHolder.setContext(context);
		
		when(revisorController.getRevisorLogado()).thenReturn(revisor);
		when(revisaoService.isTrabalhoRevisadoPeloRevisor(1L, 1L)).thenReturn(true);
		
		action = mockMvc
				.perform(get("/revisor/1/1/revisar"));
	}
	
	@Entao("^uma mensagem informando um erro deve ser mostrada$")
	public void informarErro() throws Exception{
		action.andExpect(view().name("revisor/erro_trabalho_ja_revisado"));
	}
}