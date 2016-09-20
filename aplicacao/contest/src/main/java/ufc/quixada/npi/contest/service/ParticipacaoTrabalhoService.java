package ufc.quixada.npi.contest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.repository.ParticipacaoTrabalhoRepository;

@Service
public class ParticipacaoTrabalhoService {
	@Autowired
	private ParticipacaoTrabalhoRepository participacaoTrabalhoRepository;
	
	public void adicionarOuEditar(ParticipacaoTrabalho participacaoTrabalho){
		participacaoTrabalhoRepository.save(participacaoTrabalho);
	}
	
}