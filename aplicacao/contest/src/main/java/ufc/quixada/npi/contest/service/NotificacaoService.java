package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Notificacao;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.repository.NotificacaoRepository;
@Service
public class NotificacaoService {

	@Autowired
	private NotificacaoRepository notificacaoRepository;
	
	public List<Notificacao> listaNotificacaoDePessoa(Pessoa pessoa){
		return notificacaoRepository.findNotificacaoByNovaAndPessoa(true, pessoa);
	} 
	
	public void adicionarNotificacao(Notificacao notificacao) {
		notificacaoRepository.save(notificacao);
	}

	public Notificacao getNotificacaoById(Long id){
		return notificacaoRepository.findById(id);
	}
}