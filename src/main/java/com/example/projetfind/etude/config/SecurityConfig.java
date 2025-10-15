// ce fichier me sert a autoriser les requêtes et à brancher la sécurité Web (CORS/CSRF/JWT)
package com.example.projetfind.etude.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.projetfind.etude.security.JwtAuthFilter;  // ← ton filtre que tu viens d’écrire
import com.example.projetfind.etude.security.JwtService;    // ← notre “cuisine” à tokens

@Configuration
public class SecurityConfig {

    /**
     * Déclare le "plan" de sécurité HTTP.
     * Spring appelle cette méthode au démarrage et applique ce que tu définis :
     * - CORS activé (pour laisser Angular 4200 appeler l'API)
     * - CSRF désactivé (API stateless avec JWT)
     * - Insertion de NOTRE filtre JWT
     * - Règles : quelles routes sont publiques, lesquelles exigent un token
     *
     * ⚠️ Note : on récupère un JwtService en paramètre → Spring l’injecte automatiquement.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwt) throws Exception {
        http
            // 1) CORS activé côté sécurité (les origines/headers autorisés sont définis dans CorsConfig)
            .cors(cors -> { /* rien à faire ici, on laisse CorsConfig décider */ })
            // 2) CSRF désactivé : on n’est pas sur des formulaires HTML, on utilise des jetons (JWT)
            .csrf(csrf -> csrf.disable())

            // JWT  genre pas de session serveur
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                
            // 3) On insère NOTRE filtre JWT AVANT le filtre standard de Spring Security
            //    → ainsi, si un header Authorization: Bearer <token> est présent, on pose l’Authentication.
            .addFilterBefore(new JwtAuthFilter(jwt), UsernamePasswordAuthenticationFilter.class)

            // 4) Règles d’autorisation : qui peut appeler quoi ?
            .authorizeHttpRequests(auth -> auth

                // a) Toujours autoriser les requêtes OPTIONS (pré-vol CORS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // b) Routes d’authentification publiques (inscription / connexion)
                //    → le front n’a PAS de token au moment de s’inscrire/se connecter
                .requestMatchers("/api/auth/**").permitAll()

                // c) Exemple : toutes les autres routes “métier” sous /api/** exigent d’être authentifié
                //    (si tu veux affiner plus tard par rôle, on ajoutera hasRole/hasAnyRole)
                .requestMatchers("/api/**").authenticated()

                // d) Tout le reste est aussi protégé (par défaut)
                .anyRequest().authenticated()
            );

        // 5) On “construit” la chaîne de filtres configurée
        return http.build();
    }
}
