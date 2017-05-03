package ufc.quixada.npi.contest.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
	
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
	
	@RequestMapping(value = "/cadastro", method = RequestMethod.GET)
	public String cadastro() {
		return "cadastro";
	}

}