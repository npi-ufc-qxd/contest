package ufc.quixada.npi.contest.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Submissao;

@Repository
@Transactional
public interface SubmissaoRepository extends CrudRepository<Submissao, Long>{
	@Query("select COUNT(*) from Submissao s where exists (select t from Trabalho t where "
			+ "s.trabalho.id = t.id and t.evento.id = :idEvento)")
	public int existTrabalhoNesseEvento(@Param("idEvento") Long idEvento);
}