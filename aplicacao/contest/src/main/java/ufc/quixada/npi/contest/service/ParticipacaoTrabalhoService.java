package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.Papel.Tipo;
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
	
	public List<ParticipacaoTrabalho> getParticipacaoTrabalhoPorAutorId(Long idAutor){
		return participacaoTrabalhoRepository.findParticipacaoTrabalhoByPapelAndPessoaId(Papel.Tipo.AUTOR, idAutor);
	}
	
	public List<ParticipacaoTrabalho> getParticipacaoTrabalhoPorCoautorId(Long idCoautor){
		return participacaoTrabalhoRepository.findParticipacaoTrabalhoByPapelAndPessoaId(Papel.Tipo.COAUTOR, idCoautor);
	}
	
	public List<ParticipacaoTrabalho> getTrabalhosPorRevisorId(Long idRevisor){
		return participacaoTrabalhoRepository.findParticipacaoTrabalhoByPapelAndPessoaId(Papel.Tipo.REVISOR, idRevisor);
	}
	
	public ParticipacaoTrabalho getParticipacaoTrabalhoRevisor(Long idRevisor, Long idTrabalho){
		return participacaoTrabalhoRepository.findParticipacaoTrabalhoByPapelAndPessoaIdAndTrabalhoId(Tipo.REVISOR, idRevisor, idTrabalho);
	}
	
	public boolean isParticipandoDoTrabalho(Long idTrabalho, Long idPessoa){
		return participacaoTrabalhoRepository.
				findParticipacaoTrabalhoByPessoaIdAndTrabalhoId(idPessoa, idTrabalho) != null ?
						true : false;
	}
}