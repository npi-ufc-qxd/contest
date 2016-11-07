package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.repository.ParticipacaoTrabalhoRepository;

@Service
public class ParticipacaoTrabalhoService {
	@Autowired
	private ParticipacaoTrabalhoRepository participacaoTrabalhoRepository;
	
	public void adicionarOuEditar(ParticipacaoTrabalho participacaoTrabalho){
		participacaoTrabalhoRepository.save(participacaoTrabalho);
	}
	
	public void remover(ParticipacaoTrabalho participacaoTrabalho){
		participacaoTrabalhoRepository.delete(participacaoTrabalho);
	}
	
	public List<ParticipacaoTrabalho> getParticipacaoTrabalhoByTrabalho(Trabalho trabalho){
		return participacaoTrabalhoRepository.findByTrabalho(trabalho);
	}
	
	public ParticipacaoTrabalho getParticipacaoTrabalhoRevisor(Long idRevisor, Long idTrabalho){
		return participacaoTrabalhoRepository.findParticipacaoTrabalhoByPapelAndPessoaIdAndTrabalhoId(Papel.REVISOR, idRevisor, idTrabalho);
	}
	
}