package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Secao;
import ufc.quixada.npi.contest.repository.SessaoRepository;

@Service
public class SessaoService {
	@Autowired
	private SessaoRepository sessaoRepository;
	
	public void addOrUpdate(Secao secao) {
		sessaoRepository.save(secao);
	}

	public List<Secao> list() {
		return (List<Secao>) sessaoRepository.findAll();
	}

	public void delete(Long id) {
		Secao secao = sessaoRepository.findOne(id);
		sessaoRepository.delete(secao);
	}

	public Secao get(Long id) {
		return sessaoRepository.findOne(id);
	}
	
	public List<Secao> listByEvento(Evento e){
		return sessaoRepository.findByEvento(e);	
	}
}
