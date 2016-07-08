package ufc.quixada.npi.contest.util;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {
	// LDAP - atributos presentes no arquivo application.properties
	public static final String LDAP_URL = "ldap.url";
	public static final String LDAP_BASE = "ldap.base";
	public static final String LDAP_USER = "ldap.user";
	public static final String LDAP_PASSWORD = "ldap.password";
	public static final String LDAP_OU = "ldap.ou";
	public static final String BASE = LDAP_URL;

	public static final String TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO = "evento/admin_cadastrar";
	public static final String TEMPLATE_LISTAR_EVENTOS_ATIVOS = "evento/admin_lista_ativos";
	public static final String TEMPLATE_LISTAR_EVENTOS_INATIVOS = "evento/admin_lista_inativos";
}