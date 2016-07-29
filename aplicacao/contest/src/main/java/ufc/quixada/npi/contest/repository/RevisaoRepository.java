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
	@Query("select case when count(*) > 0 then true else false end "
			+ "FROM Revisao as r, Trabalho as t  WHERE r.trabalho.id = t.id and t.evento.id = :idEvento")
	public boolean existTrabalhoNesseEvento(@Param("idEvento") Long idEvento);
}