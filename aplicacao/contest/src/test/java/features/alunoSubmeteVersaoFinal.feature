## language: pt
	Funcionalidade: O aluno submete versão final do trabalho.

		Eu como aluno gostaria de enviar a versão final do meu trabalho para o evento
			* Após receber a avaliação dos revisores o aluno pode submeter a versão final do trabalho
			* O aluno pode realizar várias submissões desde que o prazo para o envio da versão final esteja aberto

			Contexto:
				Dado que existe um aluno
				E que possui um trabalho

			Cenário: O aluno realiza submissão dentro do prazo de envio
				Quando está dentro do prazo de submissão de envio do evento
				Então o trabalho poderá ser reenviado
				
			Cenário: O aluno realiza submissão fora do prazo de envio
				Quando está fora do prazo de submissão de envio do evento
				Então o trabalho não poderá ser reenviado
  