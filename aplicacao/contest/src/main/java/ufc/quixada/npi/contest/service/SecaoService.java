package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Secao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.repository.SecaoRepository;

@Service
public class SecaoService {
	@Autowired
	private SecaoRepository secaoRepository;
	
	public void addOrUpdate(Secao secao) {
		secaoRepository.save(secao);
	}

	public List<Secao> list() {
		return (List<Secao>) secaoRepository.findAll();
	}

	public boolean delete(Long id) {
		if (secaoRepository.findOne(id) != null) {
			secaoRepository.delete(id);
			return true;
		}

		return false;
	}

	public Secao get(Long id) {
		return secaoRepository.findOne(id);
	}

	public void removerTrablahoSecao(Long idSecao, Trabalho trabalho) {
		Secao secao = secaoRepository.findOne(idSecao);
		secao.getTrabalhos().remove(trabalho);
		addOrUpdate(secao);
	}
}
