package br.com.rochasoft.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rochasoft.minhasfinancas.exception.RegraNegocioException;
import br.com.rochasoft.minhasfinancas.model.entity.Lancamento;
import br.com.rochasoft.minhasfinancas.model.enums.StatusLancamento;
import br.com.rochasoft.minhasfinancas.model.enums.TipoLancamento;
import br.com.rochasoft.minhasfinancas.model.repository.LancamentoRepository;
import br.com.rochasoft.minhasfinancas.service.LancamentoService;

@Service // indica que irá existir no container como 'serviço' (será um bean gerenciado) e injeta os parâmetros no construtor automaticamente
public class LancamentoServiceImpl implements LancamentoService 
{
	
	private LancamentoRepository repository;
	
	
	public LancamentoServiceImpl(LancamentoRepository repository) 
	{
		super();
		this.repository = repository;
	}

	@Override
	@Transactional // irá abrir a transação e ao final do método irá fazer o commit se der tudo certo
	public Lancamento salvar(Lancamento lancamento) 
	{
		
		validar(lancamento);
		
		lancamento.setStatus(StatusLancamento.PENDENTE);

		return repository.save(lancamento);
		
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) 
	{
		
		// gera uma exceção se o id não for definido
		Objects.requireNonNull(lancamento.getId());		
		
		validar(lancamento);
		
		return repository.save(lancamento);
		
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) 
	{

		// gera uma exceção se o id não for definido
		Objects.requireNonNull(lancamento.getId());
		
		repository.delete(lancamento);
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) 
	{
		
		Example example = Example.of(lancamentoFiltro, 
				ExampleMatcher
				.matching()
				.withIgnoreCase() // ignora maiusculas e minúsculas
				.withStringMatcher(StringMatcher.CONTAINING)); // busca por qualquer parte do texto
		
		return repository.findAll(example);
		
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) 
	{
		
		lancamento.setStatus(status);
		atualizar(lancamento);
		
	}

	@Override
	public void validar(Lancamento lancamento) 
	{
	
		if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals(""))
		{
			throw new RegraNegocioException("Informe uma Descrição válida.");
		}

		if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12)
		{
			throw new RegraNegocioException("Informe um Mês válido de 1 a 12.");
		}

		if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4)
		{
			throw new RegraNegocioException("Informe um Ano válido.");
		}
		
		if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null)
		{
			throw new RegraNegocioException("Informe um Usuário para registro do lançamento.");
		}

		if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1)
		{
			throw new RegraNegocioException("Informe um Valor válido.");
		}
		
		if (lancamento.getTipo() == null )
		{
			throw new RegraNegocioException("Informe um Tipo de lançamento.");
		}
		
		
	}

	@Override
	public Optional<Lancamento> obterPorId(Long id) 
	{
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) 
	{
	
		// total de receitas
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA);
		
		// total de despesas
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA);
		
		if (receitas == null) receitas = BigDecimal.ZERO;
		if (despesas == null) despesas = BigDecimal.ZERO;
		
		// retorna o saldo final
		return receitas.subtract(despesas);
		
	}
	

}
