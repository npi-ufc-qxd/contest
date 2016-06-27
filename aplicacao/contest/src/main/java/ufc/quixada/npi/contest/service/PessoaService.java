package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.repository.PessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public Pessoa findPessoaPorId(Long id){
		return pessoaRepository.findOne(id);
	}
	
}
