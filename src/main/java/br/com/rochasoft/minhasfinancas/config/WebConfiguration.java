package br.com.rochasoft.minhasfinancas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfiguration implements WebMvcConfigurer
{
	
	@Override
	public void addCorsMappings(CorsRegistry registry) 
	{
		
		// habilita de onde as requisições ao servidor serão aceitas
		// ficou liberado para qualquer caminho, mas pode definir as origens permitidas
		registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
		
	}	

}
