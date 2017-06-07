package ufc.quixada.npi.contest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ufc.quixada.npi.contest.model.*;

public interface TokenRepository extends JpaRepository<Token, String> {

	Token findByUsuario(Pessoa pessoa);
}