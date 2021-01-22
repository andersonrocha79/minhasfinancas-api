package br.com.rochasoft.minhasfinancas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/*

	links uteis
	https://docs.spring.io/spring-boot/docs/1.4.x/reference/html/common-application-properties.html
	https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-sql
	https://projectlombok.org/  (para instalar basta baixar o .jar e executá-lo) se for no intelij basta incluir o plugin no pom.xml
	
	eclipse
	https://www.eclipse.org/downloads/
	
	criação do projeto maven spring boot 
	https://start.spring.io/
	
	link do banco de dados postgre
	https://www.postgresql.org/download/
	
	aplicação no github
	https://github.com/cursodsousa/minhas-financas-app
	https://github.com/cursodsousa/minhasfinancas-api
	
	administração do banco postgre
	http://127.0.0.1:50142/browser/#

	documentação do JPA
	https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html
	
	beans e injeção de dependência do spring
	https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/beans.html
	
	testes no spring
	https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#testing-introduction
	
*/


@SpringBootApplication
public class MinhasfinancasApplication 
{
	
	public static void testandoLiveReload()
	{
		System.out.print("live reload funcionando...");
	}

	public static void main(String[] args) 
	{
		testandoLiveReload();
		SpringApplication.run(MinhasfinancasApplication.class, args);
	}

}
