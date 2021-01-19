package br.com.rochasoft.minhasfinancas.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rochasoft.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.rochasoft.minhasfinancas.exception.RegraNegocioException;
import br.com.rochasoft.minhasfinancas.model.entity.Usuario;
import br.com.rochasoft.minhasfinancas.model.repository.UsuarioRepository;
import br.com.rochasoft.minhasfinancas.service.UsuarioService;

@Service 
// indica que o container do spring irá gerenciar uma instância desta classe
// o próprio spring vai gerar uma instância e armazenar no container para fornecer para outras classes
public class UsuarioServiceImpl implements UsuarioService 
{
	
	private UsuarioRepository repository;	

	@Autowired // indica que o spring irá injetar a variável automaticamente a partir do container spring	
	public UsuarioServiceImpl(UsuarioRepository repository) 
	{
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) 
	{

		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if (!usuario.isPresent())
		{
			throw new ErroAutenticacaoException("Usuário não encontrado.");
		}
		
		if (!usuario.get().getSenha().equals(senha))
		{
			throw new ErroAutenticacaoException("Senha inválida.");
		}
		
		return usuario.get();
		
	}

	@Override
	@Transactional // cria uma transação, executa o método e comita
	public Usuario salvarUsuario(Usuario usuario) 
	{

		validarEmail(usuario.getEmail());
		
		return repository.save(usuario);
		
	}

	@Override
	public void validarEmail(String email) 
	{
		
		boolean existe = repository.existsByEmail(email);
		
		if (existe)
		{
			throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail");
		}		
		
	}

}
