// ce que mon baken envoie au frontend genre apres inscription et connexion
// renvoie les infos utilisateur et le token

package com.example.projetfind.etude.dto;

public class AuthResponse {

    // identifiant unique de l'utilisateur
    public Long id;
    // nom complet
    public String name;
    // email
    public String email;
    // pseudo
    public String username;
    // depend si on est user ou admin
    public String role = "USER"; 

    // Jeton d'auth JWT
    public String token;

    // constructeur vide obligatoire pour la sérialisation JSON
    public AuthResponse(){}
    
    public AuthResponse( Long id, String name, String email, String username){
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
    }

}
