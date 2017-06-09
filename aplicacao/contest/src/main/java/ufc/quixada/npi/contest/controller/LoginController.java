package ufc.quixada.npi.contest.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Token;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TokenService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
public class LoginController {
	
	@Autowired
	private PessoaService pessoaService;
	@Autowired
	private EventoService eventoService;
	@Autowired
	private ParticipacaoTrabalhoService participacaoTrabalhoService;
	@Autowired
	private ParticipacaoEventoService participacaoEventoService;
	@Autowired
	TokenService tokenService;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET  )
	public String login() {
		return "login";
	}
	

	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginfailed(Authentication auth, RedirectAttributes redirectAttributes) {
		if (auth != null && auth.isAuthenticated()) {
			return "redirect:/";
		}

		redirectAttributes.addFlashAttribute("loginError", true);
		return "redirect:/login";
	}
	
	@RequestMapping(value = "/cadastroForm")
	public String cadastroForm(Model model) {
		model.addAttribute("user", new Pessoa());
		return "cadastro";
	}
	
	@RequestMapping(value = "/cadastro")
	public String cadastro(@Valid Pessoa pessoa, @RequestParam String senha, @RequestParam String senhaConfirma) {
	
		if(senhaConfirma.equals(senha)){
			String password = pessoaService.encodePassword(senha);
			pessoa.setPassword(password);
			pessoa.setPapel(Tipo.USER);
			pessoaService.addOrUpdate(pessoa);
			return "login";
		}
		
		return "cadastro" ;
	}
	
	@RequestMapping(path="/completar-cadastro/{token}", method=RequestMethod.GET)
	public ModelAndView completarCadastroForm(@PathVariable("token") Token token) throws Exception{
		ModelAndView model = new ModelAndView();
		
		if (token.getAcao().equals(Constants.ACAO_COMPLETAR_CADASTRO)){
			model.setViewName("completar-cadastro");
			model.addObject("pessoa", token.getPessoa());
		} else {
			throw new Exception("O token passado não corresponde a ação de completar cadastro.");
		}
		
		return model;
	}
	
	@RequestMapping(path="/completar-cadastro", method=RequestMethod.POST)
	public String completarCadastro(@Valid Pessoa pessoa, @RequestParam String senha, @RequestParam String senhaConfirma){
		
		if(senha.equals(senhaConfirma)){
			String password =  pessoaService.encodePassword(senha);
			pessoa.setPassword(password);
			pessoaService.addOrUpdate(pessoa);
			tokenService.deletar(tokenService.buscarPorUsuario(pessoa));
			
		} 		
		
		return "redirect:/login";
	}
	
	
	
	@RequestMapping(value = "/dashboard")
	public String dashboard(Model model){
		String cpf = SecurityContextHolder.getContext().getAuthentication().getName();
		List<Evento> eventos = eventoService.eventosParaParticipar(Long.parseLong(cpf));
		Pessoa pessoaAux = pessoaService.getByCpf(cpf);
		List<ParticipacaoTrabalho> trabalhosQueReviso = participacaoTrabalhoService.getTrabalhosPorRevisorId(pessoaAux.getId());
		List<ParticipacaoEvento> eventoQueOrganizo = participacaoEventoService.getEventosDoOrganizador(EstadoEvento.ATIVO, pessoaAux.getId());
		List<ParticipacaoTrabalho> trabalhosMinhaAutoria = participacaoTrabalhoService.getParticipacaoTrabalhoPorAutorId(pessoaAux.getId());
		model.addAttribute("eventosQueOrganizo", eventoQueOrganizo);
		model.addAttribute("eventosParaParticipar", eventos);
		model.addAttribute("trabalhosQueReviso", trabalhosQueReviso);
		model.addAttribute("trabalhosMinhaAutoria", trabalhosMinhaAutoria);
		model.addAttribute("pessoa",pessoaAux);
		return "dashboard";
	}

	
	@RequestMapping("resetarSenha")
	public String resetarSenha(){
		
		return "resetar_senha";
	}
	
	@RequestMapping("esqueciMinhaSenha")
	public String esqueciSenha(){
		
		return "esqueci_senha";
	}

}
