# language: pt
Funcionalidade: Baixar Trabalhos
	
	Cenário: Eu quero baixar um trabalho em que não sou revisor e nem organizador do evento do trabalho
	  	Dado Eu quero baixar um trabalho
	  	E Não sou revisor e nem organizador do evento deste trabalho
	  	Então um erro deve ser mostrado