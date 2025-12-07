// ce fichier me sert a lire l'entete Authorization: Bearer <token>
// et à vérifier le token via JwtService pour "connecter" l'utilisateur côté Spring Security
package com.example.projetfind.etude.security;


import java.io.IOException; 

import java.util.List;      


import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // objet Authentication simple
import org.springframework.security.core.authority.SimpleGrantedAuthority;            // représentation d'un rôle (ROLE_*)
import org.springframework.security.core.context.SecurityContextHolder;              // où l'on pose l'Authentication


import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.lang.NonNull;



import org.springframework.util.StringUtils;

// API Servlet pour filtrer une requête HTTP
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** JwtAuthFilter : lit l'entête "Authorization: Bearer token si présent → vérifie et extrait le username/role via JwtService */
public class JwtAuthFilter extends OncePerRequestFilter {

    // Référence vers mon service qui fabrique/lit les JWT
    private final JwtService jwt;

    // je l'injecte via le contructeur depuis SecurityConfig
    public JwtAuthFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(
       @NonNull HttpServletRequest req,         // la requête HTTP entrante
       @NonNull HttpServletResponse res,        // la réponse HTTP
       @NonNull FilterChain chain               // permet de passer au filtre suivant
    ) throws ServletException, IOException { // ⚠️ bonnes exceptions à déclarer

        // 1) ne pas filtrer les routes publiques d'authentification
        //    ici: /api/auth/** (register, login)
        String path = req.getRequestURI();


        if (path.startsWith("/api/auth")) {
            // on passe simplement au filtre suivant (ou au contrôleur)
            chain.doFilter(req, res);
            return;
        }

        // 2) pour récupérer l'entête HTTP "Authorization"
        //    attendu sous la forme "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6..."
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);

        // 3) je vérifie qu'il y a quelque chose ET que ça commence par "Bearer "
        //    (avec un espace après Bearer)
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            // 4) pour enlever "Bearer " (7 caractères) et récupérer juste le token
            String token = header.substring(7);

            try {
                // 5) pour lire les infos depuis le token en vérifiant la signature et l'expiration
                String role = jwt.getRole(token);         // "ADMIN" ou "USER" (claim "role")
                String username = jwt.getUsername(token); // ex: "mai" (claim "username")

                // 6) construire un objet Authentication minimal :
                //    - principal = username (qui est "connecté")
                //    - credentials = null (on n'a pas le mot de passe ici)
                //    - authorities = la liste des rôles Spring → "ROLE_ADMIN" / "ROLE_USER"
                var auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                // 7) déposer l'authentification dans le contexte de sécurité
                //    (les contrôleurs pourront accéder à Authentication)
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // 8) si token invalide/expiré → on nettoie (pas d'utilisateur authentifié)
                SecurityContextHolder.clearContext();
            }
        }

        // 9) dans tous les cas, on continue la chaîne de filtres
        chain.doFilter(req, res);
    }
}
