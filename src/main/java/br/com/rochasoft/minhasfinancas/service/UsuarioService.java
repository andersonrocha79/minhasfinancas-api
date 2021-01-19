package br.com.rochasoft.minhasfinancas.service;

import br.com.rochasoft.minhasfinancas.model.entity.Usuario;

public interface UsuarioService 
{
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	

}
