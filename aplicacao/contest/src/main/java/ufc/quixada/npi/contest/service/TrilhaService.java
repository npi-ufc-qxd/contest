package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.repository.TrabalhoRepository;
import ufc.quixada.npi.contest.repository.TrilhaRepository;

@Service
public class TrilhaService {
	
	@Autowired
	private TrilhaRepository trilhaRepository;
	
	@Autowired
	private TrabalhoRepository trabalhoRepository;
	
	public void adicionarOuAtualizarTrilha(Trilha trilha) {
		trilhaRepository.save(trilha);
	}

	public void removerTrilha(Trilha trilha) {
		trilhaRepository.delete(trilha);
	}
	
	public List<Trilha> buscarTrilhas(Long id) {
		return trilhaRepository.findAllByEventoId(id);
	}
	
	public Trilha get(Long trilhaId, Long eventoId) {
		return trilhaRepository.findByTrilhaIdAndEventoId( trilhaId, eventoId);
	}
	
	public boolean exists(String nome, Long eventoId){
		return trilhaRepository.findByNomeAndEventoId(nome, eventoId);
	}
	
	public boolean existeTrabalho(Long id){
		return trabalhoRepository.existsTrilhaId(id);
	}
	
	public int buscarQtdTrilhasPorEvento(Long eventoId){
		return trilhaRepository.findAllByEventoId(eventoId).size();
	}
	public boolean existeTrilha(Long id){
		return trilhaRepository.exists(id);
	}
	
}