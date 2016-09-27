package ufc.quixada.npi.contest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class Email {

	private String nomeConvidado;
	private String funcao;
	@NotNull
	@Pattern(regexp=".+@.+\\.[a-z]+")
	private String enderecoDestinatario;
	private String texto;
	private String titulo;
	private String nomeEvento;
	
	
	public String getNomeEvento() {
		return nomeEvento;
	}
	public void setNomeEvento(String nome) {
		this.nomeEvento = nome;
	}
	public String getNomeConvidado() {
		return nomeConvidado;
	}
	public void setNomeConvidado(String nomeConvidado) {
		this.nomeConvidado = nomeConvidado;
	}
	public String getFuncao() {
		return funcao;
	}
	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}
	public String getEnderecoDestinatario() {
		return enderecoDestinatario;
	}
	public void setEnderecoDestinatario(String enderecoDestinatario) {
		this.enderecoDestinatario = enderecoDestinatario;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	
}
