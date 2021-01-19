package br.com.rochasoft.minhasfinancas.service;

import java.util.List;

import br.com.rochasoft.minhasfinancas.model.entity.Lancamento;
import br.com.rochasoft.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService 
{
	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);

}
