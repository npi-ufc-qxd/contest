package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;

@Repository
@Transactional
public interface ParticipacaoEventoRepository extends JpaRepository<ParticipacaoEvento, Long>{
	public List<ParticipacaoEvento> findByEventoEstadoAndPapelAndPessoaId(EstadoEvento estadoEvento, Tipo papel, Long id);
	public List<ParticipacaoEvento> findByEventoIdAndPapel(Long id, Tipo papel);
	public ParticipacaoEvento findOneByEventoAndPessoaAndPapel(Evento evento, Pessoa pessoa, Tipo papel);
	public ParticipacaoEvento findOneByEventoAndPessoa(Evento evento, Pessoa pessoa);
}