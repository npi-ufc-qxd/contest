package ufc.quixada.npi.contest.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.repository.TrilhaRepository;

@Service
public class TrilhaService {
	
	@Autowired
	private TrilhaRepository trilhaRepository;
	
	public void adicionarOuAtualizarTrilha(Trilha trilha) {
		trilhaRepository.save(trilha);
	}

	public boolean removerTrilha(Long id) {
		if (trilhaRepository.findOne(id) != null) {
			trilhaRepository.delete(id);
			return true;
		}
		return false;
	}
	
	public List<Trilha> buscarTrilhas(Long id) {
		List<Trilha> trilhas = new ArrayList<>();
		trilhas = trilhaRepository.findAllByEventoId(id);
		return trilhas;
	}
	
	public Trilha get(Long id) {
		return trilhaRepository.findOne(id);
	}
	
	public boolean exists(String nome, Long eventoId){
		return trilhaRepository.findByNomeAndEvento_id(nome, eventoId);
	}
	
}