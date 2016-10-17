package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;

@Repository
@Transactional
public interface TrabalhoRepository extends CrudRepository<Trabalho, Long>{
	public List<Trabalho> findByEvento(Evento evento);
	
	@Query("SELECT t FROM Trabalho t WHERE t.evento.id = :idEvento AND t.id in(SELECT pt.trabalho.id FROM ParticipacaoTrabalho"
			+ " pt where pt.pessoa.id = :idRevisor)")
	public List<Trabalho> getTrabalhosParaRevisar(@Param("idRevisor") Long idRevisor, @Param("idEvento") Long idEvento);

	@Query("SELECT p FROM Pessoa p WHERE p.id in "
			+ "(SELECT pt.pessoa.id FROM ParticipacaoTrabalho pt WHERE pt.trabalho.id = :idTrabalho AND "
			+ "pt.papel = ufc.quixada.npi.contest.model.Papel.AUTOR)")
	public List<Pessoa> getAutoresDoTrabalho(@Param("idTrabalho") Long idTrabalho);
	
	@Query("select case when count(*) > 0 then true else false end "
			+ "FROM Trabalho as t  WHERE t.trilha.id = :trilhaId")
	public boolean existsTrilhaId(@Param("trilhaId") Long trilhaId);
	
	public List<Trabalho> findByTrilha(Trilha trilha);
	
	public List<Trabalho> findByParticipacoesPessoaAndEvento(Pessoa pessoa,Evento evento);
	
	public List<Trabalho> findAllByEventoId(Long eventoID);
}