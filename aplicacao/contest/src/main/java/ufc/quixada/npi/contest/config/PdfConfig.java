package ufc.quixada.npi.contest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

@Configuration
public class PdfConfig extends WebMvcConfigurerAdapter{
    
	@Bean(name = "PDF_ORGANIZADOR")
    public JasperReportsMultiFormatView reportPdfOrganizador() {
    	JasperReportsMultiFormatView report = new JasperReportsMultiFormatView();
    	report.setUrl("classpath:CertificadoEU2016Organizacao.jrxml");
    	return report;
    }
	
	@Bean(name = "PDF_REVISORES")
	public JasperReportsMultiFormatView reportPdfRevisores() {
    	JasperReportsMultiFormatView report = new JasperReportsMultiFormatView();
    	report.setUrl("classpath:CertificadoEU2016Revisores.jrxml");
    	return report;
    }
	
	@Bean(name = "PDF_TRABALHOS")
	public JasperReportsMultiFormatView reportPdfTrabalhos() {
    	JasperReportsMultiFormatView report = new JasperReportsMultiFormatView();
    	report.setUrl("classpath:CertificadoEUTrabalhos.jrxml");
    	return report;
    }
}