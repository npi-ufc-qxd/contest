package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import net.sf.jasperreports.engine.JRDataSource;
import ufc.quixada.npi.contest.controller.EventoControllerOrganizador;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.Constants;

public class GerarCertificadoSteps {
	private static final String PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_ORGANIZADOR = "/eventoOrganizador/gerarCertificadosOrganizador/{idEvento}";
	private static final String PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_REVISOR = "/eventoOrganizador/gerarCertificadosRevisores/{idEvento}";
	private static final String PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_TRABALHOS = "/eventoOrganizador/gerarCertificadosTrabalho/{idEvento}";
	private static final Long ID_EVENTO = 3L;
	private static final String[] IDS = {"1","2","3"};
	
	@InjectMocks
	private EventoControllerOrganizador eventoControllerOrganizador;
	
	@Mock
	private PessoaService pessoaService;
	
	@Mock
	private TrabalhoService trabalhoService;
	
	@Mock
	private EventoService eventoService;

	@Mock
	private JRDataSource jrDataSource;
	
	
	private Pessoa revisor1, revisor2, revisor3;
	private List<Pessoa> listaPessoas;
	private List<Trabalho> listaTrabalho;
	private Evento evento;
	private Trabalho trabalho1, trabalho2, trabalho3;
	private MockMvc mockMvc;
	private ResultActions action;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		
		listaPessoas = new ArrayList<Pessoa>();
		listaTrabalho = new ArrayList<Trabalho>();
		List<Pessoa> coautores = new ArrayList<Pessoa>();
		evento = new Evento();
		evento.setId(ID_EVENTO);
		evento.setPrazoSubmissaoFinal(new Date());
		
		Pessoa pessoa1 = new Pessoa();
		Pessoa pessoa2 = new Pessoa();
		
		pessoa1.setNome("Joao Paulo Silv aSauro");
		pessoa2.setNome("Maria Tereza dos Anzois");
		
		coautores.add(pessoa2);
		
		Trilha trilha = new Trilha();
		trilha.setId(1L);
		trilha.setEvento(evento);
		trilha.setNome("Trilha Padrao");
		
		trabalho1 = new Trabalho();
		trabalho1.setId(1L);
		trabalho1.setTitulo("Titulo");
		trabalho1.setAutores(pessoa1, coautores);
		trabalho1.setEvento(evento);
		trabalho1.setTrilha(trilha);
		
		trabalho2 = new Trabalho();
		trabalho2.setId(2L);
		trabalho2.setTitulo("Titulo");
		trabalho2.setAutores(pessoa1, coautores);
		trabalho2.setEvento(evento);
		trabalho2.setTrilha(trilha);
		
		trabalho3 = new Trabalho();
		trabalho3.setId(3L);
		trabalho3.setTitulo("Titulo");
		trabalho3.setAutores(pessoa1, coautores);
		trabalho3.setEvento(evento);
		trabalho3.setTrilha(trilha);

				
		revisor1 = new Pessoa();
		revisor1.setId(1L);
		revisor1.setNome("Revisor 1");
		
		revisor2 = new Pessoa();
		revisor2.setId(2L);
		revisor2.setNome("Revisor 2");
		
		revisor3 = new Pessoa();
		revisor3.setId(3L);
		revisor3.setNome("Revisor 3");
	}
	
	@Dado("^que o organizador deseja gerar a planilha dos organizadores$")
	public void organizadorDesejaGerarPdfParaOrganizadores() throws Exception{
		when(pessoaService.getOrganizadoresEvento(ID_EVENTO)).thenReturn(listaPessoas);
		when(pessoaService.get(revisor1.getId())).thenReturn(revisor1);
		when(pessoaService.get(revisor2.getId())).thenReturn(revisor2);
		when(pessoaService.get(revisor3.getId())).thenReturn(revisor3);
		
		action = mockMvc.perform(get(PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_ORGANIZADOR, ID_EVENTO))
				.andExpect(view().name(Constants.TEMPLATE_GERAR_CERTIFICADOS_ORGANIZADORES));
	}
	
	@Quando("^ele seleciona os organizadores e manda gerar a planilha$")
	public void organizadoSelecionaOrganizadoresParaGerarPdf() throws Throwable {
		when(pessoaService.getOrganizadoresEvento(ID_EVENTO)).thenReturn(listaPessoas);
		action = mockMvc
				.perform(post("/eventoOrganizador/gerarCertificadosOrganizadores")
				.param("organizadoresIds", IDS));
		
	}
	
	@Então("^a planilha dos organizadores e gerada$")
	public void pdfDosOrganizadoresEGerado() throws Throwable {
		action.andExpect(view().name("DADOS_ORGANIZADOR"));
	}
	
	@Dado("^que o organizador deseja gerar a planilha dos revisores$")
	public void organizadorDesejaGerarPdfParaRevisores() throws Exception{
		when(pessoaService.getOrganizadoresEvento(ID_EVENTO)).thenReturn(listaPessoas);
		action = mockMvc.perform(get(PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_REVISOR, ID_EVENTO))
				.andExpect(view().name(Constants.TEMPLATE_GERAR_CERTIFICADOS_REVISORES));
	}
	
	@Quando("^ele seleciona os revisores e manda gerar a planilha$")
	public void organizadoSelecionaRevisoresParaGerarPdf() throws Throwable {
		when(pessoaService.getOrganizadoresEvento(ID_EVENTO)).thenReturn(listaPessoas);
		action = mockMvc
				.perform(post("/eventoOrganizador/gerarCertificadosRevisores")
				.param("revisores", IDS));
		
	}
	
	@Então("^a planilha dos revisores e gerada$")
	public void pdfDosRevisoresEGerado() throws Throwable {
		action.andExpect(view().name("DADOS_REVISORES"));
	}
	
	
	@Dado("^que o organizador deseja gerar a planilha dos trabalhos$")
	public void organizadorDesejaGerarPdfParaOsTrabalhos() throws Exception{
		when(eventoService.buscarEventoPorId(ID_EVENTO)).thenReturn(evento);
		when(trabalhoService.getTrabalhosEvento(evento)).thenReturn(listaTrabalho);
		action = mockMvc.perform(get(PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_TRABALHOS, ID_EVENTO))
				.andExpect(view().name(Constants.TEMPLATE_GERAR_CERTIFICADOS_TRABALHO));
	}
	
	@Quando("^ele seleciona os trabalhos e manda gerar a planilha$")
	public void organizadoSelecionaOsTrabalhosParaGerarPdf() throws Throwable {
		when(trabalhoService.getTrabalhoById(trabalho1.getId())).thenReturn(trabalho1);
		when(trabalhoService.getTrabalhoById(trabalho2.getId())).thenReturn(trabalho2);
		when(trabalhoService.getTrabalhoById(trabalho3.getId())).thenReturn(trabalho3);
		action = mockMvc
				.perform(post("/eventoOrganizador/gerarCertificadosTrabalho")
				.param("trabalhosIds", IDS));
		
	}
	
	@Então("^a planilha dos trabalhos e gerada$")
	public void pdfDosTrabalhosEGerado() throws Throwable {
		action.andExpect(view().name("DADOS_TRABALHOS"));
	}
	
}