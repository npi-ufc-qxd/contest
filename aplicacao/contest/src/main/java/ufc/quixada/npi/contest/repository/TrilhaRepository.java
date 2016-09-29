package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ufc.quixada.npi.contest.model.Trilha;

@Repository
@Transactional
public interface TrilhaRepository extends CrudRepository<Trilha, Long>{

	public List<Trilha> findAllByEventoId(Long eventoId);
	
	@Query("select case when count(*) > 0 then true else false end "
			+ "FROM Trilha as t  WHERE t.nome = :nome and t.evento.id = :eventoId")
	public boolean findByNomeAndEventoId(@Param("nome") String nome, @Param("eventoId") Long eventoId);
	
	@Query("select t FROM Trilha t  WHERE t.id = :trilhaId and t.evento.id = :eventoId")
	public Trilha findByTrilhaIdAndEventoId(@Param("trilhaId") Long trilhaId, @Param("eventoId") Long eventoId);
	
	
	
}
