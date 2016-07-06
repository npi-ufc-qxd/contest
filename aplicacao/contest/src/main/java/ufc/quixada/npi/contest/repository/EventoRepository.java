package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;

@Repository
@Transactional
public interface EventoRepository extends CrudRepository<Evento, Long>{
	public List<Evento> findByEstadoEquals(EstadoEvento estado);
}
