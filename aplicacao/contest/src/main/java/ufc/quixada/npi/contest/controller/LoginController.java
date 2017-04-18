package ufc.quixada.npi.contest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.service.EnviarEmailService;

@Controller
public class LoginController {
	@Autowired
	EnviarEmailService emailService;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET  )
	public String login() {
		//Linha ADICIONADA PARA TESTAR ENVIAR EMAIL
		emailService.enviarEmail("Teste Titulo","Teste Assunto","carlos_matheus95@hotmail.com","Teste Corpo");
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

}