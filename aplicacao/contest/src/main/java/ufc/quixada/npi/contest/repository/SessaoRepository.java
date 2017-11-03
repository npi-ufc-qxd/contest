package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Sessao;
@Repository
@Transactional
public interface SessaoRepository extends JpaRepository<Sessao, Long> {
	
	public List<Sessao> findByEvento(Evento evento);
	
}
