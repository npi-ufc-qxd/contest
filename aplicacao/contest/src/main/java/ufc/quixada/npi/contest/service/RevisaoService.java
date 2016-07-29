package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.repository.RevisaoRepository;

@Service
public class RevisaoService {

	@Autowired
	private RevisaoRepository revisaoRepository;
	
	public boolean existeTrabalhoNesseEvento(Long id){
		return revisaoRepository.existTrabalhoNesseEvento(id);
	}
}