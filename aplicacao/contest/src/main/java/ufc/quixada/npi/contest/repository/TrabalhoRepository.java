package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Trabalho;

@Repository
@Transactional
public interface TrabalhoRepository extends CrudRepository<Trabalho, Long>{
	public List<Trabalho> findByEvento(Evento evento);
}