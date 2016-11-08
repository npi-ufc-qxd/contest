package ufc.quixada.npi.contest.model;

import java.util.HashMap;
import java.util.Map;

public class Email {

	private String nomeDestinatario;
	private Map<String, String> enderecosDestinatarios;
	private String corpo;
	private String assunto;
	private String nomeEvento;
	
	   private Email(EmailBuilder builder) {
		   	      this.nomeDestinatario = builder.nomeDestinatario;
		   	      this.corpo = builder.corpo;
		   	      this.assunto = builder.assunto;
		   	      this.enderecosDestinatarios = builder.enderecosDestinatarios;
		   	    }
	public String getNomeEvento() {
		return nomeEvento;
	}
	public void setNomeEvento(String nome) {
		this.nomeEvento = nome;
	}
	public String getNomeConvidado() {
		return nomeDestinatario;
	}
	public void setNomeConvidado(String nomeConvidado) {
		this.nomeDestinatario = nomeConvidado;
	}
	public Map<String, String> getEnderecosDestinatarios() {
		return enderecosDestinatarios;
	}
	public void setEnderecoDestinatario(String email, String nome) {
		 for (String key : this.enderecosDestinatarios.keySet()) {
             if(email.equals(this.enderecosDestinatarios.get(key))){
                  this.enderecosDestinatarios.put(email, nome);
             }
		}
	}
	public String getCorpo() {
		return corpo;
	}
	public void setCorpo(String texto) {
		this.corpo = texto;
	}
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String titulo) {
		this.assunto = titulo;
	}
	
  public static class EmailBuilder {
  	        private String nomeDestinatario;
  	        private String assunto;
  	        private String corpo;
  	        private Map<String, String> enderecosDestinatarios = new HashMap<String,String>();
  
  	        public EmailBuilder(String nome, String assunto, String email, String corpo) {
  	        	this.nomeDestinatario = nome;
  	        	this.assunto = assunto;
  	        	this.enderecosDestinatarios.put(email, nome);
  	        	this.corpo = corpo;
  	        }
  	        public EmailBuilder emailDestinatario(String nome_destinario, String email_destinatario) {
  	            this.enderecosDestinatarios.put(email_destinatario, nome_destinario);
  	            return this;
  	        }

  	        public Email build() {
  	            return new Email(this);
  	        }
  	}
	
}
