package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PessoaRepository pessoaRepository;

	public void addOrUpdate(Pessoa pessoa) {
		pessoaRepository.save(pessoa);
	}

	public List<Pessoa> list() {
		return (List<Pessoa>) pessoaRepository.findAll();
	}

	public boolean delete(Long id) {
		if (pessoaRepository.findOne(id) != null) {
			pessoaRepository.delete(id);
			return true;
		}

		return false;
	}

	public Pessoa get(Long id) {
		return pessoaRepository.findOne(id);
	}

	public Pessoa getByCpf(String cpf) {
		return pessoaRepository.findByCpf(cpf);
	}
	public Pessoa getByEmail(String email){
		return pessoaRepository.findByEmail(email);
	}
	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	public boolean autenticaBD(String cpf, String password) {
		return (pessoaRepository.findByCpfAndPassword(cpf, passwordEncoder.encode(password)) != null);
	}

	public boolean autentica(Pessoa pessoa, String cpf, String password) {
		return (pessoa.getCpf().equals(cpf) && passwordEncoder.matches(password, pessoa.getPassword()));
	}
	
	public List<Pessoa> getPossiveisOrganizadores(){
		return pessoaRepository.getPossiveisOrganizadores();
	}
	
	public List<Pessoa> pessoasPorPapelNoEvento(Papel papel, Long idEvento){
		return pessoaRepository.pessoasPorPapelNoEvento(papel.getPapel(), idEvento);
	}
}