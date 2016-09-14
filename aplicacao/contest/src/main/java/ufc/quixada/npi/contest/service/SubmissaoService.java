package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.repository.SubmissaoRepository;;

@Service
public class SubmissaoService {

	@Autowired
	private SubmissaoRepository submissaoRepository;
	
	public boolean existeTrabalhoNesseEvento(Long id){
		return submissaoRepository.existTrabalhoNesseEvento(id);
	}
	
	public void adicionarOuEditar(Submissao submissao){
		submissaoRepository.save(submissao);
	}
}