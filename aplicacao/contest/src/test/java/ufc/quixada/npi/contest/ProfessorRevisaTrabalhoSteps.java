package ufc.quixada.npi.contest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.gl.E;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import ufc.quixada.npi.contest.controller.RevisorController;
import ufc.quixada.npi.contest.model.AvaliacaoTrabalho;
import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.MessageService;
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
	private CriteriosRevisaoValidator validator;
	
	@Mock
	private RevisaoJSON revisaoJSON;
	
	@Mock
	private RevisaoService revisaoService;
	
	@Mock
	private MessageService messageService;

	private MockMvc mockMvc;
	private ResultActions action;
	private Trabalho trabalho;
	private Revisao revisao;
	private String idTrabalho = "1", formatacao = "ok", 
			originalidade = "ok", merito = "ok",
			clareza = "ok", qualidade = "ok", 
			relevancia = "ok", auto_avaliacao = "ok",
			comentarios_autores = "ok", comentarios_organizacao="ok", 
			avaliacao_geral = "ok", avaliacao_final="APROVADO", indicar = "ok";
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(revisorController).build();
	}

	@Dado("^Dado Estou logado no sistema como professor$")
	public void professorLogadoNoSistema() throws Throwable {
		// nenhuma ação de teste necessária
	}

	@E("^Realizo a revisão de um artigo com todos os critérios obrigatórios selecionados$")
	public void todosOsCriteriosObrigatoriosSelecionados() throws Exception{
		
		trabalho = new Trabalho();
		trabalho.setId(Long.valueOf(1));
		
		when(trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho))).thenReturn(trabalho);
		when(validator.validate(originalidade, merito, clareza, qualidade, relevancia, auto_avaliacao,
				comentarios_autores, avaliacao_geral, avaliacao_final)).thenReturn(true);
		when(revisaoJSON.toJson(formatacao, originalidade, merito, clareza, qualidade, relevancia, auto_avaliacao, comentarios_autores, 
				avaliacao_geral, avaliacao_final, indicar)).thenReturn("{'originalidade':'FRACO','clareza':'RUIM',"
						+ "'avaliacao_geral':'RUIM','qualidade':'RUIM','formatacao':'Problemas com a formatação',"
						+ "'relevancia':'Não Conhecedor','comentarios':'Cara',"
						+ "'merito':'FRACO','avaliacao_final':'REPROVADO'}");
		
		revisao = new Revisao();
		revisao.setConteudo("conteudo");
		revisao.setObservacoes(comentarios_organizacao);
		revisao.setAvaliacao(AvaliacaoTrabalho.Avaliacao.REPROVADO);
		
		action = mockMvc
				.perform(post("/revisor/avaliar")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("idTrabalho", idTrabalho)
				.param("formatacao", formatacao)
				.param("originalidade", originalidade)
				.param("merito", merito)
				.param("clareza", clareza)
				.param("qualidade", qualidade)
				.param("relevancia", relevancia)
				.param("auto-avaliacao", auto_avaliacao)
				.param("avaliacao-geral", avaliacao_geral)
				.param("avaliacao_final", avaliacao_final)
				.param("comentarios_autores", comentarios_autores)
				.param("comentarios_organizacao", comentarios_organizacao));
	}
	
	@Então("^A revisão é registrada$")
	public void revisaoERegistrada(){
		verify(trabalhoService).getTrabalhoById(1L);
		verify(validator).validate(originalidade, merito, clareza, qualidade, relevancia, auto_avaliacao,
				comentarios_autores, avaliacao_geral, avaliacao_final);
		verify(revisaoJSON).toJson(formatacao,
				originalidade, merito, clareza,
				qualidade, relevancia, auto_avaliacao,
				comentarios_autores, avaliacao_geral, avaliacao_final, indicar);
		verify(revisaoService).addOrUpdate(revisao);
	}
	
	@E("^Uma mensagem de sucesso deve ser mostrada$")
	public void mensagemSucessoMostrada() throws Exception{
		action.andExpect(redirectedUrl("/revisor/" + 1L + "/trabalhosRevisao")).andExpect(model().attributeExists("trabalhoRevisado"));
	}
}