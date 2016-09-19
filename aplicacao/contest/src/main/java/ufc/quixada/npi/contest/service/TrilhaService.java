package ufc.quixada.npi.contest.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.repository.TrilhaRepository;

@Service
public class TrilhaService {
	
	@Autowired
	private TrilhaRepository trilhaRepository;
	
	public void adicionarOuAtualizarTrilha(Trilha trilha) {
		trilhaRepository.save(trilha);
	}

	public boolean removerTrilha(Trilha trilha) {
		trilhaRepository.delete(trilha);
		return false;
	}
	
	public List<Trilha> buscarTrilhas(Long id) {
		List<Trilha> trilhas = new ArrayList<>();
		trilhas = trilhaRepository.findAllByEventoId(id);
		return trilhas;
	}
	
	public Trilha get(Long trilhaId, Long eventoId) {
		return trilhaRepository.findByTrilhaIdAndEventoId( trilhaId, eventoId);
	}
	
	public boolean exists(String nome, Long eventoId){
		return trilhaRepository.findByNomeAndEventoId(nome, eventoId);
	}
	
}