package ufc.quixada.npi.contest.service;

import java.util.ArrayList;
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
	
	private TrabalhoService trabalhoService;
	
	public void addOrUpdate(Secao secao) {
		secaoRepository.save(secao);
	}

	public List<Secao> list() {
		return (List<Secao>) secaoRepository.findAll();
	}

	public void delete(Long id) {
		Secao secao = secaoRepository.findOne(id);
		secaoRepository.delete(secao);
	}

	public Secao get(Long id) {
		return secaoRepository.findOne(id);
	}
	
	public void adicionarTrabalhoSecao(Long idSecao,Trabalho trabalho){
		Secao secao = secaoRepository.findOne(idSecao);
		List<Trabalho> listAux = secao.getTrabalhos();
		listAux.add(trabalho);
		secao.setTrabalhos(listAux);
	}
}
