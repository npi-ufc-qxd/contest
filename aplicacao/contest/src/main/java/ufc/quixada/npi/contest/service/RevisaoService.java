package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.repository.RevisaoRepository;

@Service
public class RevisaoService {

	@Autowired
	private RevisaoRepository revisaoRepository;
	
	public void addOrUpdate(Revisao revisao) {
		revisaoRepository.save(revisao);
	}
	
	public boolean existeTrabalhoNesseEvento(Long id){
		return revisaoRepository.existTrabalhoNesseEvento(id);
	}
	
	public Revisao getRevisaoByTrabalho(Trabalho trabalho){
		return revisaoRepository.findRevisaoByTrabalho(trabalho);
	}
	
	public Boolean existeTrabalhoEmRevisao(Long trabalhoId){
		return revisaoRepository.existRevisaoByTrabalhoId(trabalhoId);
	}
	
	public void adicionarOuAtualizarRevisao(Revisao revisao) {
		revisaoRepository.save(revisao);
	}

}