package ufc.quixada.npi.contest.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ufc.quixada.npi.contest.util.Constants;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Authentication auth) {
		if(auth != null && auth.isAuthenticated()) {
			return "redirect:/";
		}

		return "login";
	}

	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginfailed(Model model) {
		model.addAttribute("loginError", Constants.LOGIN_INVALIDO);

		return "login";
	}

}