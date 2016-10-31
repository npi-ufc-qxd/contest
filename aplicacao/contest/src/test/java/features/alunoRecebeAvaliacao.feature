## language: pt
	Funcionalidade: O aluno recebe a avaliação do trabalho
	Eu como aluno gostaria de receber a avaliação do trabalho enviado
	Quando o prazo de revisão estiver encerrado, o aluno deve ter acesso as revisões do seu trabalho

			Contexto:
				Dado que o aluno deseja ver a revisão de seu trabalho

			Cenário: Aluno tenta ver revisão existente
				Quando há uma ou mais revisões
				Então deve obter acesso as revisões
			
			Cenário: Aluno tenta ver revisão não existente
				Quando não há revisões
				Então deve ver uma mensagem de erro de revisão inexistente
  
   
  