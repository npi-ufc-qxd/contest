package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;

@Repository
@Transactional
public interface EventoRepository extends CrudRepository<Evento, Long>{
	public List<Evento> findByEstadoEquals(EstadoEvento estado);
	@Query("select e from Evento e where e.estado = ufc.quixada.npi.contest.model.EstadoEvento.ATIVO and "
			+ "e.visibilidade = ufc.quixada.npi.contest.model.VisibilidadeEvento.PUBLICO")
	public List<Evento> findEventosAtivosEPublicos();
	
	@Query("SELECT e FROM Evento e " + 
	"WHERE e.id NOT in ( SELECT DISTINCT pe.evento.id FROM ParticipacaoEvento pe WHERE :idPessoa = pe.pessoa.id) "
	+ "AND e.visibilidade = ufc.quixada.npi.contest.model.VisibilidadeEvento.PUBLICO "
	+ "AND e.estado = ufc.quixada.npi.contest.model.EstadoEvento.ATIVO "+
	"ORDER BY e.id")
	public List<Evento> eventosParaParticipar(@Param("idPessoa") Long idPessoa);
	
	@Query("SELECT e FROM Evento e " + 
			"WHERE e.id in ( SELECT DISTINCT pe.evento.id FROM ParticipacaoEvento pe WHERE :idAutor = pe.pessoa.id) "
			+ "AND  e.visibilidade = ufc.quixada.npi.contest.model.VisibilidadeEvento.PUBLICO "+
			"ORDER BY e.id")
	public List<Evento> findEventosDoAutor(@Param("idAutor") Long idAutor);
	
	@Query("SELECT e FROM Evento e " + 
			"WHERE e.id in (SELECT DISTINCT pe.evento.id FROM ParticipacaoEvento pe WHERE :idRevisor = pe.pessoa.id) "
			+ "AND  e.visibilidade = ufc.quixada.npi.contest.model.VisibilidadeEvento.PUBLICO "+
			"ORDER BY e.id")
	public List<Evento> findEventosDoRevisor(@Param("idRevisor") Long idRevisor);
	
	public List<Evento> findEventoByEstado(EstadoEvento estadoEvento);
}