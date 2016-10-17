package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;

@Repository
@Transactional
public interface ParticipacaoEventoRepository extends CrudRepository<ParticipacaoEvento, Long>{
	public ParticipacaoEvento findByEventoId(Long idEvento);
	//public List<ParticipacaoEvento> findDistinctByEventoEstadoAndPapel(EstadoEvento estadoEvento, Papel papel);
	public List<ParticipacaoEvento> findByEventoEstadoAndPapelAndPessoaId(EstadoEvento estadoEvento, Papel papel, Long id);
}