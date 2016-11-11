package ufc.quixada.npi.contest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Revisao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.repository.RevisaoRepository;
import ufc.quixada.npi.contest.repository.TrabalhoRepository;

@Service
public class TrabalhoService {
	
	@Autowired
	private TrabalhoRepository trabalhoRepository;
	
	@Autowired
	private RevisaoRepository revisaoRepository;
	
	public Trabalho getTrabalhoById(Long idTrabalho){
		return trabalhoRepository.findOne(idTrabalho);
	}
	
	public boolean existeTrabalho(Long idTrabalho){
		return trabalhoRepository.exists(idTrabalho);
	}
	
	public List<Trabalho> getTrabalhosEvento(Evento evento){
		return trabalhoRepository.findByEvento(evento);
	}
	
	public List<Trabalho> getTrabalhosTrilha(Trilha trilha){
		return trabalhoRepository.findByTrilha(trilha);
	}
	
	public void adicionarTrabalho(Trabalho trabalho){
		trabalhoRepository.save(trabalho);
	}
	
	public List<Trabalho> getTrabalhosParaRevisar(Long idRevisor, Long idEvento){
		return trabalhoRepository.getTrabalhosParaRevisar(idRevisor, idEvento);
	}
	
	public List<Long> getTrabalhosRevisadosDoRevisor(Long idRevisor, Long idEvento){
		List<Trabalho> trabalhosParaRevisar = this.getTrabalhosParaRevisar(idRevisor, idEvento);
		List<Long> trabalhosRevisados = new ArrayList<Long>();
		for(Trabalho trabalho : trabalhosParaRevisar){
			if(revisaoRepository.trabalhoEstaRevisadoPeloRevisor(trabalho.getId(), idRevisor)){
				trabalhosRevisados.add(trabalho.getId());
			}
		}
		
		return trabalhosRevisados;
	}
	
	public List<Pessoa> getAutoresDoTrabalho(Long idTrabalho){
		return trabalhoRepository.getAutoresDoTrabalho(idTrabalho);
	}
	
	public List<Trabalho> getTrabalhosDoAutorNoEvento(Pessoa pessoa,Evento evento){
		return trabalhoRepository.getTrabalhoDoAutorNoEvento(pessoa.getId(), evento.getId());
	}
	public void remover(Long id){
		trabalhoRepository.delete(id);
	}
	
	public int buscarQuantidadeTrabalhosPorEvento(Evento evento){
		return getTrabalhosEvento(evento).size();
	}
	
	public int buscarQuantidadeTrabalhosNaoRevisadosPorEvento(Evento evento){
		int naoRevisados = 0;
		List<Trabalho> trabalhos = getTrabalhosEvento(evento);
		for (int i = 0; i < trabalhos.size(); i++) {
			if (trabalhos.get(i).getRevisoes().isEmpty()) {
				naoRevisados++;
			}
		}
		return naoRevisados;
	}
	
	public int buscarQuantidadeTrabalhosRevisadosEComentadosPorEvento(Evento evento){
		int revisoesComentadas = 0;
		List<Trabalho> trabalhos = getTrabalhosEvento(evento);
		for (int i = 0; i < trabalhos.size(); i++) {
			List<Revisao> revisoes = trabalhos.get(i).getRevisoes();
			if (!revisoes.isEmpty()) {
				revisoesComentadas += contarComentarios(revisoes);
			}
		}
		return revisoesComentadas;
	}
	
	private int contarComentarios(List<Revisao> revisoes){
		int comentarios = 0;
		for (int i = 0; i < revisoes.size(); i++) {
			if (revisoes.get(i).getObservacoes() != null) {
				comentarios++;
			}
		}
		return comentarios;
	}
}