package ufc.quixada.npi.contest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cucumber.api.java.Before;
import cucumber.api.java.gl.E;
import cucumber.api.java.pt.Dado;
import cucumber.api.java.pt.Então;
import ufc.quixada.npi.contest.controller.AutorController;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TrabalhoService;

public class BaixarTrabalhoSteps {

	@InjectMocks
	private AutorController autorController;
	
	@Mock
	private ParticipacaoTrabalhoService participacaoTrabalhoService;
	
	@Mock
	private ParticipacaoEventoService participacaoEventoService;
	
	@Mock
	private TrabalhoService trabalhoService;
	
	@Mock
	private PessoaService pessoaService;
	
	private MockMvc mockMvc;
	private ResultActions action;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(autorController).build();
	}
	
	//Cenário: Eu quero baixar um trabalho em que não sou revisor e nem organizador do evento do trabalho
		@Dado("^Eu quero baixar um trabalho$")
		public void baixarTrabalho(){
			//Não há necessidade de testes
		}
	
		@E("^Não sou revisor e nem organizador do evento deste trabalho$")
		public void naoRevisorNaoOrganizador() throws Exception{
			SecurityContext context = Mockito.mock(SecurityContext.class);
			Authentication auth = Mockito.mock(Authentication.class);
			when(context.getAuthentication()).thenReturn(auth);
			when(auth.getName()).thenReturn("11111111102");
			SecurityContextHolder.setContext(context);
			
			Evento evento = new Evento();
			evento.setId(1L);
			
			Long idTrabalho = 1l;
			Trabalho trabalho = new Trabalho();
			trabalho.setId(idTrabalho);
			trabalho.setEvento(evento);
			
			Long idRevisor = 6l;
			Pessoa revisor = new Pessoa();
			revisor.setId(idRevisor);
			revisor.setCpf("11111111102");
			
			when(trabalhoService.getTrabalhoById(Long.valueOf(idTrabalho))).thenReturn(trabalho);
			when(pessoaService.getByCpf(revisor.getCpf())).thenReturn(revisor);
			when(participacaoTrabalhoService.isParticipandoDoTrabalho(idTrabalho, revisor.getId())).thenReturn(false);
			when(participacaoEventoService.isOrganizadorDoEvento(revisor, evento.getId())).thenReturn(false);
			
			action = mockMvc
					.perform(get("/autor/file/1"));
		}
		
		@Então("^um erro deve ser mostrado$")
		public void erroDownload() throws Exception{
			action.andExpect(redirectedUrl("/error/500"));
		}
}