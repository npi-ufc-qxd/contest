package ufc.quixada.npi.contest.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ufc.quixada.npi.contest.model.Notificacao;
import ufc.quixada.npi.contest.model.Pessoa;
import ufc.quixada.npi.contest.service.NotificacaoService;
import ufc.quixada.npi.contest.service.PessoaService;

public class NotificacoesFilter implements Filter {
	
	@Autowired
	private NotificacaoService notificacaoService;
	

	@Autowired
	private PessoaService pessoaService;


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String cpf = auth.getName();
		Pessoa autorLogado = pessoaService.getByCpf(cpf);
		
		List<Notificacao> listaNotificacao = notificacaoService.listaNotificacaoDePessoa(autorLogado);
		
		request.setAttribute("notificacoes", listaNotificacao);
		request.setAttribute("pessoa", autorLogado);
		filter.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig request) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
