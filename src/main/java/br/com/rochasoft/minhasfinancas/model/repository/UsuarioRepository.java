package br.com.rochasoft.minhasfinancas.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rochasoft.minhasfinancas.model.entity.Usuario;

// extendendo a classe 'jparepository' já temos a implementação dos métodos default
public interface UsuarioRepository extends JpaRepository<Usuario, Long> 
{
	
	// optional indica que irá retornar um objeto tipo 'Optional' podendo conter ou não um usuário
    // findByEmail é um 'QueryMethod' > 'findBy' + propriedade 'email'
	// não precisa implementar, porque o já irá implementar automaticamente
	// Optional<Usuario> findByEmail(String email);
	
	// queryMethod que verifica se existe um Usuario com o email informado
    boolean existsByEmail(String email);
    
    // queryMethod
    Optional<Usuario> findByEmail(String email);

}
