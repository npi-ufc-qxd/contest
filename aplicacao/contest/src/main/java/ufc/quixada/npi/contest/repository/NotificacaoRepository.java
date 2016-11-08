package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Notificacao;
import ufc.quixada.npi.contest.model.Pessoa;
@Repository
@Transactional
public interface NotificacaoRepository extends CrudRepository<Notificacao, Long>{
	public List<Notificacao> findByPessoa(Pessoa pessoa);
	public Notificacao findById(Long id);
	public List<Notificacao> findNotificacaoByNovaAndPessoa(boolean nova, Pessoa pessoa);
}