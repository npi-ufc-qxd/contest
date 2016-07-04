package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

	public void delete(Integer id) {
		if (pessoaRepository.findOne(id) != null)
			pessoaRepository.delete(id);
	}

	public Pessoa get(Integer id) {
		return pessoaRepository.findOne(id);
	}

	public Pessoa getByCpf(String cpf) {
		return pessoaRepository.findByCpf(cpf);
	}

	public String encodePassword(String password){
		return passwordEncoder.encode(password);
	}

	public boolean autenticaBD(String cpf, String password) {
		if (pessoaRepository.findByCpfAndPassword(cpf, passwordEncoder.encode(password)) != null)
			return true;

		return false;
	}

	public boolean autentica(Pessoa pessoa, String cpf, String password) {
		if (pessoa.getCpf().equals(cpf) && passwordEncoder.matches(password, pessoa.getPassword()))
			return true;

		return false;
	}
}