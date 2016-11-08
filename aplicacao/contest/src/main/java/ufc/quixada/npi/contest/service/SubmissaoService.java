package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.repository.SubmissaoRepository;;

@Service
public class SubmissaoService {

	@Autowired
	private SubmissaoRepository submissaoRepository;
	
	public boolean existeTrabalhoNesseEvento(Long id){
		return submissaoRepository.existTrabalhoNesseEvento(id);
	}
	
	public boolean existeTrabalhoFinalNesseEvento(Long id){
		return submissaoRepository.existTrabalhoFinalNesseEvento(id);
	}
	
	public Submissao getSubmissaoByTrabalho(Trabalho trabalho){
		return submissaoRepository.findByTrabalho(trabalho);
	}
	
	public boolean adicionarOuEditar(Submissao submissao){
		if(submissao != null){
			submissaoRepository.save(submissao);
			return true;
		}
		return false;
	}
	
	public Submissao getSubmissaoByTrabalho(Long id){
		return submissaoRepository.findSubmissaoByTrabalhoId(id);
	}
}