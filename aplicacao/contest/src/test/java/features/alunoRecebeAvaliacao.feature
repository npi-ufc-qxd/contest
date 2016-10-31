## language: pt
	Funcionalidade: O aluno recebe a avaliação do trabalho
	Eu como aluno gostaria de receber a avaliação do trabalho enviado
	Quando o prazo de revisão estiver encerrado, o aluno deve ter acesso as revisões do seu trabalho

			Contexto:
				Dado que o aluno deseja ver a revisão de seu trabalho

			Cenário: Aluno tenta ver revisão em período de submissão inicial
				Quando tenta ver a revisão em período de submissão inicial 
				Então deve ver uma mensagem de erro de revisão inexistente
				
			Cenário: Aluno tenta ver revisão em período de submissão final
				Quando tenta ver a revisão em período de submissão final
				Então deve obter acesso a revisão
			
			Cenário: Aluno tenta ver revisão em período de revisão
				Quando tenta enviar em período de revisão 
				Então deve ver uma mensagem de erro de revisão inexistente
  
  
  