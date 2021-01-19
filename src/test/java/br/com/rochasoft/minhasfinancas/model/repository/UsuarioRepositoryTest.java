package br.com.rochasoft.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.rochasoft.minhasfinancas.model.entity.Usuario;

// migração junit4 para junit5
// @RunWith para @ExtendWith(SpringExtension.class)
// @Test de org.junit.jupiter.api.Test;

// teste de integração
// precisa de recursos externos a aplicação
// pra rodar precisa do banco de dados
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") // inclui esta diretiva para usar as definições de 'application-test.properties' nos testes, com banco h2
@DataJpaTest // cria a instância do banco de dados em memória e depois apaga a instância - sempre inicia a transação, depois do teste, faz o rollback para não influenciar nos outros testes
@AutoConfigureTestDatabase(replace = Replace.NONE) // não sobscreve as configurações do banco em memória definida em 'application-test.properties'
public class UsuarioRepositoryTest 
{
	
	// declara a classe que estarei testando
	@Autowired // para que o contexto do springBoot crie uma instância e injete para execução do teste
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager; // classe responsável por fazer as alterações na base de dados
	
	@Test
	@DisplayName("verifica se um email já existe na base de dados")
	public void deveVerificarExistenciaEmail()
	{
		
		// cenário
		// cria o usuario e grava no banco de dados
		String email    = "usuario@email.com";
		Usuario usuario = Usuario.builder().nome("usuario").email(email).build();
		entityManager.persist(usuario);
		
		// execução
		boolean result  = repository.existsByEmail(email);
		
		// verificação
		Assertions.assertThat(result).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve retornar falso quando não houver usuário cadastrado com o e-mail")
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoEmail()
	{
		
		// cenário
		// cria o usuario e grava no banco de dados
		// repository.deleteAll(); não precisa mais, porque os testes executados não serão 'comitados'
		
		// execução
		boolean result  = repository.existsByEmail("usuario@email.com");
		
		// verificação
		Assertions.assertThat(result).isFalse();
		
	}
	
	@Test
	@DisplayName("Deve incluir um novo usuário na base de dados")
	public void devePersistirUmUsuarioBaseDados()
	{
		
		// cenário
		Usuario usuario = criarUsuario();
		
		// ação
		Usuario usuarioSalvo = repository.save(usuario);
		
		// verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	@Test
	@DisplayName("Deve buscar usuário por email")
	public void deveBuscarUsuarioPorEmail()
	{
		
		// cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		// ação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		// verificação
		Assertions.assertThat(result.isPresent()).isTrue();

	}	
	
	@Test
	@DisplayName("Deve retornar vazio ao buscar usuário por email quando não existe na base")
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase()
	{
		
		// cenário
		// base limpa
		
		// ação
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		// verificação
		Assertions.assertThat(result.isPresent()).isFalse();

	}		

	public static Usuario criarUsuario()
	{
		
		Usuario usuario = Usuario.builder()
				.nome("usuario")
				.email("usuario@email.com")
				.senha("senha")
				.build();
		
		return usuario;
	}
	
}
