package ufc.quixada.npi.contest.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Revisao;

@Repository
@Transactional
public interface RevisaoRepository extends CrudRepository<Revisao, Long>{
	@Query("select COUNT(*) from Revisao r where exists (select t from Trabalho t where "
			+ "r.trabalho.id = t.id and t.evento.id = :idEvento)")
	public int existTrabalhoNesseEvento(@Param("idEvento") Long idEvento);
}