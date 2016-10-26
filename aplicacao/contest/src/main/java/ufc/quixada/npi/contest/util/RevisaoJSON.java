package ufc.quixada.npi.contest.util;

import org.json.JSONObject;

public class RevisaoJSON {

	public String toJson(String formatacao, String originalidade, String merito, 
			String clareza, String qualidade, String relevancia, String auto_avaliacao,
			String comentarios_autores, String avaliacao_geral, String avaliacao_final, String indicar){
		
		JSONObject json = new JSONObject();
		json.put("formatacao", formatacao);
		json.put("originalidade", originalidade);
		json.put("merito", merito);
		json.put("clareza", clareza);
		json.put("qualidade", qualidade);
		json.put("relevancia", relevancia);
		json.put("auto_avaliacao", auto_avaliacao);
		json.put("comentarios_autores", comentarios_autores);
		json.put("avaliacao_geral", avaliacao_geral);
		json.put("avaliacao_final", avaliacao_final);
		json.put("indicacao", indicar);
		
		return json.toString();
	}
	
	public String fromJson(String conteudo, String chave){
		JSONObject json = new JSONObject(conteudo);
		return json.getString(chave);
	}
	
}
