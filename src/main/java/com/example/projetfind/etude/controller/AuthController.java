// la porte d’entrée de l’API.
package com.example.projetfind.etude.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.projetfind.etude.dto.AuthResponse;
import com.example.projetfind.etude.dto.LoginRequest;
import com.example.projetfind.etude.dto.RegisterRequest;
import com.example.projetfind.etude.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Pour gerer la logique 
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

// la partie inscrition genre POST  /api/auth/register 
@PostMapping("/register")
//transforme le JSON du body en objet RegisterRequest
public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req){
    // pour renvoyer 201 creact plus user genre sans mot de passe
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
}

// partie connexion POST  /api/auth/login
@PostMapping("/login")
public AuthResponse login(@RequestBody LoginRequest req){
    // renvoyer 200 si tt est ok plus info sur user genre sans mot de passe
    return authService.login(req);
}

// mes url 
// POST http://localhost:8080/api/auth/register Pour s'inscrire 

// POST http://localhost:8080/api/auth/login Pour se connecter
}
