package ufc.quixada.npi.contest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.PapelLdap;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

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
	public List<Pessoa> getPossiveisOrganizadoresDoEvento(Long idEvento){
		return pessoaRepository.getPossiveisOrganizadoresDoEvento(idEvento);
	}
	
	public List<Pessoa> pessoasPorPapelNoEvento(PapelLdap.Tipo papel, Long idEvento){
		return pessoaRepository.pessoasPorPapelNoEvento(papel, idEvento);
	}
	
	public List<Pessoa> revisoresNoEvento(Long idEvento){
		List<Pessoa> revisores = new ArrayList<>();
		revisores.addAll(pessoaRepository.pessoasPorPapelNoEvento(PapelLdap.Tipo.DOCENTE, idEvento));
		revisores.addAll(pessoaRepository.pessoasPorPapelNoEvento(PapelLdap.Tipo.STA, idEvento));
		return revisores;
	}
	
	public List<Pessoa> getRevisoresEvento(Long idEvento){
		return pessoaRepository.findPessoaByParticipacoesEventoEventoIdAndParticipacoesEventoPapel(idEvento, Tipo.REVISOR);
	}
	
	public List<Pessoa> getRevisoresDoEventoQueNaoParticipaDoTrabalho(Long idEvento){
		return pessoaRepository.findPessoaByParticipacoesEventoPapelAndParticipacoesEventoEventoIdOrderByParticipacoesEventoPessoaNome(Tipo.REVISOR, idEvento);
	}
	
	public List<Pessoa> getOrganizadoresEvento(Long idEvento){
		return pessoaRepository.findPessoaByParticipacoesEventoEventoIdAndParticipacoesEventoPapel(idEvento, Tipo.ORGANIZADOR);
	}
	
	
	public List<Pessoa> getTodosInEvento(Evento e){
		List<Pessoa> pessoas = new ArrayList<>();
		List<ParticipacaoEvento> participacoes = participacaoEventoService.getParticipacoesPorEvento(e);
		
		for(ParticipacaoEvento partipacao : participacoes){
			pessoas.add(partipacao.getPessoa());
		}
		
		return pessoas;		
	}
	
}