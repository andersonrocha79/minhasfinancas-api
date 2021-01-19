package br.com.rochasoft.minhasfinancas.exception;

public class ErroAutenticacaoException extends RuntimeException 
{

	// exception que é gerada quando uma regra de negócio é quebrada
	public ErroAutenticacaoException(String msg) 
	{
		super(msg);
	}
	
}
