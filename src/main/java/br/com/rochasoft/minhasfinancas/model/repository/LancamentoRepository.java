package br.com.rochasoft.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rochasoft.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> 
{
	
	

}