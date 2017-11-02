package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;

@Repository
@Transactional
public interface RevisaoRepository extends CrudRepository<Revisao, Long>{
	
	@Query("select case when count(*) > 0 then true else false end "
			+ "FROM Revisao as r, Trabalho as t  WHERE r.trabalho.id = t.id and t.evento.id = :idEvento")
	public boolean existTrabalhoNesseEvento(@Param("idEvento") Long idEvento);
	
	@Query("select case when count(*) > 0 then true else false end "
			+ "FROM Revisao as r WHERE r.trabalho.id = :idTrabalho AND r.revisor.id = :idRevisor")
	public boolean trabalhoEstaRevisadoPeloRevisor(@Param("idTrabalho") Long idTrabalho, @Param("idRevisor") Long idRevisor);
	
	public List<Revisao> findRevisaoByTrabalho(Trabalho trabalho);
	
	@Query("select r FROM Revisao as r WHERE r.trabalho.id = :idTrabalho AND r.revisor.id = :idRevisor")
	public Revisao findRevisaoDoAutorNoTrabalho(@Param("idTrabalho") Long idTrabalho, @Param("idRevisor") Long idRevisor);
	
	public List<Revisao> findRevisaoByTrabalho(Revisao revisao);
	
	@Query("select r "
			+ "FROM Revisao r  WHERE r.trabalho.id in (select t.id from Trabalho t where t.evento.id = :idEvento)")
	public List<Revisao> revisoesEvento(@Param("idEvento") Long evento );

	
	
}