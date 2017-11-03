package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Sessao;
import ufc.quixada.npi.contest.repository.SessaoRepository;

@Service
public class SessaoService {
	@Autowired
	private SessaoRepository sessaoRepository;
	
	public void addOrUpdate(Sessao sessao) {
		sessaoRepository.save(sessao);
	}

	public List<Sessao> list() {
		return (List<Sessao>) sessaoRepository.findAll();
	}

	public void delete(Long id) {
		Sessao sessao = sessaoRepository.findOne(id);
		sessaoRepository.delete(sessao);
	}

	public Sessao get(Long id) {
		return sessaoRepository.findOne(id);
	}
	
	public List<Sessao> listByEvento(Evento e){
		return sessaoRepository.findByEvento(e);	
	}
}
