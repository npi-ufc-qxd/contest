package ufc.quixada.npi.contest.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Secao;
@Repository
@Transactional
public interface SecaoRepository extends JpaRepository<Secao, Long> {
	
}
