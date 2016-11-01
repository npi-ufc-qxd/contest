package ufc.quixada.npi.contest.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ufc.quixada.npi.contest.model.EstadoEvento;
import ufc.quixada.npi.contest.model.Evento;
import ufc.quixada.npi.contest.model.Papel;
import ufc.quixada.npi.contest.model.ParticipacaoEvento;
import ufc.quixada.npi.contest.model.ParticipacaoTrabalho;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.model.Submissao;
import ufc.quixada.npi.contest.model.TipoSubmissao;
import ufc.quixada.npi.contest.model.Trabalho;
import ufc.quixada.npi.contest.model.Trilha;
import ufc.quixada.npi.contest.service.EventoService;
import ufc.quixada.npi.contest.service.MessageService;
import ufc.quixada.npi.contest.service.ParticipacaoEventoService;
import ufc.quixada.npi.contest.service.PessoaService;
import ufc.quixada.npi.contest.service.RevisaoService;
import ufc.quixada.npi.contest.service.StorageService;
import ufc.quixada.npi.contest.service.SubmissaoService;
import ufc.quixada.npi.contest.service.TrabalhoService;
import ufc.quixada.npi.contest.service.TrilhaService;
import ufc.quixada.npi.contest.util.Constants;
import ufc.quixada.npi.contest.validator.TrabalhoValidator;


@Controller
@RequestMapping("/autor")
public class AutorController {

	private static final String EXTENSAO_PDF = ".pdf";
	private static final String FORA_DO_PRAZO_SUBMISSAO = "FORA_DO_PRAZO_SUBMISSAO";
	private static final String ERRO_EXCLUIR_TRABALHO = "ERRO_EXCLUIR_TRABALHO";
	private static final String TRABALHO_EXCLUIDO_COM_SUCESSO = "TRABALHO_EXCLUIDO_COM_SUCESSO";
	private static final String FORA_DA_DATA_DE_SUBMISSAO = "FORA_DA_DATA_DE_SUBMISSAO";
	private static final String ERRO_CADASTRO_TRABALHO = "ERRO_CADASTRO_TRABALHO";
	private static final String TRABALHO_ENVIADO = "TRABALHO_ENVIADO";
	private static final String FORMATO_ARQUIVO_INVALIDO = "FORMATO_ARQUIVO_INVALIDO";
	private static final String NAO_HA_TRABALHOS = "NAO_HA_TRABALHOS";
	private static final String NAO_TEM_REVISAO = "NAO_TEM_REVISAO";
	private static final String TEM_REVISAO = "TEM_REVISAO";
	private static final String PARTICAPAR_EVENTO_SUCESSO = "PARTICAPAR_EVENTO_SUCESSO";
	private static final String PARTICAPACAO_EVENTO_SUCESSO = "particapacaoEventoSucesso";
	private static final String EVENTO_NAO_EXISTE = "EVENTO_NAO_EXISTE";
	private static final String EVENTO_INEXISTENTE_ERROR = "eventoInexistenteError";
	private static final String PARTICIPAR_EVENTO_INATIVO = "PARTICIPAR_EVENTO_INATIVO";
	private static final String ID_EVENTO_VAZIO_ERROR = "ID_EVENTO_VAZIO_ERROR";
	private static final String EVENTO_VAZIO_ERROR = "eventoVazioError";
	private static final String PARTICIPAR_EVENTO_INATIVO_ERROR = "participarEventoInativoError";
	private static final String ERRO_TRABALHO_EVENTO = "ERRO_TRABALHO_EVENTO";
	private static final String ERRO_REENVIAR = "ERRO_REENVIAR";

	@Autowired
	private ParticipacaoEventoService participacaoEventoService;

	@Autowired
	private EventoService eventoService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private RevisaoService revisaoService;

	@Autowired
	private SubmissaoService submissaoService;

	@Autowired
	private TrabalhoService trabalhoService;

	@Autowired
	private TrabalhoValidator trabalhoValidator;

	@Autowired
	private TrilhaService trilhaService;

	@Autowired
	private StorageService storageService;

	@RequestMapping
	public String index(Model model){
		Pessoa autorLogado = getAutorLogado();
		model.addAttribute("eventosParaParticipar", eventoService.eventosParaParticipar(autorLogado.getId()));
		model.addAttribute("eventoParticipando", eventoService.buscarEventosParticapacaoAutor(autorLogado.getId()));
		return Constants.TEMPLATE_INDEX_AUTOR;
	}
	
	@RequestMapping(value="/participarEvento", method = RequestMethod.GET)
	public String eventosAtivos(Model model){
		Pessoa autorLogado = getAutorLogado();
		model.addAttribute("eventosParaParticipar", eventoService.eventosParaParticipar(autorLogado.getId()));
		model.addAttribute("eventoParticipando", eventoService.buscarEventosParticapacaoAutor(autorLogado.getId()));
		return Constants.TEMPLATE_INDEX_AUTOR;
	}
		
	@RequestMapping(value="/participarEvento", method = RequestMethod.POST)
	public String participarEvento(@RequestParam String idEvento, Model model, RedirectAttributes redirect){
		if(!eventoService.existeEvento(Long.parseLong(idEvento))){
			redirect.addFlashAttribute(EVENTO_VAZIO_ERROR, messageService.getMessage(ID_EVENTO_VAZIO_ERROR));
			return "redirect:/autor/participarEvento";
		}

		Pessoa autorLogado = getAutorLogado();
		Evento evento = eventoService.buscarEventoPorId(Long.parseLong(idEvento));
		
		if(evento != null){
			if(evento.getEstado() == EstadoEvento.ATIVO){
				ParticipacaoEvento participacaoEvento = new ParticipacaoEvento();
				participacaoEvento.setEvento(evento);
				participacaoEvento.setPessoa(autorLogado);
				participacaoEvento.setPapel(Papel.AUTOR);
				
				participacaoEventoService.adicionarOuEditarParticipacaoEvento(participacaoEvento);
				redirect.addFlashAttribute(PARTICAPACAO_EVENTO_SUCESSO,
						messageService.getMessage(PARTICAPAR_EVENTO_SUCESSO));
			} else {
				redirect.addFlashAttribute(PARTICIPAR_EVENTO_INATIVO_ERROR,
						messageService.getMessage(PARTICIPAR_EVENTO_INATIVO));
				return "redirect:/autor";
			}
		} else {
			redirect.addFlashAttribute(EVENTO_INEXISTENTE_ERROR, messageService.getMessage(EVENTO_NAO_EXISTE));
			return "redirect:/autor";
		}
		return "redirect:/autor/enviarTrabalhoForm/" + idEvento;
	}

	@RequestMapping(value = "/meusTrabalhos", method = RequestMethod.GET)
	public String listarEventosInativos(Model model) {
		Pessoa autorLogado = getAutorLogado();
		List<Evento> eventos =  eventoService.buscarEventosParticapacaoAutor(autorLogado.getId());
		if(eventos != null){
			List<String> trabalhosEventos = new ArrayList<>();
			
			for(Evento evento : eventos){
				if(submissaoService.existeTrabalhoNesseEvento(evento.getId())){
					if(revisaoService.existeTrabalhoNesseEvento(evento.getId())){
						trabalhosEventos.add(TEM_REVISAO);
					}else{
						trabalhosEventos.add(NAO_TEM_REVISAO);
					}
				}else{
					trabalhosEventos.add(NAO_TEM_REVISAO);
				}
			}
			
			model.addAttribute("eventos", eventos);
			model.addAttribute("trabalhosEvento", trabalhosEventos);
		}else{
			model.addAttribute("naoHaTrabalhos", messageService.getMessage(NAO_HA_TRABALHOS));
		}
		return Constants.TEMPLATE_MEUS_TRABALHOS_AUTOR;
	}

	@RequestMapping(value = "/enviarTrabalhoForm/{id}", method = RequestMethod.GET)
	public String enviarTrabalhoForm(@PathVariable String id, Model model, RedirectAttributes redirect){
		try{
			Long idEvento = Long.parseLong(id);
			
			if(eventoService.existeEvento(idEvento)){
				List<Trilha> trilhas = trilhaService.buscarTrilhas(Long.parseLong(id));
				Trabalho trabalho = new Trabalho();
				ParticipacaoTrabalho part = null;
				List<ParticipacaoTrabalho> partipacoes = new ArrayList<>();
				partipacoes.add(part);
				trabalho.setParticipacoes(partipacoes);
				
				model.addAttribute("trabalho", trabalho);
				model.addAttribute("eventoId", id);
				model.addAttribute("trilhas", trilhas);
				model.addAttribute("autor", getAutorLogado());
				return Constants.TEMPLATE_ENVIAR_TRABALHO_FORM_AUTOR;	
			}
			return "redirect:/autor/meusTrabalhos";
		}catch(NumberFormatException e){
			redirect.addFlashAttribute("error", messageService.getMessage(ID_EVENTO_VAZIO_ERROR));
			return "redirect:/autor/meusTrabalhos";
		}
	}
	
	@RequestMapping(value = "/enviarTrabalhoForm", method = RequestMethod.POST)
	public String enviarTrabalhoForm(@Valid Trabalho trabalho, BindingResult result, Model model, @RequestParam(value="file",required = true) MultipartFile file,
                                 @RequestParam("eventoId") String eventoId,  @RequestParam(required = false) String trilhaId, RedirectAttributes redirect){
		Evento evento;
		Trilha trilha;
		Submissao submissao;
		try{
			Long idEvento = Long.parseLong(eventoId);
			Long idTrilha = Long.parseLong(trilhaId);
			
			evento = eventoService.buscarEventoPorId(idEvento);
			trilha = trilhaService.get(idTrilha,idEvento);
			trabalho.setEvento(evento);
			trabalho.setTrilha(trilha);
			
			submissao = configuraSubmissao(new Submissao(), evento);		

		}catch(NumberFormatException e){
			redirect.addFlashAttribute("erroAoCadastrar", messageService.getMessage(ERRO_CADASTRO_TRABALHO));
			return "redirect:/autor/meusTrabalhos";
		}
		
		trabalhoValidator.validate(trabalho, result);
		if(result.hasErrors()){
			List<Trilha> trilhas = trilhaService.buscarTrilhas(Long.parseLong(eventoId));
			model.addAttribute("eventoId", eventoId);
			model.addAttribute("trilhas", trilhas);
			model.addAttribute("autor", getAutorLogado());
			return Constants.TEMPLATE_ENVIAR_TRABALHO_FORM_AUTOR;
		}else{
			if(validarArquivo(file)){
				if(evento.isPeriodoInicial() || evento.isPeriodoFinal()){
					return adicionarTrabalho(trabalho, evento, submissao, file, redirect);
				}else{
					redirect.addFlashAttribute("foraDoPrazoDeSubmissao", messageService.getMessage(FORA_DA_DATA_DE_SUBMISSAO));
					return "redirect:/autor/enviarTrabalhoForm/"+ eventoId;
				}
			}else{
				redirect.addFlashAttribute("erro", messageService.getMessage(FORMATO_ARQUIVO_INVALIDO));
				return "redirect:/autor/enviarTrabalhoForm/"+ eventoId;
			}
		}
	}
	
	@RequestMapping(value = "/reenviarTrabalho", method = RequestMethod.POST)
	public String reenviarTrabalhoForm(@RequestParam("trabalhoId") String trabalhoId, @RequestParam("eventoId") String eventoId, @RequestParam(value="file",required = true) MultipartFile file, RedirectAttributes redirect){
		Long idEvento = Long.parseLong(eventoId);
		Long idTrabalho = Long.parseLong(trabalhoId);
		try{			
			if(trabalhoService.existeTrabalho(idTrabalho) && eventoService.existeEvento(idEvento)){
				
				Evento evento = eventoService.buscarEventoPorId(Long.parseLong(eventoId));
				Trabalho trabalho = trabalhoService.getTrabalhoById(idTrabalho);
				Submissao submissao = configuraSubmissao(new Submissao(), evento);
				
				if(validarArquivo(file)){		
					if(evento.isPeriodoInicial() || evento.isPeriodoFinal()){
						return adicionarTrabalho(trabalho, evento, submissao, file, redirect);
					}else{
						redirect.addFlashAttribute("FORA_DA_DATA_DE_SUBMISSAO", messageService.getMessage(FORA_DA_DATA_DE_SUBMISSAO));
						return "redirect:/autor/listarTrabalhos/" + idEvento;
					}
				}else{
					redirect.addFlashAttribute("arquivoInvalido", messageService.getMessage(FORMATO_ARQUIVO_INVALIDO));
					return "redirect:/autor/listarTrabalhos/" + idEvento;
				}

			}
			redirect.addAttribute("ERRO_TRABALHO_EVENTO", messageService.getMessage(ERRO_TRABALHO_EVENTO));
			return "redirect:/autor/listarTrabalhos/" + idEvento;
		}catch(NumberFormatException e){
			redirect.addAttribute("ERRO_REENVIAR", messageService.getMessage(ERRO_REENVIAR));
			return "redirect:/autor/listarTrabalhos/" + idEvento;
		}
	}
	
	@RequestMapping(value = "/listarTrabalhos/{id}", method = RequestMethod.GET)
	public String listarTrabalhos(@PathVariable String id, Model model, RedirectAttributes redirect){
		try{
			Long idEvento = Long.parseLong(id);
			if(eventoService.existeEvento(idEvento)){
				Evento evento = eventoService.buscarEventoPorId(Long.parseLong(id));
				Pessoa pessoa = getAutorLogado();
				List<Trabalho> listaTrabalho = trabalhoService.getTrabalhosPorAutor(pessoa,evento);
				model.addAttribute("nomeEvento",evento.getNome());
				model.addAttribute("eventoId", evento.getId());
				model.addAttribute("listaTrabalhos", listaTrabalho);
				return Constants.TEMPLATE_LISTAR_TRABALHO_AUTOR;
			}
			return "redirect:/autor/meusTrabalhos";
			
		}catch(NumberFormatException e){
			redirect.addFlashAttribute("erroAoCadastrar", messageService.getMessage(ERRO_CADASTRO_TRABALHO));
			return "redirect:/autor/meusTrabalhos";
		}
	}
   
	@RequestMapping(value="/file", method=RequestMethod.GET, produces = "application/pdf")
	public void downloadPDFFile(@RequestParam("path") String path,  HttpServletResponse response)
	        throws IOException {

            try{
            	Path file = Paths.get(path);
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "attachment; filename="+path);
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
            catch (IOException e) {
                e.printStackTrace();
                response.reset();
                response.sendRedirect("/error/404");
                response.addHeader("Status", "404 Not Found");
                response.getOutputStream().flush();
            }

	}
	
	@RequestMapping(value="/excluirTrabalho", method = RequestMethod.POST)
	public String excluirTrabalho(@RequestParam("trabalhoId") String trabalhoId, @RequestParam("eventoId") String eventoId,
								  Model model, RedirectAttributes redirect){
		try{
			Long idEvento = Long.parseLong(eventoId);
			Long idTrabalho = Long.parseLong(trabalhoId);
			if(trabalhoService.existeTrabalho(idTrabalho) && eventoService.existeEvento(idEvento)){
				
				Evento evento = eventoService.buscarEventoPorId(Long.parseLong(eventoId));
				Date dataDeEnvio = new Date(System.currentTimeMillis());
				
				if(evento.getPrazoSubmissaoFinal().after(dataDeEnvio)){
					Trabalho t = trabalhoService.getTrabalhoById(idTrabalho);
					storageService.deleteArquivo(t.getPath());
					trabalhoService.remover(Long.parseLong(trabalhoId));
					
					redirect.addFlashAttribute("trabalhoExcluido", messageService.getMessage(TRABALHO_EXCLUIDO_COM_SUCESSO));
					return "redirect:/autor/listarTrabalhos/"+eventoId;
				}else{
					redirect.addFlashAttribute("erroExcluir", messageService.getMessage(FORA_DO_PRAZO_SUBMISSAO));
					return "redirect:/autor/listarTrabalhos/"+evento.getId();
				}
			}
			model.addAttribute("erroExcluir", messageService.getMessage(ERRO_EXCLUIR_TRABALHO));
			return Constants.TEMPLATE_MEUS_TRABALHOS_AUTOR;
		}catch(NumberFormatException e){
			model.addAttribute("erroExcluir", messageService.getMessage(ERRO_EXCLUIR_TRABALHO));
			return Constants.TEMPLATE_MEUS_TRABALHOS_AUTOR;
		}
	}
	
	
	public Pessoa getAutorLogado(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String cpf = auth.getName();		
		return pessoaService.getByCpf(cpf);
	}
	
	public boolean validarArquivo(MultipartFile file){
		return file.getOriginalFilename().endsWith(EXTENSAO_PDF) && !file.isEmpty();
	}
	
	public Submissao configuraSubmissao(Submissao submissao, Evento evento){
		submissao.setDataSubmissao(new Date(System.currentTimeMillis()));
		if(evento.isPeriodoInicial()){
			submissao.setTipoSubmissao(TipoSubmissao.PARCIAL);
		}else if(evento.isPeriodoFinal()){
			submissao.setTipoSubmissao(TipoSubmissao.FINAL);
		}
		return submissao; 
	}
	
	public String adicionarTrabalho(Trabalho trabalho, Evento evento, Submissao submissao, MultipartFile file, RedirectAttributes redirect) {
		definePapelParticipantes(trabalho);
		
		ParticipacaoTrabalho participacaoAutor = new ParticipacaoTrabalho();
		participacaoAutor.setPapel(Papel.AUTOR);
		participacaoAutor.setTrabalho(trabalho);
		participacaoAutor.setPessoa(getAutorLogado());
		
		List<ParticipacaoTrabalho> participacaoTrabalhos = trabalho.getParticipacoes();
		participacaoTrabalhos.add(participacaoAutor);
		trabalho.setParticipacoes(participacaoTrabalhos);

		submissao.setTrabalho(trabalho);

		String nomeDoArquivo = new StringBuilder("CONT-").append(evento.getId()).toString();		
		trabalho.setPath(storageService.store(file, nomeDoArquivo));
		
		submissaoService.adicionarOuEditar(submissao);
		
		redirect.addFlashAttribute("sucessoEnviarTrabalho", messageService.getMessage(TRABALHO_ENVIADO));
		return "redirect:/autor/meusTrabalhos";
	}
	
	public void definePapelParticipantes(Trabalho trabalho){
		List<ParticipacaoTrabalho> lista = trabalho.getParticipacoes();
		for(int i = 0; i < lista.size(); i++){
			ParticipacaoTrabalho p = lista.get(i);
			p.setTrabalho(trabalho);
			lista.get(i).setPapel(Papel.COAUTOR);
			Pessoa autor= pessoaService.getByEmail(p.getPessoa().getEmail());
			if(autor != null){
				p.setPessoa(autor);
			}
		}
	}
}