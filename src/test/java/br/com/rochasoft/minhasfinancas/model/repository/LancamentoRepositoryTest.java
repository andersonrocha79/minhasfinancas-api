package br.com.rochasoft.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// importa de forma statica a classe para não precisar digitar Assertions várias vezes
import static org.assertj.core.api.Assertions.*;

import br.com.rochasoft.minhasfinancas.model.entity.Lancamento;
import br.com.rochasoft.minhasfinancas.model.enums.StatusLancamento;
import br.com.rochasoft.minhasfinancas.model.enums.TipoLancamento;

//migração junit4 para junit5
//@RunWith para @ExtendWith(SpringExtension.class)
//@Test de org.junit.jupiter.api.Test;

//teste de integração
//precisa de recursos externos a aplicação
//pra rodar precisa do banco de dados
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") // inclui esta diretiva para usar as definições de 'application-test.properties' nos testes, com banco h2
@DataJpaTest // cria a instância do banco de dados em memória e depois apaga a instância - sempre inicia a transação, depois do teste, faz o rollback para não influenciar nos outros testes
@AutoConfigureTestDatabase(replace = Replace.NONE) // não sobscreve as configurações do banco em memória definida em 'application-test.properties'

public class LancamentoRepositoryTest 
{
	
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	@DisplayName("Deve salvar um lançamento")
	public void deveSalvarUmLancamento()
	{
		
		Lancamento lancamento = criarLancamento();
		
		lancamento = repository.save(lancamento);		
		
		assertThat(lancamento.getId()).isNotNull();
		
	}

	public static Lancamento criarLancamento() 
	{
		
		Lancamento lancamento = Lancamento.builder()
				.ano(2019)
				.mes(1)
				.descricao("teste")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();	
		
		return lancamento;
		
	}
	
	@Test
	@DisplayName("Deve deletar um lançamento")
	public void deveDeletarUmLancamento()
	{
		
		// grava o lançamento
		Lancamento lancamento = criarPersistirLancamento();
		
		// retorna o lançamento gravado
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		
		// retorna o lançamento gravado
		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		
		
		assertThat(lancamentoInexistente).isNull();
		
	}

	private Lancamento criarPersistirLancamento() 
	{
		
		Lancamento lancamento = criarLancamento();		
		entityManager.persist(lancamento);
		
		return lancamento;
		
	}	
	
	@Test
	@DisplayName("Deve atualizar um lançamento")
	public void deveAtualizarUmLancamento()
	{
		
		// cria o lançamento e grava
		Lancamento lancamento = criarPersistirLancamento();
		
		// altera os parametros e regrava
		lancamento.setMes(7);
		lancamento.setAno(2018);
		lancamento.setDescricao("atualizado");
		lancamento.setStatus(StatusLancamento.CANCELADO);		
		repository.save(lancamento);
		
		// retorna o lançamento atualizado
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
							
		// verificações
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(lancamento.getAno());
		assertThat(lancamentoAtualizado.getMes()).isEqualTo(lancamento.getMes());
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo(lancamento.getDescricao());
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(lancamento.getStatus());
		
	}
	
	@Test
	@DisplayName("Deve buscar um lançamento por Id")
	public void deveBuscarLancamentoPorId()
	{
		
		// cria o lançamento e grava
		Lancamento lancamento = criarPersistirLancamento();
			
		// retorna o lançamento atualizado
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
							
		// verificações
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
		
	}		
}
