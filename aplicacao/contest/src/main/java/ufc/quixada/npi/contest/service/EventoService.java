package ufc.quixada.npi.contest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ufc.quixada.npi.ldap.model.Usuario;
import br.ufc.quixada.npi.ldap.service.UsuarioService;
import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel.Tipo;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Token;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.model.VisibilidadeEvento;
import ufc.quixada.npi.contest.repository.EventoRepository;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.util.ContestUtil;

@Service
public class EventoService {

	private static final String TITULO_EMAIL_ORGANIZADOR = "TITULO_EMAIL_CONVITE_ORGANIZADOR";
	private static final String TEXTO_EMAIL_ORGANIZADOR = "TEXTO_EMAIL_CONVITE_ORGANIZADOR";
	private static final String ASSUNTO_EMAIL_CONFIRMACAO = "ASSUNTO_EMAIL_CONFIRMACAO";
	private static final String TEXTO_EMAIL_CONFIRMACAO = ".Fique atento aos prazos, o próximo passo será a fase das revisões, confira no edital os prazos. Boa sorte!";
	//private static final String ASSUNTO_EMAIL_NOTIFICACAO_AUTOR_PRINCIPAL = "ASSUNTO_EMAIL_NOTIFICACAO_AUTOR_PRINCIPAL";
	
	@Autowired
	EventoRepository eventoRepository;

	@Autowired
	PessoaService pessoaService;

	@Autowired
	MessageService messageService;

	@Autowired
	EnviarEmailService emailService;

	@Autowired
	ParticipacaoEventoService participacaoEventoService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private TrilhaService trilhaService;
	
	@Autowired
	private UsuarioService usuarioService;

	private boolean adicionarPessoa(String email, Evento evento, Tipo papel, String url) {

		Pessoa pessoa = pessoaService.getByEmail(email);
		String nome = "Nome Temporário "+"<"+ email +">";

		String assunto = messageService.getMessage(TITULO_EMAIL_ORGANIZADOR) + " " + evento.getNome();
		String corpo = "Olá"+ messageService.getMessage(TEXTO_EMAIL_ORGANIZADOR) + " " + evento.getNome() + " como "+ papel.getNome();
		String titulo = "[CONTEST] Convite para o Evento: " + evento.getNome();
		String pageCadastro = "/completar-cadastro/";
		Token token =  new Token();
		
		if (pessoa == null) {
			
			try {
				Usuario usuarioLdap = usuarioService.getByEmail(email);
				
				if(usuarioLdap != null){
					pessoa = ContestUtil.convertUsuarioToPessoa("", usuarioLdap, new Pessoa());
				}else{
					pessoa = new Pessoa(nome, email);
					pessoa.setPapel(Tipo.USER);
				}
				pessoaService.addOrUpdate(pessoa);
			
				token = tokenService.novoToken(pessoa, Constants.ACAO_COMPLETAR_CADASTRO);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			corpo = corpo + ". Você não está cadastrado na nossa base de dados. Acesse: " + url + pageCadastro + token.getToken() + " e termine o seu cadastro";
		} else {
			corpo = corpo + ". Realize o login em " + url + "/login";
		}
		
		
		if (!emailService.enviarEmail(titulo, assunto, email, corpo)) {
			try{
				pessoaService.delete(pessoa.getId());
			}catch (Exception ex){
				ex.printStackTrace();
			}
			return false;
		}
		ParticipacaoEvento participacao = new ParticipacaoEvento(papel, pessoa, evento);
		participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacao);
		return true;
	}

	public boolean adicionarOrganizador(String email, Evento evento, String url) {
		Tipo papel = Tipo.ORGANIZADOR;
		return adicionarPessoa(email, evento, papel, url);
	}

	public boolean adicionarRevisor(String email, Evento evento, String url) {
		Tipo papel = Tipo.REVISOR;
		return adicionarPessoa(email, evento, papel, url);
	}

	public boolean adicionarAutor(String email, Evento evento, String url) {
		Tipo papel = Tipo.AUTOR;
		return adicionarPessoa(email, evento, papel, url);
	}
	
	public boolean adicionarCoAutor(String email, Evento evento, String url) {
		Tipo papel = Tipo.COAUTOR;
		return adicionarPessoa(email, evento, papel, url);
	}
	

	public boolean adicionarOuAtualizarEvento(Evento evento) {
		if (evento.getPrazoSubmissaoInicial() != null && evento.getPrazoSubmissaoFinal() != null
				&& evento.getPrazoRevisaoInicial() != null && evento.getPrazoRevisaoFinal() != null) {
			if (!evento.getEstado().equals(EstadoEvento.FINALIZADO)
					&& evento.getPrazoSubmissaoInicial().before(evento.getPrazoSubmissaoFinal())
					&& evento.getPrazoRevisaoInicial().after(evento.getPrazoSubmissaoInicial())
					&& evento.getPrazoRevisaoInicial().before(evento.getPrazoRevisaoFinal())
					&& evento.getPrazoRevisaoFinal().before(evento.getPrazoSubmissaoFinal())) {
				
				eventoRepository.save(evento);
				return true;
			}
			return false;
		} else {
			Trilha trilha = new Trilha();
			trilha.setEvento(evento);
			trilha.setNome("(DEFAULT)");
			trilhaService.adicionarOuAtualizarTrilha(trilha);
			eventoRepository.save(evento);
			return true;
		}
	}

	public boolean removerEvento(Long id) {
		if (eventoRepository.findOne(id) != null) {
			eventoRepository.delete(id);
			return true;
		}

		return false;
	}

	public Evento buscarEventoPorId(Long id) {
		return eventoRepository.findOne(id);
	}

	public List<Evento> buscarEventos() {
		return (List<Evento>) eventoRepository.findAll();
	}

	public Boolean existeEvento(Long id) {
		if (id == null || id.toString().isEmpty()) {
			return false;
		} else {
			return eventoRepository.exists(id);
		}
	}

	public List<Evento> buscarEventoPorEstado(EstadoEvento estado) {
		return eventoRepository.findEventoByEstado(estado);
	}

	public List<Evento> buscarEventosAtivosEPublicos() {
		return eventoRepository.findEventosAtivosEPublicos();
	}

	public List<Evento> eventosParaParticipar(Long idPessoa) {
		return eventoRepository.eventosParaParticipar(idPessoa);
	}

	public List<Evento> getMeusEventos(Long id) {
		return eventoRepository.findDistinctEventoByParticipacoesPessoaId(id);
	}
	
	public List<Evento> getMeusEventosComoAutor(Long idAutor){
		return eventoRepository.eventosPorPapel(idAutor, Tipo.AUTOR);
	}
	
	
	public List<Evento> getMeusEventosComoCoautor(Long idAutor){
		return eventoRepository.eventosPorPapel(idAutor, Tipo.COAUTOR);
	}
	
	public List<Evento> getMeusEventosAtivosComoOrganizador(Long idOrganizador){
		return eventoRepository.eventosComoOrganizadorEstado(idOrganizador, EstadoEvento.ATIVO);
	}

	public List<Evento> getMeusEventosInativosComoOrganizador(Long idOrganizador) {
		return eventoRepository.eventosPorPapelEstado(idOrganizador, Tipo.ORGANIZADOR, EstadoEvento.INATIVO);
	}
	public List<Evento> getMeusEventosAtivosComoRevisor(Long idRevisor) {
		return eventoRepository.eventosPorPapelEstado(idRevisor, Tipo.REVISOR, EstadoEvento.ATIVO);
	}

	public List<Evento> getEventosByEstadoEVisibilidadePublica(EstadoEvento estado) {
		return eventoRepository.findEventoByEstadoAndVisibilidade(estado, VisibilidadeEvento.PUBLICO);
	}
	
	public void notificarPessoaParticipantesDoArtigo (Trabalho trabalho, String email, Evento evento) {
		String assunto = messageService.getMessage(ASSUNTO_EMAIL_CONFIRMACAO) + " " + trabalho.getTitulo();
		String corpo = "Olá, seu trabalho " + trabalho.getTitulo() + " foi enviado com sucesso para o evento "
				+ evento.getNome() + TEXTO_EMAIL_CONFIRMACAO;
		String titulo = "[CONTEST] Confirmação de envio do trabalho: " + trabalho.getTitulo();
		
		emailService.enviarEmail(titulo, assunto, email, corpo);
	}
	public void notificarPessoaTrabalhoAdicionadoASessao(Trabalho trabalho, String email, Evento evento) {
		String assunto = "Email confirmando a atribuição do trabalho "+ trabalho.getTitulo()+ " à sessão " + trabalho.getSessao(); 
		String corpo = "Olá, seu trabalho " + trabalho.getTitulo() + "foi adicionado com sucesso na sessao" + trabalho.getSessao() +"no evento:" 
		+ evento.getNome();
		String titulo = "[CONTEST] Notificação de adição do trabalho:"+ trabalho.getTitulo() +"à sessão: " + trabalho.getSessao();
				
		emailService.enviarEmail(titulo, assunto, email, corpo);
	}

	
}