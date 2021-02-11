package br.com.rochasoft.minhasfinancas.api.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.rochasoft.minhasfinancas.api.dto.AtualizaStatusDTO;
import br.com.rochasoft.minhasfinancas.api.dto.LancamentoDTO;
import br.com.rochasoft.minhasfinancas.exception.RegraNegocioException;
import br.com.rochasoft.minhasfinancas.model.entity.Lancamento;
import br.com.rochasoft.minhasfinancas.model.entity.Usuario;
import br.com.rochasoft.minhasfinancas.model.enums.StatusLancamento;
import br.com.rochasoft.minhasfinancas.model.enums.TipoLancamento;
import br.com.rochasoft.minhasfinancas.service.LancamentoService;
import br.com.rochasoft.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor // faz com que seja criada um construtor com todos argumentos obrigatórios (final)
public class LancamentoResource 
{
	
	private final LancamentoService service;
	private final UsuarioService    usuarioService;

	/*
	public LancamentoResource(LancamentoService service, UsuarioService usuarioService) 
	{
		super();
		this.service        = service;
		this.usuarioService = usuarioService;
	}
	*/
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody LancamentoDTO dto )
	{
		
		try
		{
		
			Lancamento entidade = converter(dto);
			
			entidade = service.salvar(entidade);
			
			return new ResponseEntity(entidade, HttpStatus.CREATED);
			
		}
		catch (RegraNegocioException e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody LancamentoDTO dto )
	{
				
		return service.obterPorId(id).map( entity -> 
		{
			
			try
			{
			
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok().body(lancamento);

			}
			catch (RegraNegocioException e)
			{
				return ResponseEntity.badRequest().body(e.getMessage());
			}
				
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
				
		
	}	
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id)	
	{

		return service.obterPorId(id).map( entidade -> 
		{
			
			try
			{
						
				service.deletar(entidade);
				return new ResponseEntity(HttpStatus.NO_CONTENT);

			}
			catch (RegraNegocioException e)
			{
				return ResponseEntity.badRequest().body(e.getMessage());
			}
				
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
		
	}
	
	private Lancamento converter(LancamentoDTO dto)
	{
		
		Lancamento lancamento = new Lancamento();
		
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		lancamento.setDataCadastro(LocalDate.now());
				
		Usuario usuario = usuarioService
							.obterPorId(dto.getUsuario())
							.orElseThrow( () -> new RegraNegocioException("Usuário não encontrado para o Id informado") );
		
		lancamento.setUsuario(usuario);
		
		if (dto.getTipo() != null)
		{
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo())); // se o texto for o nome da constante, retorna o valor (indice) da constante
		}
		
		if (dto.getStatus() != null)
		{
		   lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus())); // se o texto for o nome da constante, retorna o valor (indice) da constante
		}
		
		return lancamento;
		
	}
	
	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String  descricao,
			                     @RequestParam(value = "ano",       required = false) Integer ano,
			                     @RequestParam(value = "mes",       required = false) Integer mes,
			                     @RequestParam("usuario") Long idUsuario)
	{
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setMes(mes);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		
		if (!usuario.isPresent())
		{
			return ResponseEntity.badRequest().body("Usuário não encontrado");
		}
		else
		{
			lancamentoFiltro.setUsuario(usuario.get());
			List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
			return ResponseEntity.ok(lancamentos);
		}
		
	}
	
	// atualiza apenas o status do registro id passado como parâmetro
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualisarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto )
	{
				
		return service.obterPorId(id).map( entity -> 
		{
			
			try
			{
				
				StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
				
				if (statusSelecionado == null)
				{
					return ResponseEntity.badRequest().body("Status inválido.");
				}
				else
				{
					entity.setStatus(statusSelecionado);
					service.atualizar(entity);
					return ResponseEntity.ok().body(entity);					
				}			

			}
			catch (RegraNegocioException e)
			{
				return ResponseEntity.badRequest().body(e.getMessage());
			}
				
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));
				
		
	}		
	
	private LancamentoDTO converter(Lancamento lancamento)
	{
		return LancamentoDTO.builder()
				.id(lancamento.getId())
				.descricao(lancamento.getDescricao())
				.valor(lancamento.getValor())
				.mes(lancamento.getMes())
				.ano(lancamento.getAno())
				.tipo(lancamento.getTipo().name())
				.status(lancamento.getStatus().name())
				.usuario(lancamento.getUsuario().getId())
				.build();
	}

	@GetMapping("{id}")
	public ResponseEntity obterLancamento(@PathVariable("id") Long id)	
	{

		return service.obterPorId(id).map( lancamento -> 
		{
			
			try
			{						
				return new ResponseEntity(converter(lancamento), HttpStatus.OK);
			}
			catch (RegraNegocioException e)
			{
				return ResponseEntity.badRequest().body(e.getMessage());
			}
				
		}).orElseGet( () -> new ResponseEntity("Lançamento não encontrado", HttpStatus.NOT_FOUND));
		
	}	
}
