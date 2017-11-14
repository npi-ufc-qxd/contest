package ufc.quixada.npi.contest.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Token;
import ufc.quixada.npi.contest.service.EnviarEmailService;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.ParticipacaoTrabalhoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.TokenService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;

@Controller
public class LoginController {

	@Autowired
	PessoaService pessoaService;

	@Autowired
	EventoService eventoService;

	@Autowired
	ParticipacaoTrabalhoService participacaoTrabalhoService;

	@Autowired
	TrabalhoService trabalhoService;
	
	public static final String PESSOA = "pessoa";

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private EnviarEmailService enviarEmailService;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}

	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginfailed(Authentication auth, RedirectAttributes redirectAttributes) {
		if (auth != null && auth.isAuthenticated()) {
			return "redirect:/";
		}

		redirectAttributes.addFlashAttribute("loginError", true);
		return Constants.REDIRECIONAR_PARA_LOGIN;
	}

	@RequestMapping(value = "/cadastroForm")
	public String cadastroForm(Model model) {
		model.addAttribute("user", new Pessoa());
		return "cadastro";
	}

	@RequestMapping(value = "/cadastro")
	public String cadastro(@Valid Pessoa pessoa, @RequestParam String senha, @RequestParam String senhaConfirma) {

		if (senhaConfirma.equals(senha)) {
			String password = pessoaService.encodePassword(senha);
			pessoa.setPassword(password);
			pessoa.setPapel(Tipo.USER);
			pessoaService.addOrUpdate(pessoa);
			return Constants.REDIRECIONAR_PARA_LOGIN;
		}

		return "cadastro";
	}

	@RequestMapping(path = "/completar-cadastro/{token}", method = RequestMethod.GET)
	public ModelAndView completarCadastroForm(@PathVariable("token") Token token) throws IllegalArgumentException {
		ModelAndView model = new ModelAndView();

		if (token.getAcao().equals(Constants.ACAO_COMPLETAR_CADASTRO)) {
			model.setViewName("completar-cadastro");
			model.addObject(PESSOA, token.getPessoa());
		} else {
			throw new IllegalArgumentException("O token passado não corresponde a ação de completar cadastro.");
		}

		return model;
	}

	@RequestMapping(path = "/completar-cadastro", method = RequestMethod.POST)
	public String completarCadastro(@Valid Pessoa pessoa, @RequestParam String senha,
			@RequestParam String senhaConfirma) {

		Pessoa pessoaDB = pessoaService.getByCpf(pessoa.getCpf());
		if (senha.equals(senhaConfirma)) {
			if (pessoaDB != null) {

				List<ParticipacaoEvento> participacaoEvento = participacaoEventoService
						.findParticipacaoEventoPorPessoa(pessoa.getId());
				List<ParticipacaoTrabalho> participacaoTrabalho = participacaoTrabalhoService
						.findParticipacaoTrabalhoPorPessoa(pessoa.getId());

				for (ParticipacaoEvento p : participacaoEvento) {
					p.setPessoa(pessoaDB);
					participacaoEventoService.adicionarOuEditarParticipacaoEvento(p);
				}

				for (ParticipacaoTrabalho p : participacaoTrabalho) {
					p.setPessoa(pessoaDB);
					participacaoTrabalhoService.adicionarOuEditar(p);
				}

				String password = pessoaService.encodePassword(senha);
				pessoaDB.setPassword(password);
				tokenService.deletar(tokenService.buscarPorUsuario(pessoa));
				pessoaService.delete(pessoa.getId());
				
				String emailUsuarioTemp = pessoa.getEmail();
				String nomeUsuarioTemp = pessoa.getNome();
				
				pessoaDB.setEmail(emailUsuarioTemp);
				pessoaDB.setNome(nomeUsuarioTemp);
				
				pessoaService.addOrUpdate(pessoaDB);

			} else {
				String password = pessoaService.encodePassword(senha);
				pessoa.setPassword(password);
				pessoaService.addOrUpdate(pessoa);
				tokenService.deletar(tokenService.buscarPorUsuario(pessoa));
			}
		}

		return Constants.REDIRECIONAR_PARA_LOGIN;
	}
	
	@RequestMapping(value = "/perfil")
	public String perfil(Model model){
		Pessoa pessoa = PessoaLogadaUtil.pessoaLogada();
		model.addAttribute(PESSOA, pessoa);
		return Constants.PERFIL_USER;
	}

	@RequestMapping(value = "/dashboard")
	public String dashboard(Model model) {
		Long idPessoaLogada = PessoaLogadaUtil.pessoaLogada().getId();
		List<Evento> eventosQueReviso = eventoService.getMeusEventosAtivosComoRevisor(idPessoaLogada);
		List<Evento> eventoQueOrganizo = eventoService.getMeusEventosAtivosComoOrganizador(idPessoaLogada);
		List<Evento> eventosAtivos = eventoService.buscarEventosAtivosEPublicos();
		List<Evento> eventosMinhaCoutoria = eventoService.getMeusEventosComoCoautor(idPessoaLogada);
		List<Evento> eventosQueSouAutor = eventoService.getMeusEventosComoAutor(idPessoaLogada);
		List<Evento> eventosInativos = eventoService.getMeusEventosInativosComoOrganizador(idPessoaLogada);
    
		model.addAttribute("eventosQueSouAutor", eventosQueSouAutor);
		model.addAttribute("eventosQueOrganizo", eventoQueOrganizo);
		model.addAttribute("eventos", eventosAtivos);
		model.addAttribute("eventosQueReviso", eventosQueReviso);
		model.addAttribute("eventosMinhaCoautoria", eventosMinhaCoutoria);
		model.addAttribute(PESSOA, PessoaLogadaUtil.pessoaLogada());
		model.addAttribute("eventosInativos", eventosInativos);
		return "dashboard";
	}

	@RequestMapping(path = "resetar-senha/{token}", method = RequestMethod.GET)
	public ModelAndView resetarSenhaForm(@PathVariable("token") Token token) throws IllegalArgumentException {
		ModelAndView model = new ModelAndView();
		if (token.getAcao().equals(Constants.ACAO_RECUPERAR_SENHA)) {
			model.setViewName("resetar_senha");
			model.addObject("token", token);
		} else {
			throw new IllegalArgumentException("O token passado não corresponde a ação de recuperar senha.");
		}
		return model;
	}

	@RequestMapping(path = "/resetar-senha/{token}", method = RequestMethod.POST)
	public String resetarSenha(@PathVariable("token") Token token, @RequestParam String senha,
			@RequestParam String senhaConfirma, RedirectAttributes redirectAttributes) {
		enviarEmailService.resetarSenhaEmail(token, senha, senhaConfirma, redirectAttributes);
		return Constants.REDIRECIONAR_PARA_LOGIN;
	}

	@RequestMapping(path = "/esqueci-minha-senha", method = RequestMethod.GET)
	public String esqueciSenhaForm() {
		return "esqueci_senha";
	}

	@RequestMapping(path = "/esqueci-minha-senha", method = RequestMethod.POST)
	public String esqueciSenha(@RequestParam String email, RedirectAttributes redirectAttributes,
			HttpServletRequest request) {

		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
		try {
			enviarEmailService.esqueciSenhaEmail(email, redirectAttributes, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		redirectAttributes.addFlashAttribute("esqueciSenha", true);
		return Constants.REDIRECIONAR_PARA_LOGIN;

	}

}
