package ufc.quixada.npi.contest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

	@RequestMapping("/403")
	public String acessoNegado(){
		return "403";
	}
	
}
