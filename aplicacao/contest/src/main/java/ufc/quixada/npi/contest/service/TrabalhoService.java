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
import ufc.quixada.npi.contest.util.PessoaLogadaUtil;

@Service
public class TrabalhoService {

	@Autowired
	private TrabalhoRepository trabalhoRepository;

	@Autowired
	private RevisaoRepository revisaoRepository;

	@Autowired
	private EventoService eventoService;

	public Trabalho getTrabalhoById(Long idTrabalho) {
		return trabalhoRepository.findOne(idTrabalho);
	}

	public boolean existeTrabalho(Long idTrabalho) {
		return trabalhoRepository.exists(idTrabalho);
	}

	public List<Trabalho> getTrabalhosEvento(Evento evento) {
		return trabalhoRepository.getByEvento(evento);
	}

	public List<Trabalho> getTrabalhosTrilha(Trilha trilha) {
		return trabalhoRepository.findByTrilha(trilha);
	}

	public void adicionarTrabalho(Trabalho trabalho) {
		trabalhoRepository.save(trabalho);
	}

	public List<Trabalho> getTrabalhosParaRevisar(Long idRevisor, Long idEvento) {
		return trabalhoRepository.getTrabalhosParaRevisar(idRevisor, idEvento);
	}

	public List<Long> getTrabalhosRevisadosDoRevisor(Long idRevisor, Long idEvento) {
		List<Trabalho> trabalhosParaRevisar = this.getTrabalhosParaRevisar(idRevisor, idEvento);
		List<Long> trabalhosRevisados = new ArrayList<Long>();
		for (Trabalho trabalho : trabalhosParaRevisar) {
			if (revisaoRepository.trabalhoEstaRevisadoPeloRevisor(trabalho.getId(), idRevisor)) {
				trabalhosRevisados.add(trabalho.getId());
			}
		}

		return trabalhosRevisados;
	}

	public List<Pessoa> getAutoresDoTrabalho(Long idTrabalho) {
		return trabalhoRepository.getAutoresDoTrabalho(idTrabalho);
	}

	public List<Trabalho> getTrabalhosDoAutorNoEvento(Pessoa pessoa, Evento evento) {
		return trabalhoRepository.getTrabalhoDoAutorNoEvento(pessoa.getId(), evento.getId());
	}

	public List<Trabalho> getTrabalhosDoCoautor(Pessoa pessoa) {
		return trabalhoRepository.getTrabalhoDoCoautor(pessoa.getId());
	}

	public List<Trabalho> getTrabalhosDoAutor(Pessoa pessoa) {
		return trabalhoRepository.getTrabalhoDoAutor(pessoa.getId());
	}

	public void remover(Long id) {
		trabalhoRepository.delete(id);
	}

	public int buscarQuantidadeTrabalhosPorEvento(Evento evento) {
		return getTrabalhosEvento(evento).size();
	}

	public int buscarQuantidadeTrabalhosNaoRevisadosPorEvento(Evento evento) {
		return getTrabalhosEvento(evento).size() - trabalhoRepository.getTrabalhoRevisadoEvento(evento.getId()).size();
	}

	public int buscarQuantidadeTrabalhosRevisadosEComentadosPorEvento(Evento evento) {
		return trabalhoRepository.getTrabalhoRevisadoComentadoEvento(evento.getId());
	}

	public void removerSecao(Trabalho trabalho) {
		trabalho.setSecao(null);
		trabalhoRepository.save(trabalho);
	}

	public List<Trabalho> buscarTodosTrabalhos() {
		return trabalhoRepository.findAll();
	}

	public String mensurarAvaliacoes(Trabalho trabalho) {
		int numeroDeAprovacao = 0;
		int numeroDeReprovacao = 0;
		int numeroDeRessalvas = 0;

		List<Revisao> revisoes = trabalho.getRevisoes();
		if (!revisoes.isEmpty()) {
			for (int i = 0; i < revisoes.size(); i++) {

				if (revisoes.get(i).getAvaliacao().toString().equals("APROVADO") || revisoes.get(i).getAvaliacao().toString().equals("RESSALVAS")) {
					numeroDeAprovacao++;

					if (revisoes.get(i).getAvaliacao().toString().equals("RESSALVAS")) {					
						numeroDeRessalvas++;
						if (numeroDeRessalvas == trabalho.getRevisoes().size()) {
							return "RESSALVAS";
						}
					}
					if (numeroDeAprovacao == trabalho.getRevisoes().size())
						return "APROVADO";

				} else if (revisoes.get(i).getAvaliacao().toString().equals("REPROVADO")) {
					numeroDeReprovacao++;
					if (numeroDeReprovacao == trabalho.getRevisoes().size())
						return "REPROVADO";
				} 
			}
			return "MODERACAO";
		}
		return null;
	}

	public List<String> pegarConteudo(Trabalho trabalho) {

		String conteudoAux;
		String conteudo;

		List<String> resultadoAvaliacoes = new ArrayList<>();

		StringBuilder bld = new StringBuilder();
		for (Revisao revisao : trabalho.getRevisoes()) {

			conteudo = revisao.getConteudo().substring(1, revisao.getConteudo().length() - 1);
			bld.append("REVISOR : " + revisao.getRevisor().getNome().toUpperCase() + " , TRABALHO: "
					+ trabalho.getId().toString());

			while (!conteudo.isEmpty()) {
				if (conteudo.contains(",")) {
					conteudoAux = conteudo.substring(0, conteudo.indexOf(','));
					if (!conteudoAux.contentEquals("comentarios")) {
						bld.append((" ," + (conteudoAux.replaceAll("\"", " ").replaceAll("_", " ")
								.replaceAll("avaliacao", "AVALIAÇÃO").replaceAll("OTIMO", "ÓTIMO")
								.replaceAll("merito", "MÉRITO").replaceAll("relevancia", "RELEVÂNCIA")).toUpperCase()));
						conteudoAux = conteudo.substring(conteudo.indexOf(',') + 1);
						conteudo = conteudoAux;
					}
				} else {
					resultadoAvaliacoes.add((bld + (" , AVALIAÇÃO FINAL : " + revisao.getAvaliacao()).toString()));
					conteudo = "";
				}
			}
			bld.delete(0, bld.length());
		}

		return resultadoAvaliacoes;
	}

	public List<Trabalho> buscarTodosTrabalhosDaSecao(Long idSecao) {
		return trabalhoRepository.findTrabalhoBySecaoId(idSecao);

	}

	public void notificarAutoresEnvioTrabalho(Evento evento, Trabalho trabalho) {
		eventoService.notificarPessoa(trabalho, PessoaLogadaUtil.pessoaLogada().getEmail(), evento);

		List<Pessoa> coautores = trabalho.getCoAutoresDoTrabalho();
		for (Pessoa coautor : coautores) {
			eventoService.notificarPessoa(trabalho, coautor.getEmail(), evento);
		}
	}

	public List<Trabalho> getTrabalhosDoCoautorNoEvento(Pessoa pessoa, Evento evento) {
		return trabalhoRepository.getTrabalhoDoCoautorNoEvento(pessoa.getId(), evento.getId());
	}
}
