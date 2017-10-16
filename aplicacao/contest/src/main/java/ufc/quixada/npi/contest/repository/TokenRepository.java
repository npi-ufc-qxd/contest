package ufc.quixada.npi.contest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Token;

public interface TokenRepository extends JpaRepository<Token, String> {

	Token findByPessoa(Pessoa pessoa);
}