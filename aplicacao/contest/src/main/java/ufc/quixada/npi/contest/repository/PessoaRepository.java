	package ufc.quixada.npi.contest.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends CrudRepository<Pessoa, Long>{
	public Pessoa findByCpf(String cpf);
	public Pessoa findByEmail(String email);
	public Pessoa findByCpfAndPassword(String cpf, String password);
	
	@Query("select p from Pessoa p where p.papelLdap = ufc.quixada.npi.contest.model.PapelLdap$Tipo.DOCENTE or "
			+ "p.papelLdap = ufc.quixada.npi.contest.model.PapelLdap$Tipo.STA")
	public List<Pessoa> getPossiveisOrganizadores();
	
}