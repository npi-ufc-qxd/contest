package ufc.quixada.npi.contest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/participacaoevento")
public class ParticipacaoEventoController {
	
	
	@RequestMapping(value="/professor", method = RequestMethod.GET)
	public String professorParticipa(Model model){
		return "/";
	}
}
