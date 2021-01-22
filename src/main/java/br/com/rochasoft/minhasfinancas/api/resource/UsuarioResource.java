package br.com.rochasoft.minhasfinancas.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rochasoft.minhasfinancas.api.dto.UsuarioDTO;
import br.com.rochasoft.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.rochasoft.minhasfinancas.exception.RegraNegocioException;
import br.com.rochasoft.minhasfinancas.model.entity.Usuario;
import br.com.rochasoft.minhasfinancas.service.LancamentoService;
import br.com.rochasoft.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource 
{
	
	// declarando a interface, porque a instancia sera definda pelo container do springboot
	private final UsuarioService    service;
	private final LancamentoService lancamentoService;
	
	// construtor a ser utilizado pela injeção de dependência
	// o spring boot irá passar como parâmetro automaticamente um objeto a partir do container
	/*
	public UsuarioResource(UsuarioService service)
	{
		this.service = service;
	}
	*/
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar( @RequestBody UsuarioDTO dto)
	{
			
		try 
		{
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} 
		catch (ErroAutenticacaoException e) 
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}		
		
	}	
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody UsuarioDTO dto)
	{
		
		// transforma o dto em entidade usuario
		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha())
				.build();
		
		try 
		{
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} 
		catch (RegraNegocioException e) 
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}		
		
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id)
	{
		
		// verifica se o usuário existe
		Optional<Usuario> usuario = service.obterPorId(id);
		
		if (!usuario.isPresent())
		{
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		// busca o saldo do usuário
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		
		// retorna o saldo
		return ResponseEntity.ok(saldo);
		
	}
	
}
