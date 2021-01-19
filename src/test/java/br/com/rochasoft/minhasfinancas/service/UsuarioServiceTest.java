package br.com.rochasoft.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.rochasoft.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.rochasoft.minhasfinancas.exception.RegraNegocioException;
import br.com.rochasoft.minhasfinancas.model.entity.Usuario;
import br.com.rochasoft.minhasfinancas.model.repository.UsuarioRepository;
import br.com.rochasoft.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") 
public class UsuarioServiceTest 
{

	// @Autowired // o spring irá injetar uma instancia desta variável que estiver no container
	// não precisou mais injetar porque vai utilizar instancias fake (mockito)
	@SpyBean
	UsuarioServiceImpl	service;
	
	@MockBean
	UsuarioRepository 	repository; // já cria uma instância fake
	
	@Test
	@DisplayName("Deve salvar um usuário")
	public void deveSalvarUmUsuario()
	{
		
		// cenário
		// não faz nada quando executar o método 'validarEmail' do service
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				.id(1l)
				.nome("nome")
				.email("usuario@email.com")
				.senha("senha")
				.build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		// ação
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		// verificação
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("usuario@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		
	}
	
	@Test
	@DisplayName("Não deve salvar um usuário com email já cadastrado")
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado()
	{
		
		// cenário
		String email = "usuario@email.com";
		Usuario usuario = Usuario.builder()
				.email(email)
				.build();
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
				
		// ação
        Throwable exception = Assertions.catchThrowable( () -> service.salvarUsuario(usuario));
	
		// verificação
        // tem que ter gerado uma exceção
        Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class);		
		// nunca pode ter chamado o método 'save'        
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
	}	
	
	@Test
	@DisplayName("Deve validar o e-mail")
	public void deveValidarEmail()
	{
		
		// cenário
		// repository.deleteAll(); // cria um cenário fake
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		// ação
		Throwable exception = Assertions.catchThrowable( () -> service.validarEmail("email@email.com.br"));
		
		// verificação (não pode gerar erro)
        Assertions.assertThat(exception).isNull();				
		
	}
	
	@Test
	@DisplayName("Deve gerar erro quando existir e-mail cadastrado")
	public void deveGerarErroAoValidarEmailQuandoExistirEmailCadastrado()
	{
		
		// cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		// ação
        Throwable exception = Assertions.catchThrowable( () -> service.validarEmail("email@email.com.br"));

        // verifica se o erro foi gerado (usuario já existe)
        Assertions.assertThat(exception)
                  .isInstanceOf(RegraNegocioException.class);
        		
		
	}	
	
	@Test
	@DisplayName("Deve auttenticar um usuário com sucesso")
	public void deveAutenticarUmUsuarioComSucesso()
	{
		
		// cenário
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		// ação
		Usuario result = service.autenticar(email, senha);
		
		// verificação
		Assertions.assertThat(result).isNotNull();
		
	}
	
	@Test
	@DisplayName("Deve lançar erro quando não encontrar um usuário com o email informado")
	public void deveLancarErroQuandoNaoEncontrarUsuarioEmailInformado()
	{
		
		// cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
			
		// ação
        Throwable exception = Assertions.catchThrowable( () -> service.autenticar("usuario@email.com", "senha") );

        // tem que gerar erro
        Assertions.assertThat(exception)
                  .isInstanceOf(ErroAutenticacaoException.class)
                  .hasMessage("Usuário não encontrado.");
				
	}
		
	@Test
	@DisplayName("Deve lançar erro quando a senha informada for incorreta")
	public void deveLancarErroQuandoSenhaEstiverIncorreta()
	{
		
		// cenário
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
			
		// ação
        Throwable exception = Assertions.catchThrowable( () -> service.autenticar("usuario@email.com", "123") );

        // tem que gerar erro
        Assertions.assertThat(exception)
                  .isInstanceOf(ErroAutenticacaoException.class)
                  .hasMessage("Senha inválida.");
        	      		
				
	}

}

/*
teste utilizando os objetos reais
// cenário
Usuario usuario = Usuario.builder().nome("usuario").email("email@email.com.br").build();
repository.save(usuario);

// ação
Throwable exception = Assertions.catchThrowable( () -> service.validarEmail("email@email.com.br"));

// verifica se o erro foi gerado (usuario já existe)
Assertions.assertThat(exception)
          .isInstanceOf(RegraNegocioException.class);
*/		
