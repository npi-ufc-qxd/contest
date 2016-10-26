package ufc.quixada.npi.contest.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import ufc.quixada.npi.contest.model.Revisao;

public class RevisaoJSON {

	public static String toJson(String formatacao, String originalidade, String merito, 
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
	
	public static Map<String, String> fromJson(Revisao revisao){
		
		Map<String, String> revisaoWrapper = new HashMap<>();
		JSONObject json = new JSONObject(revisao.getConteudo());
		for(String key : json.keySet()){
			revisaoWrapper.put(key, json.getString(key));
		}
		return revisaoWrapper;
		
	}
	
}
