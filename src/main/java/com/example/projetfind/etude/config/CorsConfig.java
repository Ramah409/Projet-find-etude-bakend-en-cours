// ce fichier me sert a autoriser le front angular

package com.example.projetfind.etude.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.lang.NonNull;


@Configuration
public class CorsConfig implements WebMvcConfigurer{

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry){
        // toutes les routes qui commence par /api/ (metier)
        registry.addMapping("/api/**")
               // pour autoriser uniquement angular en local et internet en gros deployer netlify
               // Sans cette ligne, le navigateur bloque les requêtes (erreur CORS).
                .allowedOrigins("http://localhost:4200", "https://soramtfr-projet.netlify.app")
                // les methodes autoriser
                .allowedMethods("GET","POST","PUT", "DELETE", "OPTIONS")
                // pour autoriser tous les header HTTP
                .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")
                // utile si je renvoies cet header
                .exposedHeaders("Authorization")
                // pour autoriser les cookies ou sessions 
                .allowCredentials(true);


         // mes routes d'authentification genre se connecter ou crée un compte (register et login) AVEC le /api devant
    registry.addMapping("/api//auth/**")
    // l'autorisation sur la partie front en local et sur netlify
    .allowedOrigins("http://localhost:4200", "https://soramfr-projet.netlify.app")
    // les methodes http
    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    .allowedHeaders("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With")
    .allowCredentials(true);

    }

    

}
