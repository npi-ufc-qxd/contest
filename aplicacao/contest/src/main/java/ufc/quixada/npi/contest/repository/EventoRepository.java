package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;

@Repository
@Transactional
public interface EventoRepository extends CrudRepository<Evento, Long>{
	public List<Evento> findEventoByEstadoAndVisibilidade(EstadoEvento estado, VisibilidadeEvento visibilidade);
	
	@Query("select e from Evento e where e.estado = ufc.quixada.npi.contest.model.EstadoEvento.ATIVO and "
			+ "e.visibilidade = ufc.quixada.npi.contest.model.VisibilidadeEvento.PUBLICO")
	public List<Evento> findEventosAtivosEPublicos();
	
	@Query("SELECT e FROM Evento e " + 
	"WHERE e.id NOT in ( SELECT DISTINCT pe.evento.id FROM ParticipacaoEvento pe WHERE :idPessoa = pe.pessoa.id) "
	+ "AND e.visibilidade = ufc.quixada.npi.contest.model.VisibilidadeEvento.PUBLICO "
	+ "AND e.estado = ufc.quixada.npi.contest.model.EstadoEvento.ATIVO "+
	"ORDER BY e.id")
	public List<Evento> eventosParaParticipar(@Param("idPessoa") Long idPessoa);
	
	public List<Evento> findDistinctEventoByParticipacoesPessoaIdAndVisibilidade(Long id, VisibilidadeEvento visbilidade);
	
	public List<Evento> findEventoByParticipacoesPessoaIdAndParticipacoesPapelAndVisibilidadeAndEstado(Long id,Tipo papel,VisibilidadeEvento visibilidade, EstadoEvento estado);
	
	public List<Evento> findEventoByParticipacoesPessoaIdAndParticipacoesPapelAndEstado(Long id, Tipo papel, EstadoEvento estado);
	
	public List<Evento> findEventoByEstado(EstadoEvento estadoEvento);
	
	@Query("select case when count(*) > 0 then true else false end "
			+ "FROM Revisao as r, Trabalho as t  WHERE r.trabalho.id = t.id and t.evento.id = :idEvento")
	public boolean organizadorParticipaEvento(@Param("idEvento") Long idEvento);
}