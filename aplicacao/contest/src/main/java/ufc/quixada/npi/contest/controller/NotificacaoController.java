package ufc.quixada.npi.contest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Notificacao;
import ufc.quixada.npi.contest.model.NotificacaoJson;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.NotificacaoService;
import ufc.quixada.npi.contest.util.Constants;

@Controller
@RequestMapping("/notificacao")
public class NotificacaoController {
	

	@Autowired
	private NotificacaoService notificacaoService;

	@RequestMapping(value = "/removerNotificacao", method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String removerNotificacao(@RequestBody NotificacaoJson dadosNotificao){
		Notificacao notificacao = notificacaoService.getNotificacaoById(dadosNotificao.getNotificacaoId());
		notificacao.setNova(false);
		notificacaoService.adicionarNotificacao(notificacao);
		
		return "{\"result\":\"ok\"}";
	}
	
	
	
}
