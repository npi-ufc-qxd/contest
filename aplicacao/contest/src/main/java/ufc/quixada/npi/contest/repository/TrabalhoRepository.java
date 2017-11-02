package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;

@Repository
@Transactional
public interface TrabalhoRepository extends JpaRepository<Trabalho, Long>{
	public List<Trabalho> getByEvento(Evento evento);
	
	@Query("SELECT t FROM Trabalho t WHERE t.evento.id = :idEvento AND t.id in(SELECT pt.trabalho.id FROM ParticipacaoTrabalho"
			+ " pt where pt.pessoa.id = :idRevisor AND pt.papel = ufc.quixada.npi.contest.model.Papel$Tipo.REVISOR)")
	public List<Trabalho> getTrabalhosParaRevisar(@Param("idRevisor") Long idRevisor, @Param("idEvento") Long idEvento);
	
	@Query("SELECT p FROM Pessoa p WHERE p.id in "
			+ "(SELECT pt.pessoa.id FROM ParticipacaoTrabalho pt WHERE pt.trabalho.id = :idTrabalho AND "
			+ "pt.papel = ufc.quixada.npi.contest.model.Papel$Tipo.AUTOR)")
	public List<Pessoa> getAutoresDoTrabalho(@Param("idTrabalho") Long idTrabalho);
	
	@Query("select case when count(*) > 0 then true else false end "
			+ "FROM Trabalho as t  WHERE t.trilha.id = :trilhaId")
	public boolean existsTrilhaId(@Param("trilhaId") Long trilhaId);
	
	public List<Trabalho> findByTrilha(Trilha trilha);
	
	@Query("SELECT t FROM Trabalho t WHERE t.evento.id = :eventoId AND t.id in (SELECT pt.trabalho.id FROM ParticipacaoTrabalho"
			+ " pt where pt.pessoa.id = :autorId AND pt.papel = ufc.quixada.npi.contest.model.Papel$Tipo.AUTOR)")
	public List<Trabalho> getTrabalhoDoAutorNoEvento(@Param("autorId") Long pessoaId, @Param("eventoId") Long eventoId);
	
	@Query("SELECT t FROM Trabalho t WHERE t.evento.id = :eventoId AND t.id in (SELECT pt.trabalho.id FROM ParticipacaoTrabalho"
			+ " pt where pt.pessoa.id = :autorId AND pt.papel = ufc.quixada.npi.contest.model.Papel$Tipo.COAUTOR)")
	public List<Trabalho> getTrabalhoDoCoautorNoEvento(@Param("autorId") Long pessoaId, @Param("eventoId") Long eventoId);
	
	@Query("SELECT t FROM Trabalho t WHERE t.evento.id = :eventoId AND t.id in (SELECT pt.trabalho.id FROM ParticipacaoTrabalho"
			+ " pt where pt.pessoa.id = :autorId AND "
			+ "(pt.papel = ufc.quixada.npi.contest.model.Papel$Tipo.COAUTOR or pt.papel = ufc.quixada.npi.contest.model.Papel$Tipo.AUTOR))")
	public List<Trabalho> getTrabalhoComoAutorECoautorNoEvento(@Param("autorId") Long autorId, @Param("eventoId") Long eventoId);
	
	@Query("SELECT t FROM Trabalho t WHERE t.id in (SELECT pt.trabalho.id FROM ParticipacaoTrabalho"
			+ " pt where pt.pessoa.id = :coautorId AND pt.papel = ufc.quixada.npi.contest.model.Papel$Tipo.COAUTOR)")
	public List<Trabalho> getTrabalhoDoCoautor(@Param("coautorId") Long pessoaId);
	
	@Query("SELECT t FROM Trabalho t WHERE t.id in (SELECT pt.trabalho.id FROM ParticipacaoTrabalho"
			+ " pt where pt.pessoa.id = :autorId AND pt.papel = ufc.quixada.npi.contest.model.Papel$Tipo.AUTOR)")
	public List<Trabalho> getTrabalhoDoAutor(@Param("autorId") Long pessoaId);
	
	@Query("SELECT t FROM Trabalho t WHERE t.evento.id = :eventoId AND t.id in (SELECT rev.trabalho.id FROM Revisao"
			+ " rev where rev.trabalho.id = t.id)")
	public List<Trabalho> getTrabalhoRevisadoEvento(@Param("eventoId") Long eventoId);
	
	@Query("SELECT count(*) FROM Trabalho t WHERE t.evento.id = :eventoId AND t.id in (SELECT rev.trabalho.id FROM Revisao"
			+ " rev where rev.trabalho.id = t.id AND rev.observacoes <> '')")
	public int getTrabalhoRevisadoComentadoEvento(@Param("eventoId") Long eventoId);
	
	public List<Trabalho> findAllByEventoId(Long eventoID);

	public List<Trabalho> findTrabalhoBySecaoId(Long idSecao);
	
}