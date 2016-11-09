package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
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
	
	private List<Pessoa> listaPessoas;
	private List<Trabalho> listaTrabalho;
	private Evento evento;
	private Trabalho trabalho;
	private MockMvc mockMvc;
	private ResultActions action;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(eventoControllerOrganizador).build();
		
		listaPessoas = new ArrayList<Pessoa>();
		listaTrabalho = new ArrayList<Trabalho>();
		evento = new Evento();
		trabalho = new Trabalho();
		
		evento.setId(ID_EVENTO);
	}
	
	@Dado("^que o organizador deseja gerar o pdf dos organizadores$")
	public void organizadorDesejaGerarPdfParaOrganizadores() throws Exception{
		when(pessoaService.getOrganizadoresEvento(ID_EVENTO)).thenReturn(listaPessoas);
		action = mockMvc.perform(get(PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_ORGANIZADOR, ID_EVENTO))
				.andExpect(view().name(Constants.TEMPLATE_GERAR_CERTIFICADOS_ORGANIZADORES));
	}
	
	@Quando("^ele seleciona os organizadores e manda gerar o pdf$")
	public void organizadoSelecionaOrganizadoresParaGerarPdf() throws Throwable {
		when(pessoaService.getOrganizadoresEvento(ID_EVENTO)).thenReturn(listaPessoas);
		action = mockMvc
				.perform(post("/eventoOrganizador/gerarCertificadosOrganizadores")
				.param("organizadoresIds", IDS));
		
	}
	
	@Então("^o pdf dos organizadores e gerado$")
	public void pdfDosOrganizadoresEGerado() throws Throwable {
		action.andExpect(view().name("PDF_ORGANIZADOR"));
	}
	
	@Dado("^que o organizador deseja gerar o pdf dos revisores$")
	public void organizadorDesejaGerarPdfParaRevisores() throws Exception{
		when(pessoaService.getOrganizadoresEvento(ID_EVENTO)).thenReturn(listaPessoas);
		action = mockMvc.perform(get(PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_REVISOR, ID_EVENTO))
				.andExpect(view().name(Constants.TEMPLATE_GERAR_CERTIFICADOS_REVISORES));
	}
	
	@Quando("^ele seleciona os revisores e manda gerar o pdf$")
	public void organizadoSelecionaRevisoresParaGerarPdf() throws Throwable {
		when(pessoaService.getOrganizadoresEvento(ID_EVENTO)).thenReturn(listaPessoas);
		action = mockMvc
				.perform(post("/eventoOrganizador/gerarCertificadosRevisores")
				.param("revisores", IDS));
		
	}
	
	@Então("^o pdf dos revisores e gerado$")
	public void pdfDosRevisoresEGerado() throws Throwable {
		action.andExpect(view().name("PDF_REVISORES"));
	}
	
	
	@Dado("^que o organizador deseja gerar o pdf dos trabalhos$")
	public void organizadorDesejaGerarPdfParaOsTrabalhos() throws Exception{
		when(eventoService.buscarEventoPorId(ID_EVENTO)).thenReturn(evento);
		when(trabalhoService.getTrabalhosEvento(evento)).thenReturn(listaTrabalho);
		action = mockMvc.perform(get(PAGINA_EVENTO_ORGANIZADOR_GERAR_CERTIFICADOS_TRABALHOS, ID_EVENTO))
				.andExpect(view().name(Constants.TEMPLATE_GERAR_CERTIFICADOS_TRABALHO));
	}
	
	@Quando("^ele seleciona os trabalhos e manda gerar o pdf$")
	public void organizadoSelecionaOsTrabalhosParaGerarPdf() throws Throwable {
		when(trabalhoService.getTrabalhoById(ID_EVENTO)).thenReturn(trabalho);
		action = mockMvc
				.perform(post("/eventoOrganizador/gerarCertificadosTrabalho")
				.param("trabalhosIds", IDS));
		
	}
	
	@Então("^o pdf dos trabalhos e gerado$")
	public void pdfDosTrabalhosEGerado() throws Throwable {
		action.andExpect(view().name("PDF_TRABALHOS"));
	}
	
}