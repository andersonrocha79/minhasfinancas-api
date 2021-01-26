package br.com.rochasoft.minhasfinancas.api.resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.rochasoft.minhasfinancas.api.dto.UsuarioDTO;
import br.com.rochasoft.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.rochasoft.minhasfinancas.exception.RegraNegocioException;
import br.com.rochasoft.minhasfinancas.model.entity.Usuario;
import br.com.rochasoft.minhasfinancas.service.LancamentoService;
import br.com.rochasoft.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") 
@WebMvcTest(controllers = UsuarioResource.class) // especifica para RestController (suba o contexto rest para testar o controller)
@AutoConfigureMockMvc
public class UsuarioResourceTest 
{
	
	static final String API = "/api/usuarios";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	@DisplayName("Deve autenticar um usuário")
	public void deveAutenticarUmUsuario() throws Exception
	{
		
		// cenário
		
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).nome("anderson").build();
		
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).nome("anderson").build();
		
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
									.post(API.concat("/autenticar"))
									.accept(MediaType.APPLICATION_JSON) // aceita json
									.contentType(MediaType.APPLICATION_JSON) // envia json
									.content(json);
											
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId())) 
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}

	@Test
	@DisplayName("Deve retornar badRequest ao obter erro de autenticacao")
	public void deveRetornarBadRequestErroAutenticacao() throws Exception
	{
		
		// cenário
		
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).nome("anderson").build();
			
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
									.post(API.concat("/autenticar"))
									.accept(MediaType.APPLICATION_JSON) // aceita json
									.contentType(MediaType.APPLICATION_JSON) // envia json
									.content(json);
											
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}
	
	@Test
	@DisplayName("Deve criar um novo usuário")
	public void deveCriarUmNovoUsuario() throws Exception
	{
		
		// cenário
		
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).nome("anderson").build();
		
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).nome("anderson").build();
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
									.post(API)
									.accept(MediaType.APPLICATION_JSON) // aceita json
									.contentType(MediaType.APPLICATION_JSON) // envia json
									.content(json);
											
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId())) 
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}	

	@Test
	@DisplayName("Deve retornar um BadRequest ao tentar criar um usuario invalido")
	public void deveRetornarBadRequestAoTentarCriarUsuarioInvalido() throws Exception
	{
		
		// cenário
		
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).nome("anderson").build();
		
	
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		// execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
									.post(API)
									.accept(MediaType.APPLICATION_JSON) // aceita json
									.contentType(MediaType.APPLICATION_JSON) // envia json
									.content(json);
											
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}	
	
}
