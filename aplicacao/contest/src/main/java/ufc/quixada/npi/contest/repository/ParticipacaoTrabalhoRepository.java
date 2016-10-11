package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Trabalho;

@Repository
@Transactional
public interface ParticipacaoTrabalhoRepository extends CrudRepository<ParticipacaoTrabalho, Long>{
	public List<ParticipacaoTrabalho> findByTrabalho(Trabalho trabalho);
}