package ufc.quixada.npi.contest.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ufc.quixada.npi.contest.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends CrudRepository<Pessoa, Integer>{
	Pessoa findByCpf(String cpf);

	Pessoa findByCpfAndPassword(String cpf, String password);
}