package br.com.rochasoft.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.rochasoft.minhasfinancas.exception.RegraNegocioException;
import br.com.rochasoft.minhasfinancas.model.entity.Lancamento;
import br.com.rochasoft.minhasfinancas.model.entity.Usuario;
import br.com.rochasoft.minhasfinancas.model.enums.StatusLancamento;
import br.com.rochasoft.minhasfinancas.model.repository.LancamentoRepository;
import br.com.rochasoft.minhasfinancas.model.repository.LancamentoRepositoryTest;
import br.com.rochasoft.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") 
public class LancamentoServiceTest 
{
	
	@SpyBean
	LancamentoServiceImpl service; 		// instancia real
	
	@MockBean
	LancamentoRepository repository; 	// instancia fake
	
	@Test
	@DisplayName("deve gravar um lançamento")
	public void deveSalvarUmLancamento()
	{
		
		// cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		// execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		// verificação
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);			
	}
	
	@Test
	@DisplayName("não pode gravar um lançamento quando houver erro de validação")
	public void naoDeveSalvarUmLancamentoQuandoHouverErroValidacao()
	{
		
		// cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar); // gera o exception quando chamar o 'validar'
		
		// execução e verificação
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		
		// verificação
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar); // verificação pra garantir que o médoto save não sera executado quando chamar o método 'salvar'
		
	}
	
	@Test
	@DisplayName("deve atualizar um lançamento")
	public void deveAtualizarUmLancamento()
	{
		
		// cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		// execução
		service.atualizar(lancamentoSalvo);
		
		// verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		
	}	
	

	@Test
	@DisplayName("Deve gerar erro ao tentar atualizar lançamento que ainda não foi salvo")
	public void deveGerarErroAoTentarAtualizarLancamentoQueAindaNaoFoiSalvo()
	{
		
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		// execução e verificação
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class);
		
		// verificação
		Mockito.verify(repository, Mockito.never()).save(lancamento); // verificação pra garantir que o médoto save não sera executado quando chamar o método 'salvar'
		
	}	
	
	@Test
	@DisplayName("Deve excluir um lançamento")
	public void deveExcluirUmLancamento()
	{
		
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		// execução 
		service.deletar(lancamento);
		
		// verificação
		Mockito.verify(repository).delete(lancamento); // verificação pra garantir que o médoto save não sera executado quando chamar o método 'salvar'
		
	}	
	
	@Test
	@DisplayName("Deve gerar erro ao tentar excluir lançamento que ainda não foi salvo")
	public void deveGerarErroAoTentarExcluirLancamentoQueAindaNaoFoiSalvo()
	{
		
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		// execução e verificação
		Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);
		
		// verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamento); // verificação pra garantir que o médoto save não sera executado quando chamar o método 'salvar'
		
	}		
	
	@Test
	@DisplayName("Deve filtrar lançamentos")
	public void deveFiltrarLancamentos()
	{
		
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		// quando executar o findall, retorna a lista com o lançamento definido
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		// execução
		List<Lancamento> resultado = service.buscar(lancamento);
		
		// verificação
		Assertions
		.assertThat(resultado)
		.isNotEmpty()
		.hasSize(1)
		.contains(lancamento);
		
	}
	
	@Test
	@DisplayName("Deve atualizar o status de um lançamento")
	public void deveAtualizarStatusUmLancamento()
	{
		
		// cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		// execução 
		service.atualizarStatus(lancamento, novoStatus);
		
		// verificação
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento); 
		
	}	
	
	@Test
	@DisplayName("Deve obter um lançamento por Id")
	public void deveObterUmlancamentoPorId()
	{
		
		// cenário
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		// execução 
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		// verificação
		Assertions.assertThat(resultado.isPresent()).isTrue(); 
		
	}	
		
	@Test
	@DisplayName("Deve retornar vazio quando o lançamento não existe")
	public void deveRetornarVazioQuandoOLancamentoNaoExiste()
	{
		
		// cenário
		Long id = 1l;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		// execução 
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		// verificação
		Assertions.assertThat(resultado.isPresent()).isFalse(); 
		
	}
	
	@Test
	@DisplayName("deve gerar erro ao validar lançamento")
	public void deveGerarErrosAoValidarLancamento()
	{
		
		// testa todas as possibilidades de valores que podem ser informados e validados no teste
		
		// cenário
		Lancamento lancamento = new Lancamento();
		
		// execução e verificação
		Throwable erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
						
		lancamento.setDescricao("salario");

		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido de 1 a 12.");
		
		lancamento.setMes(13);
		
		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido de 1 a 12.");		
		
		lancamento.setMes(1);

		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(0);		

		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(2021);

		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário para registro do lançamento.");
		
		lancamento.setUsuario(Usuario.builder().email("anderson@teste.com.br").build());
		
		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário para registro do lançamento.");
		
		lancamento.setUsuario(Usuario.builder().email("anderson@teste.com.br").id(1l).build());

		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(-20));
		
		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
			
		lancamento.setValor(BigDecimal.valueOf(50));

		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de lançamento.");

		lancamento.setTipo(null);
		
		erro = Assertions.catchThrowableOfType( () -> service.validar(lancamento), RegraNegocioException.class);
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de lançamento.");
		
	}
	
}
