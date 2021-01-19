package br.com.rochasoft.minhasfinancas.exception;

public class RegraNegocioException extends RuntimeException 
{

	// exception que é gerada quando uma regra de negócio é quebrada
	public RegraNegocioException(String msg) 
	{
		super(msg);
	}
	
}
