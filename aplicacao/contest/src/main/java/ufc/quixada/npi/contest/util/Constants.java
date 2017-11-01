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

	public static final String TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ADMIN = "evento/admin_cadastrar";
	public static final String TEMPLATE_LISTAR_EVENTOS_ATIVOS_ADMIN = "evento/admin_lista_ativos";
	public static final String TEMPLATE_LISTAR_EVENTOS_INATIVOS_ADMIN = "evento/admin_lista_inativos";
	
	public static final String TEMPLATE_ADICIONAR_OU_EDITAR_EVENTO_ORG = "organizador/org_editar_eventos";
	public static final String TEMPLATE_ATIVAR_EVENTO_ORG = "organizador/org_ativar_eventos";
	public static final String TEMPLATE_EDITAR_EVENTO_ORG = "organizador/org_editar_eventos";
	public static final String TEMPLATE_LISTAR_EVENTOS_ATIVOS_ORG = "organizador/org_eventos_listar_ativos";
	public static final String TEMPLATE_LISTAR_EVENTOS_INATIVOS_ORG = "organizador/org_eventos_listar_inativos";
	public static final String TEMPLATE_MEUS_EVENTOS_ORG = "organizador/organizador_meus_eventos";
	public static final String TEMPLATE_GERAR_CERTIFICADOS_TRABALHO = "organizador/org_gerar_certificados_trabalhos";
	public static final String TEMPLATE_GERAR_CERTIFICADOS_ORGANIZADORES = "organizador/org_gerar_certificados_organizadores";
	public static final String TEMPLATE_GERAR_CERTIFICADOS_REVISORES = "organizador/org_gerar_certificados_revisores";
	public static final String TEMPLATE_LISTAR_EVENTOS_ATIVOS_REV = "revisor/revisor_eventos_listar_ativos";
	
	public static final String TEMPLATE_LISTAR_TRILHAS_ORG = "organizador/org_trilhas";
	public static final String TEMPLATE_DETALHES_TRILHA_ORG = "organizador/org_detalhes_trilha";
	public static final String TEMPLATE_DETALHES_EVENTO_ORG = "organizador/org_detalhes_evento";
	public static final String TEMPLATE_DETALHES_EVENTO_REV = "revisor/revisor_detalhes_evento";
	public static final String TEMPLATE_ATRIBUIR_REVISOR_ORG = "organizador/org_atribuir_revisores";
	public static final String TEMPLATE_CONSIDERACOES_REVISORES_ORG = "organizador/org_consideracoes_dos_revisores";
	public static final String TEMPLATE_ORGANIZADOR_SEM_PERMISSAO = "organizador/erro_permissao_de_org";
	
	public static final String TEMPLATE_INDEX_AUTOR = "autor/autor_index";
	public static final String TEMPLATE_MEUS_TRABALHOS_AUTOR = "autor/autor_meus_trabalhos";
	
	public static final String TEMPLATE_CONVIDAR_PESSOAS_EMAIL_ORG = "organizador/org_convidar_pessoas";
	
	public static final String TEMPLATE_ENVIAR_TRABALHO_AUTOR = "autor/autor_enviar_trabalho";
	public static final String TEMPLATE_ENVIAR_TRABALHO_FORM_AUTOR = "autor/autor_enviar_trabalho_form";
	public static final String TEMPLATE_LISTAR_TRABALHO_AUTOR = "autor/autor_listar_trabalhos";
	public static final String TEMPLATE_REVISAO_AUTOR = "autor/autor_revisao";	
	
	public static final String CAMINHO_TRABALHOS = "/mnt/contest-uploads";
	public static final String ENDERECO_EMAIL_CONTEST = "naoresponda@contest.quixada.ufc.br";
	
	public static final String ACAO_RECUPERAR_SENHA = "Recuperar Senha";
	public static final String ACAO_COMPLETAR_CADASTRO = "Completar Cadastro";
	public static final String REDIRECIONAR_PARA_LOGIN = "redirect:/login";	
	
	public static final String ERROR_403 = "error/403";
	public static final String ERROR_404 = "error/404";
	public static final String NO_ERROR = "NO_ERROR";
	
	}